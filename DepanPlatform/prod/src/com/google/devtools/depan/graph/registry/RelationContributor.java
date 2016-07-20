package com.google.devtools.depan.graph.registry;

import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;

public interface RelationContributor {

  /**
   * A human-sensible identifier for this collection of {@link Relation}s.
   * The label is often shared with a {@code NodeContributor}.
   */
  String getLabel();

  Collection<? extends Relation> getRelations();
}
