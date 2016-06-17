package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.model.GraphNode;

/**
 * Abstract repository that provides access to node display properties.
 */
public interface NodeDisplayRepository {

  public static interface ChangeListener {
    void nodeDisplayChanged(GraphNode node, NodeDisplayProperty prop);
  }

  /**
   * Provide the {@link NodeDisplayProperty} for the supplied {@link GraphNode}.
   */
  NodeDisplayProperty getDisplayProperty(GraphNode node);

  /**
   * Set the {@link EdgeDisplayProperty} for the supplied {@link GraphNode}.
   */
  void setDisplayProperty(GraphNode node, NodeDisplayProperty prop);

  void addChangeListener(ChangeListener listener);

  void removeChangeListener(ChangeListener listener);
}