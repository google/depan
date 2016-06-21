package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;

/**
 * Abstract repository that provides access to the relation selection.
 */
public interface RelationSetRepository {

  public static interface ChangeListener {

    /**
     * A single relation changed status to the supplied state.
     */
    void includedRelationChanged(Relation relation, boolean included);

    /**
     * Multiple relations changed state.  Query the repository for
     * the current states.
     */
    void relationsChanged();
  }

  public static interface ProvidesUniverse {
    Collection<Relation> getUniverse();
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