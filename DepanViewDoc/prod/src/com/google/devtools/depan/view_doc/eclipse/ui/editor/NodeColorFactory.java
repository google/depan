package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;
import com.google.devtools.depan.eclipse.visualization.ogl.ColorMap;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.DirectedGraph;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NodeColorFactory {

  private GraphModel exposedGraph;

  private Collection<GraphNode> movables;

  /** Used for rendering and ranking in the rending pipe. */
  private DirectedGraph<GraphNode, GraphEdge> jungGraph;

  private Map<GraphNode, Double> ranking;

  private int maxDegree;

  private Double maxRank;

  public NodeColorFactory(
      GraphModel exposedGraph, Collection<GraphNode> movables) {
    this.exposedGraph = exposedGraph;
    this.movables = movables;
  }

  public void buildJungGraph() {
    LayoutContext context = new LayoutContext();
    context.setGraphModel(exposedGraph);
    context.setMovableNodes(movables);
    // TODO: Compute ranking based on selected edge matcher
    context.setEdgeMatcher(com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors.FORWARD);

    jungGraph = LayoutUtil.buildJungGraph(context);
    ranking = rankGraph();
    maxDegree = calcMaxDegree();
    maxRank = calcMaxRank();
  }

  public Object getJungGraph() {
    return jungGraph;
  };

  private Map<GraphNode, Double> rankGraph() {

    Double unit = 1.0;
    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : exposedGraph.getNodes()) {
      result.put(node, unit);
    }
    return result;
  }

  private int calcMaxDegree() {
    int result = 0;

    for (GraphNode node : exposedGraph.getNodes()) {
      int degree = jungGraph.getPredecessorCount(node);
      result = Math.max(result, degree);
    }
    return result;
  }

  private Double calcMaxRank() {
    Double result = 0.0;

    for (Double rank : ranking.values()) {
      if (null != rank) {
        result = Math.max(result, rank.doubleValue());
      }
    }

    return result;
  }

  /**
   * Associate to each node a value based on it's "importance" in the graph.
   * The selected algorithm is a Page Rank algorithm.
   *
   * The result is stored in the map {@link #ranking}.
   */
  @SuppressWarnings("unused") // Retained legacy code
  private Map<GraphNode, Double> rankGraphX(
          DirectedGraph<GraphNode, GraphEdge> graph) {

    KStepMarkov<GraphNode, GraphEdge> ranker =
        new KStepMarkov<GraphNode, GraphEdge>(graph, null, 6, null);
    ranker.setRemoveRankScoresOnFinalize(false);
    ranker.evaluate();

    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : exposedGraph.getNodes()) {
      result.put(node, ranker.getVertexRankScore(node));
    }

    return result;
  }

  public List<GraphNode> getRoots() {
    List<GraphNode> result = Lists.newArrayList();
    for (Entry<GraphNode, Double> entry : ranking.entrySet()) {
      if (0.0 == entry.getValue()) {
        result.add(entry.getKey());
      }
    }
    return result;
  }

  /////////////////////////////////////
  // Jung based color supplier

  private static class JungStatsColorSupplier implements NodeColorSupplier {

    private final float voltagePercent;
    private final float degreePercent;

    public JungStatsColorSupplier(float voltagePercent, float degreePercent) {
      this.voltagePercent = voltagePercent;
      this.degreePercent = degreePercent;
    }

    @Override
    public Color getStrokeColor(
        NodeColors nodeColors, GraphNode node, ColorMap cm ) {
      return Color.blue;
    }

    @Override
    public Color getFillColor(
        NodeColors nodeColors, GraphNode node, ColorMap cm ) {
      return nodeColors.getColor(node, cm, voltagePercent, degreePercent);
    }
  }

  public NodeColorSupplier getColorSupplier(GraphNode node) {
    float voltagePercent = (float) (ranking.get(node) / maxRank);
    float degreePercent = jungGraph.getPredecessorCount(node) / maxDegree;

    return new JungStatsColorSupplier(voltagePercent, degreePercent);
  };

  /////////////////////////////////////
  // Jung based color supplier

  private static class JungStatsSizeSupplier implements NodeSizeSupplier {

    private final NodeSize nodeSize;
    private final float voltagePercent;
    private final float degreePercent;

    public JungStatsSizeSupplier(
        NodeSize nodeSize, float voltagePercent, float degreePercent) {
      this.nodeSize = nodeSize;
      this.voltagePercent = voltagePercent;
      this.degreePercent = degreePercent;
    }

    @Override
    public float getSize() {
      return nodeSize.getSize(voltagePercent, degreePercent);
    }

    @Override
    public float getOverridenSize(NodeSize overrideSize) {
      return overrideSize.getSize(voltagePercent, degreePercent);
    }
  }

  public NodeSizeSupplier getSizeSupplier(GraphNode node) {
    float voltagePercent = (float) (ranking.get(node) / maxRank);
    float inDegree = jungGraph.getPredecessorCount(node);
    float inPercent = (float) (inDegree  / maxDegree);

    return new JungStatsSizeSupplier(
        NodeSize.getDefault(), voltagePercent, inPercent);
  };

  /////////////////////////////////////
  // Jung based ratio supplier

  public NodeRatioSupplier getRatioSupplier(GraphNode node) {
    float inDegree = jungGraph.getPredecessorCount(node);
    float outDegree = jungGraph.getSuccessorCount(node);
    float ratio = (float) ((inDegree + outDegree) / maxDegree);

    return new NodeRatioSupplier.Simple(ratio);
  };

  /////////////////////////////////////
  // Jung based shape supplier

  private static class JungStatsShapeSupplier implements NodeShapeSupplier {

    private final NodeShape mode;
    private int degree;

    public JungStatsShapeSupplier(NodeShape mode, int degree) {
      this.mode = mode;
      this.degree = degree;
    }

    @Override
    public GLEntity getShape() {
      return mode.getShape(degree, node);
    }
  }

  public NodeShapeSupplier getShapeSupplier(GraphNode node, NodeShape mode) {
    float inDegree = jungGraph.getPredecessorCount(node);
    float outDegree = jungGraph.getSuccessorCount(node);
    float ratio = (float) ((inDegree + outDegree) / maxDegree);

    return new JungStatsShapeSupplier(ratio);
  }
}
