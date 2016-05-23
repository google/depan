package com.google.devtools.depan.graph.registry;

import com.google.devtools.depan.graph.api.Relation;

import java.util.Collection;

public interface RelationContributor {

  Collection<? extends Relation> getRelations();
}
