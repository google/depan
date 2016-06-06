package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.graph.api.Relation;

/**
 * Abstract repository that provides access to the relation selection.
 */
public interface RelationSetRepository {

  public static interface ChangeListener {
    void includedRelationChanged(Relation relation, boolean visible);
  }

  /**
   * Indicate whether the supplied {@code relation} is included
   * in the relation set.
   */
  boolean isRelationIncluded(Relation relation);

  /**
   * Set whether the supplied {@link Relation} is included in
   * the relation set.
   */
  void setRelationChecked(Relation relation, boolean isIncluded);

  void addChangeListener(ChangeListener listener);

  void removeChangeListener(ChangeListener listener);
}