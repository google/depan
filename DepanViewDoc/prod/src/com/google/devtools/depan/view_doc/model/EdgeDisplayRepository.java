package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.model.GraphEdge;

/**
 * Abstract repository that provides access to edge display properties.
 */
public interface EdgeDisplayRepository {

  public static interface ChangeListener {
    void edgeDisplayChanged(GraphEdge edge, EdgeDisplayProperty prop);
  }

  /**
   * Provide the {@link EdgeDisplayProperty} for the supplied {@link GraphEdge}.
   */
  EdgeDisplayProperty getDisplayProperty(GraphEdge edge);

  /**
   * Set the {@link EdgeDisplayProperty} for the supplied {@link GraphEdge}.
   */
  void setDisplayProperty(GraphEdge edge, EdgeDisplayProperty prop);

  void addChangeListener(ChangeListener listener);

  void removeChangeListener(ChangeListener listener);
}