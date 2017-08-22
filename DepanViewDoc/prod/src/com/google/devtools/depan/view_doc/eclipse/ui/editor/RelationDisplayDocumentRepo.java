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

package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ListenerManager.Dispatcher;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.RelationDisplayTableControl;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.RelationDisplayRepository;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Provide an {@link RelationDisplayRepository} based on an
 * {@link RelationDisplayDocument}.  The supports integration with the
 * standard {@link RelationDisplayTableControl} for {@link Relation}
 * grained changes to edge rendering properties.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class RelationDisplayDocumentRepo
    implements RelationDisplayRepository {

  private final Collection<Relation> universe;

  private Map<Relation, EdgeDisplayProperty> edgeProps;

  private Map<Relation, EdgeDisplayProperty> updates;

  ListenerManager<ChangeListener> listeners =
      new ListenerManager<ChangeListener>();

  private abstract static class LoggingDispatcher
      implements Dispatcher<ChangeListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      PersistenceLogger.LOG.error(
          "Exception during EdgeDisplay document update", errAny);
    }
  };

  public RelationDisplayDocumentRepo(Collection<Relation> universe) {
    this.universe = universe;
  }

  public void setEdgeDisplayProperties(
      Map<Relation, EdgeDisplayProperty> edgeProps) {
    this.edgeProps = edgeProps;
    updates = null;
  }

  public Map<Relation, EdgeDisplayProperty> getUpdates() {
    return updates;
  }

  public Map<Relation, EdgeDisplayProperty> getEdgeDisplayDocument() {
    if (null == updates) {
      return edgeProps;
    }

    Map<Relation, EdgeDisplayProperty> result =
        Maps.newHashMap(edgeProps);
    result.putAll(updates);
    return result;
  }

  @Override
  public EdgeDisplayProperty getDisplayProperty(Relation relation) {
    if (null != updates) {
      return updates.get(relation);
    }

    return edgeProps.get(relation);
  }

  @Override
  public void setDisplayProperty(
      final Relation relation, final EdgeDisplayProperty prop) {

    ensureUpdatable();
    if (null != prop) {
      updates.put(relation, prop);
    } else {
      updates.remove(relation);
    }

    listeners.fireEvent(new LoggingDispatcher() {
      @Override
      public void dispatch(ChangeListener listener) {
        listener.edgeDisplayChanged(relation, prop);
      }
    });
  }

  private void ensureUpdatable() {
    if (null != updates) {
      return;
    }
    updates = buildUpdates();
  }

  private Map<Relation, EdgeDisplayProperty> buildUpdates() {
    Map<Relation, EdgeDisplayProperty> result = Maps.newHashMap();
    for (Relation relation : universe) {
      EdgeDisplayProperty prop = getDisplayProperty(relation);
      if (null != prop) {
        result.put(relation, prop);
      }
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
