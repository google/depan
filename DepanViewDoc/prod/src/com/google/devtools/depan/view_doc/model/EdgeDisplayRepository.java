package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.graph.api.Relation;

/**
 * Abstract repository that provides access to edge display properties.
 */
public interface EdgeDisplayRepository {

  public static interface ChangeListener {
    void edgeDisplayChanged(Relation relation, EdgeDisplayProperty props);
  }

  /**
   * Provide the {@link EdgeDisplayProperty} for the supplied {@link Relation}.
   */
  EdgeDisplayProperty getDisplayProperty(Relation relation);

  /**
   * Set the {@link EdgeDisplayProperty} for the supplied {@link Relation}.
   */
  void setDisplayProperty(Relation relation, EdgeDisplayProperty props);

  void addChangeListener(ChangeListener listener);

  void removeChangeListener(ChangeListener listener);
}