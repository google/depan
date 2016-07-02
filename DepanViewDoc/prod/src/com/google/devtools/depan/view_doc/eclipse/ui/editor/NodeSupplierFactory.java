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
import com.google.devtools.depan.model.GraphNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NodeSupplierFactory {

  private Collection<GraphNode> nodes;

  /** Used for rendering and ranking in the rending pipe. */
  private DirectedGraph<GraphNode, GraphEdge> jungGraph;

  private Map<GraphNode, Double> ranking;

  private int maxDegree;

  private Double maxRank;

  public NodeSupplierFactory(
      Collection<GraphNode> nodes,
      DirectedGraph<GraphNode, GraphEdge> jungGraph) {
    this.nodes = nodes;
    this.jungGraph = jungGraph;
    ranking = rankGraph();
    maxDegree = calcMaxDegree();
    maxRank = calcMaxRank();
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  };

  public Graph<GraphNode, GraphEdge> getJungGraph() {
    return jungGraph;
  };

  private Map<GraphNode, Double> rankGraph() {

    Double unit = 1.0;
    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : nodes) {
      result.put(node, unit);
    }
    return result;
  }

  private int calcMaxDegree() {
    int result = 0;

    for (GraphNode node : nodes) {
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
    for (GraphNode node : nodes) {
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

  /**
   * In a completely disconnected graph, maxRank and maxDegree can both
   * be zero.  Things should still render properly.
   */
  private static float divSafe(float num, float den, float alt) {
    if (den == 0.0) {
      return alt;
    }
    return num / den;
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
    float voltagePercent = divSafe(
        ranking.get(node).floatValue(), maxRank.floatValue(), 0.0f);
    float degreePercent = divSafe(
        jungGraph.getPredecessorCount(node), maxDegree, 0.0f);

    return new JungStatsColorSupplier(voltagePercent, degreePercent);
  };

  /////////////////////////////////////
  // Jung based color supplier

  private static class JungStatsSizeSupplier implements NodeSizeSupplier {

    private final float fixedSize;
    private final float voltagePercent;
    private final float degreePercent;

    public JungStatsSizeSupplier(
        float fixedSize, float voltagePercent, float degreePercent) {
      this.fixedSize = fixedSize;
      this.voltagePercent = voltagePercent;
      this.degreePercent = degreePercent;
    }

    @Override
    public float getSize() {
      return fixedSize;
    }

    @Override
    public float getOverridenSize(NodeSize overrideSize) {
      return overrideSize.getSize(voltagePercent, degreePercent);
    }
  }

  public NodeSizeSupplier getSizeSupplier(GraphNode node, NodeSize nodeSize) {
    float voltagePercent = divSafe(
        ranking.get(node).floatValue(), maxRank.floatValue(), 0.0f);
    float inDegree = jungGraph.getPredecessorCount(node);
    float degreePercent = divSafe(inDegree, maxDegree, 0.0f);
    float fixedSize = nodeSize.getSize(voltagePercent, degreePercent);

    return new JungStatsSizeSupplier(fixedSize, voltagePercent, degreePercent);
  };

  /////////////////////////////////////
  // Jung based ratio supplier

  public NodeRatioSupplier getRatioSupplier(GraphNode node) {
    float inDegree = jungGraph.getPredecessorCount(node);
    float outDegree = jungGraph.getSuccessorCount(node);
    float ratio = divSafe(inDegree + outDegree, maxDegree, 0.0f);

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
    public GLEntity getShape(GraphNode node) {
      return mode.getShape(degree, node);
    }
  }

  public NodeShapeSupplier getShapeSupplier(GraphNode node, NodeShape mode) {
    int degree = jungGraph.getPredecessorCount(node);

    return new JungStatsShapeSupplier(mode, degree);
  }
}
