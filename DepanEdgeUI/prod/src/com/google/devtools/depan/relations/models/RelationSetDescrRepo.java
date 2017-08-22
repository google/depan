/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.edge_ui.EdgeUILogger;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ListenerManager.Dispatcher;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

/**
 * A {@link RelationSetRepository} based on a {@link RelationSet}.
 * Useful for editing a {@link RelationSet}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSetDescrRepo
    implements RelationSetRepository {

  private final Collection<Relation> universe;

  private RelationSet relSet;

  private Set<Relation> updates;

  ListenerManager<ChangeListener> listeners =
      new ListenerManager<ChangeListener>();

  private abstract static class LoggingDispatcher
      implements Dispatcher<ChangeListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      EdgeUILogger.LOG.error(
          "Exception during RelationSet update", errAny);
    }
  };

  public RelationSetDescrRepo(Collection<Relation> universe) {
    this.universe = universe;
  }

  public void setRelationSet(RelationSet relSet) {
    this.relSet = relSet;
    updates = null;

    listeners.fireEvent(new LoggingDispatcher() {
      @Override
      public void dispatch(ChangeListener listener) {
        listener.relationsChanged();
      }
    });
  }

  public Set<Relation> getUpdateSet() {
    return updates;
  }

  public RelationSet getRelationSet() {
    if (null == updates) {
      return relSet;
    }

    return RelationSets.createSimple(updates);
  }

  @Override
  public boolean isRelationIncluded(Relation relation) {
    if (null != updates) {
      return updates.contains(relation);
    }

    return relSet.contains(relation);
  }

  @Override
  public void setRelationChecked(
      final Relation relation, final boolean isIncluded) {

    boolean nowVisible = isRelationIncluded(relation);
    if (nowVisible == isIncluded) {
      return;
    }

    ensureUpdatable();
    if (isIncluded) {
      updates.add(relation);
    } else {
      updates.remove(relation);
    }

    listeners.fireEvent(new LoggingDispatcher() {
      @Override
      public void dispatch(ChangeListener listener) {
        listener.includedRelationChanged(relation, isIncluded);
      }
    });
  }

  private void ensureUpdatable() {
    if (null != updates) {
      return;
    }
    updates = buildIncludedRelations();
  }

  private Set<Relation> buildIncludedRelations() {
    Set<Relation> result = Sets.newHashSet();
    for (Relation relation : universe) {
      if (isRelationIncluded(relation))
        result.add(relation);
    }
    return result;
  }


  @Override
  public void addChangeListener(ChangeListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public void removeChangeListener(ChangeListener listener) {
    listeners.removeListener(listener);
  }
}
