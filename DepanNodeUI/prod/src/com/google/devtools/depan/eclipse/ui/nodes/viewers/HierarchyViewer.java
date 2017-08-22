/*
 * Copyright 2013 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.NodesUILogger;
import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSelectorControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * A composite control for obtaining a user selected hierarchy.
 *
 * Listeners are notified whenever the hierarchy is changed.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class HierarchyViewer<T> extends Composite {

  private HierarchyCache<T> hierarchies;

  private EdgeMatcherSelectorControl edgeMatcherSelector;

  /** Listener when the selection change. */
  // private List<HierarchyChangeListener> listeners = Lists.newArrayList();

  private ListenerManager<HierarchyChangeListener> listeners =
      new ListenerManager<HierarchyChangeListener>();

  public static interface HierarchyChangeListener {
    public void hierarchyChanged();
  }

  /////////////////////////////////////
  // Relationship Set Selector itself

  public HierarchyViewer(Composite parent) {
    super(parent, SWT.NONE);
    this.setLayout(Widgets.buildContainerLayout(2));

    edgeMatcherSelector = new EdgeMatcherSelectorControl(this);
    edgeMatcherSelector.setLayoutData(Widgets.buildHorzFillData());
    edgeMatcherSelector.addChangeListener(
        new EdgeMatcherSelectorControl.SelectorListener() {

          @Override
          public void selectedEdgeMatcherChanged(
              PropertyDocumentReference<GraphEdgeMatcherDescriptor> edgeMatcher) {
            fireSelectionChange();
          }
        }
      );
  }

  public void setInput(
      HierarchyCache<T> hierarchies,
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef,
      IProject project) {
    edgeMatcherSelector.setInput(matcherRef, project);
    this.hierarchies = hierarchies;
  }

  /////////////////////////////////////
  // Listener support

  /**
   * @param listener new listener for this selector
   */
  public void addChangeListener(HierarchyChangeListener listener) {
    listeners.addListener(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(HierarchyChangeListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Called when the user has changed the hierarchy.
   * 
   * @param selection the new selection
   */
  protected void fireSelectionChange() {
    listeners.fireEvent(new SimpleDispatcher());
  }

  private static class SimpleDispatcher
    implements ListenerManager.Dispatcher<HierarchyChangeListener> {

    @Override
    public void dispatch(HierarchyChangeListener listener) {
      listener.hierarchyChanged();
    }

    @Override
    public void captureException(RuntimeException errAny) {
      NodesUILogger.LOG.warn(errAny.toString());
    }
  }

  /**
   * Provide the currently selected hierarchy data as a GraphData
   * instance.  If invert selection is chosen, GraphData for the graph nodes
   * that are outside selected hierarchy is returned.
   */
  public GraphData<T> getGraphData() {
    if (null == hierarchies) {
      return new GraphData<T>(null, TreeModel.EMPTY);
    }

    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef =
        edgeMatcherSelector.getSelection();
    if (null == matcherRef) {
      return new GraphData<T>(null, TreeModel.EMPTY);
    }
    EdgeMatcher<String> matcher = matcherRef.getDocument().getInfo();
    GraphData<T> baseData = hierarchies.getHierarchy(matcher);

    return baseData;
  }
}
