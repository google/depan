/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.HierarchyCache;
import com.google.devtools.depan.eclipse.editors.NodeDisplayProperty;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.editors.ViewPrefsListener;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.utils.HierarchyViewer;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.SelectedNodeChoicesControl;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.ui.nodes.trees.GraphNodeViewer;
import com.google.devtools.depan.view.CollapseData;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import java.util.Collection;
import java.util.List;

/**
 * Collapse Tool. Provide a GUI to collapse and uncollapse nodes under another
 * node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTool extends ViewSelectionListenerTool {

  // UX Elelements
  /** For manual collapse selecting master node for new collapse group */
  private SelectedNodeChoicesControl selectedNodes;

  /** Provides hierarchy to use for autoCollapse operations. */
  private HierarchyViewer<NodeDisplayProperty> autoHierarchyPicker;

  private GraphNodeViewer<NodeDisplayProperty> nodeViewer =
      new GraphNodeViewer<NodeDisplayProperty>();

  /** Monitor changes on the underlying persistent document */
  private ViewPrefsListener prefsListener;

  @Override
  public Control setupComposite(Composite parent) {
    // first expand bar containing collapsing operations
    Composite topComposite = new Composite(parent, SWT.NONE);

    GridLayout topGrid = new GridLayout();
    topGrid.verticalSpacing = 9;
    topComposite.setLayout(topGrid);

    // Setup the collapse controls.
    TabFolder folder = createCollapseTabs(topComposite);
    folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite treeControl = nodeViewer.createNodeViewer(topComposite);
    treeControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    return topComposite;
  }

  private TabFolder createCollapseTabs(Composite parent) {
    TabFolder result = new TabFolder(parent, SWT.NONE);

    TabItem selected = new TabItem(result, SWT.None);
    selected.setText("Selected Nodes");
    selected.setControl(createSelectedCollapse(result));

    TabItem byEdge = new TabItem(result, SWT.None);
    byEdge.setText("By Edge");
    byEdge.setControl(createEdgeCollapse(result));

    return result;
  }

  private Composite createSelectedCollapse(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);

    GridLayout manualGrid = new GridLayout(2, true);
    manualGrid.marginWidth = 10;
    manualGrid.marginHeight = 10;
    result.setLayout(manualGrid);

    Label collapseLabel = createLabel(result, "Collapse under");
    collapseLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

    selectedNodes = new SelectedNodeChoicesControl(result);
    selectedNodes.getCombo().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Button eraseCollapse = createPushButton(result, "collapse / erase");
    eraseCollapse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button collapseButton = createPushButton(result, "collapse / add");
    collapseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button uncollapseAll = createPushButton(result, "Uncollapse All Selected");
    uncollapseAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    // actions
    eraseCollapse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapse(true);
      }
    });
    collapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapse(false);
      }
    });
    uncollapseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseAllSelected();
      }
    });

    return result;
  }

  private Composite createEdgeCollapse(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);

    result.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout autoGrid = new GridLayout(2, false);
    autoGrid.marginWidth = 10;
    autoGrid.marginHeight = 10;
    result.setLayout(autoGrid);

    autoHierarchyPicker =
        new HierarchyViewer<NodeDisplayProperty>(result, false);
    autoHierarchyPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));
    Button doAutoGrouping = createPushButton(result, "Collapse");
    doAutoGrouping.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    // actions
    doAutoGrouping.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        autoCollapse();
      }
    });

    return result;
  }

  private Label createLabel(Composite manualCollapse, String text) {
    Label result = new Label(manualCollapse, SWT.NONE);
    result.setText(text);
    return result;
  }

  private Button createPushButton(Composite parent, String text) {
    Button result = new Button(parent, SWT.PUSH);
    result.setText(text);
    return result;
  }

  /////////////////////////////////////
  // Tool life-cycle methods

  @Override
  protected void acquireResources() {
    super.acquireResources();

    updateCollapseView();

    prefsListener = new ViewPrefsListener.Simple() {

      @Override
      public void collapseChanged(
          Collection<CollapseData> created,
          Collection<CollapseData> removed, Object author) {
        updateCollapseView();
      }
    };
    getEditor().addViewPrefsListener(prefsListener);
  }

  @Override
  protected void releaseResources() {
    if (hasEditor()) {
      getEditor().removeSelectionChangeListener(this);
      if (null != prefsListener) {
        getEditor().removeViewPrefsListener(prefsListener);
      }
    }
    super.releaseResources();
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // Update the RelSet picker for auto-collapse.
    HierarchyCache<NodeDisplayProperty> hierarchies =
        getEditor().getHierarchies();
    GraphEdgeMatcherDescriptor edgeMatcher =
        getEditor().getTreeEdgeMatcher();
    List<GraphEdgeMatcherDescriptor> choices =
        getEditor().getTreeEdgeMatcherChoices();
    autoHierarchyPicker.setInput(hierarchies, edgeMatcher, choices);

    updateCollapseView();
  }

  /**
   * Autocollapse the current graph.
   * Redraws are triggered by event propagation.
   *
   * @param finder
   */
  private void autoCollapse() {
    // TODO(leeca): How can this be active if there is no ViewModel
    if (!hasEditor()) {
      return;
    }

    GraphData<NodeDisplayProperty> graphData =
        autoHierarchyPicker.getGraphData();
    getEditor().collapseTree(graphData.getTreeModel(), null);
  }

  /**
   * Uncollapses <b>all</b> selected nodes.
   */
  private void uncollapseAllSelected() {
    for (GraphNode master : selectedNodes.getSelectedNodes()) {
      getEditor().uncollapse(master, null);
    }
  }

  /**
   * Collapsing operation.
   *
   * @param erase if true, if there is an existing group with the same master,
   *        it will be erased. if not, the selected nodes will be added to the
   *        existing group.
   */
  private void collapse(boolean erase) {
    GraphNode master = selectedNodes.getChosenNode();
    Collection<GraphNode> objects = selectedNodes.getSelectionNodes();
    getEditor().collapse(master, objects, erase, null);
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_COLLAPSE;
  }

  @Override
  public String getName() {
    return Resources.NAME_COLLAPSE;
  }

  @Override
  public void emptySelection() {
    selectedNodes.emptySelection();
  }

  /**
   * Add newly selected nodes to the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    selectedNodes.extendSelection(extension);
  }

  /**
   * Remove unselected nodes from the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    selectedNodes.reduceSelection(reduction);
  }

  /**
   * Refresh the content of the collapseMaster combo with selected nodes.
   */
  @Override
  public void updateSelectionTo(Collection<GraphNode> selection) {
    emptySelection();
    updateSelectedExtend(selection);
  }

  private void updateCollapseView() {
    if (!hasEditor()) {
      return;
    }

    ViewEditor editor = getEditor();
    nodeViewer.setGraphModel(editor.getViewGraph());
    nodeViewer.setCollapseTreeModel(editor.getCollapseTreeModel());
    nodeViewer.setProvider(editor.getNodeDisplayPropertyProvider());
    nodeViewer.refresh();
  }
}
