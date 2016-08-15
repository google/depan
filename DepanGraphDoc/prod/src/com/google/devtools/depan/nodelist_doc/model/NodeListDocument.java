package com.google.devtools.depan.nodelist_doc.model;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IResource;

import java.util.Collection;

/**
 * Similar to a ViewDoc, but none of the rendering preferences.
 * 
 * This document type is available without UX components.
 * 
 * @author Lee Carver
 */
public class NodeListDocument {
  /**
   * Standard extension to use when loading or saving {@code NodeListDocument}s.
   * The characters represent "DepAn View Info".
   */
  public static final String EXTENSION = "dni";

  private final GraphModelReference parentGraph;

  private final Collection<GraphNode> nodes;

  public NodeListDocument(
      GraphModelReference parentGraph, Collection<GraphNode> nodes) {
    this.parentGraph = parentGraph;
    this.nodes = nodes;
  }

  /**
   * Provide a copy of the collection of nodes in this document.
   */
  public Collection<GraphNode> getNodes() {
    return ImmutableList.copyOf(nodes);
  }

  public GraphDocument getGraphDocument() {
    return parentGraph.getGraph();
  }

  /**
   * Provide the location where the parent {@link GraphModelReference} is
   * stored.  Typically, the {@link NodeListDocument} is stored near by.
   */
  public IResource getReferenceLocation() {
    return parentGraph.getLocation();
  }

  /**
   * Provide the complete graph from the {@link GraphModelReference}.
   */
  public GraphModel getGraphModel() {
    return getGraphDocument().getGraph();
  }

  /**
   * Provide the {@link DependencyModel} from the {@link GraphModelReference}.
   */
  public DependencyModel getDependencyModel() {
    return getGraphDocument().getDependencyModel();
  }

  /**
   * Provide the imputed graph based on the edges from the
   * {@link GraphModelReference} and these {@link #nodes}.
   */
  public GraphModel getNodeListGraph() {
    GraphModel master = parentGraph.getGraph().getGraph();
    return GraphBuilders.buildFromNodes(master, nodes);
  }
}
