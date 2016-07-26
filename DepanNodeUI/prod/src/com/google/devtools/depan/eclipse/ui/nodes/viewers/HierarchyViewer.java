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

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.GraphEdgeMatcherSelectorControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.platform.ListenerManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * A composite control for obtaining a user selected hierarchy.
 *
 * Listeners are notified whenever the hierarchy is changed.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class HierarchyViewer<T> extends Composite {

  private static final Logger logger =
      Logger.getLogger(HierarchyViewer.class.getName());

  private HierarchyCache<T> hierarchies;

  private boolean isInvertSelect;

  private GraphEdgeMatcherSelectorControl edgeMatcherSelector;

  /** Listener when the selection change. */
  // private List<HierarchyChangeListener> listeners = Lists.newArrayList();

  private ListenerManager<HierarchyChangeListener> listeners =
      new ListenerManager<HierarchyChangeListener>();

  public static interface HierarchyChangeListener {
    public void hierarchyChanged();
  }

  /////////////////////////////////////
  // Relationship Set Selector itself

  public HierarchyViewer(Composite parent, boolean isInvertSelect) {
    super(parent, SWT.NONE);
    this.isInvertSelect = isInvertSelect;

    GridLayout viewerLayout = new GridLayout(3, false);
    viewerLayout.marginWidth = 0;
    viewerLayout.marginHeight = 0;
    this.setLayout(viewerLayout);

    Label selectorLabel = new Label(this, SWT.NONE);
    selectorLabel.setText("Hierarchy from");
    selectorLabel.setLayoutData(
        new GridData(SWT.LEFT, SWT.CENTER, false, false));

    edgeMatcherSelector = new GraphEdgeMatcherSelectorControl(this);
    edgeMatcherSelector.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));

    edgeMatcherSelector.addChangeListener(
        new GraphEdgeMatcherSelectorControl.SelectorListener() {

          @Override
          public void selectedEdgeMatcherChanged(
              GraphEdgeMatcherDescriptor edgeMatcher) {
            fireSelectionChange();
          }
        }
      );

    final Button invertSelect = new Button(this, SWT.CHECK);
    invertSelect.setText("Invert");
    invertSelect.setSelection(isInvertSelect);
    invertSelect.setLayoutData(
        new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    invertSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setInvertSelect(invertSelect.getSelection());
      }
    });
  }

  public void setInput(
      HierarchyCache<T> hierarchies,
      GraphEdgeMatcherDescriptor selectedEdgeMatcher,
      List<GraphEdgeMatcherDescriptor> choices) {
    edgeMatcherSelector.setInput(selectedEdgeMatcher, choices);
    this.hierarchies = hierarchies;
  }

  public void setInvertSelect(boolean invertSelect) {
    isInvertSelect = invertSelect;
    fireSelectionChange();
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
      logger.warning(errAny.toString());
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

    GraphEdgeMatcherDescriptor selectedEdgeMatcher =
        edgeMatcherSelector.getSelection();
    if (null == selectedEdgeMatcher) {
      return new GraphData<T>(null, TreeModel.EMPTY);
    }
    GraphData<T> baseData =
        hierarchies.getHierarchy(selectedEdgeMatcher.getInfo());

    // Synthesize a inverse tree if requested.
    // These are not cached.  The inverted hierarchies are often small,
    // and quick to computes, so it should not matter.  When the inverts
    // are large, they are rarely useful and rarely have a long lifetime.
    // Caching a such a model will only impede garbage collection.
    if (isInvertSelect) {
      return excludedNodes(baseData);
    }

    return baseData;
  }

  private GraphData<T> excludedNodes(GraphData<T> baseData) {
    TreeModel baseTree = baseData.getTreeModel();
    Collection<GraphNode> included = baseTree.computeTreeNodes();
    return hierarchies.excludedNodes(included);
  }
}
