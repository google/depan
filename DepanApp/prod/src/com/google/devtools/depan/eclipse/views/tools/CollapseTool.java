/*
 * Copyright 2007 Google Inc.
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
import com.google.devtools.depan.eclipse.editors.ViewPrefsListener;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.trees.NodeTreeViews;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeProvider;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView;
import com.google.devtools.depan.eclipse.utils.HierarchyViewer;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.CollapseTreeModel;
import com.google.devtools.depan.view.NodeDisplayProperty;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;
import java.util.List;

/**
 * Collapse Tool. Provide a GUI to collapse and uncollapse nodes under another
 * node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTool extends ViewSelectionListenerTool
    implements RelationshipSelectorListener {
  
  private CollapseTreeProvider<NodeDisplayProperty> provider =
      new CollapseTreeProvider<NodeDisplayProperty>() {

    @Override
    public NodeDisplayProperty getObject(CollapseData collapseData) {
      if (hasEditor()) {
        return null;
      }
      GraphNode node = collapseData.getMasterNode();
      return getEditor().getNodeProperty(node);
    }
  };

  /**
   * Content provider for selected nodes. Fill the list of selected nodes used
   * to choose the master node for a new collapse group.
   */
  private TableContentProvider<GraphNode> collapseMaster = null;

  /** Provides hierarchy to use for autoCollapse operations */
  private HierarchyViewer<NodeDisplayProperty> autoHierarchyPicker;

  private CollapseTreeView<NodeDisplayProperty> collapseView = null;

  private ViewPrefsListener prefsListener;

  /**
   * TODO: use those options for uncollapsing.
   */
  private static final String[] uncollapseOptions =
      new String[] {"normal", "in a new view"};

  private ComboViewer masterViewer;

  @Override
  public Control setupComposite(Composite parent) {
    // first expand bar containing collapsing operations
    Composite topComposite = new Composite(parent, SWT.NONE);

    GridLayout topGrid = new GridLayout();
    topGrid.verticalSpacing = 9;
    topComposite.setLayout(topGrid);

    // Setup the manual collapse controls
    setupManualCollapseGroup(topComposite);
    setupAutoCollapseGroup(topComposite);
    setupCollapseHierarchy(topComposite);

    // content
    collapseMaster = new TableContentProvider<GraphNode>();
    collapseMaster.initViewer(masterViewer);

    //FIXME(yc): select first set
    //namedSet.selectSet(BuiltinRelationshipSets.CONTAINER);

    return topComposite;
  }

  private Composite setupCollapseHierarchy(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    baseComposite.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    baseComposite.setLayout(new GridLayout(1, false));

    Composite options = new Composite(baseComposite, SWT.NONE);
    options.setLayout(new RowLayout());
    options.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button collapseButton = setupPushButton(options, "collapseAll");
    collapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapseView.collapseAll();
      }
    });
    collapseButton.setLayoutData(new RowData());

    Button expandButton = setupPushButton(options, "expandAll");
    expandButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapseView.expandAll();
      }
    });
    expandButton.setLayoutData(new RowData());

    collapseView = CollapseTreeView.buildCollapseTreeView(
        baseComposite,
        SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL,
        provider);

    TreeViewer viewer = collapseView.getTreeViewer();
    viewer.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    NodeTreeViews.configure(viewer);

    return baseComposite;
  }

  private void setupManualCollapseGroup(Composite parent) {
    Group manualCollapse = new Group(parent, SWT.NONE);
    manualCollapse.setText("Manual collapsing");
    manualCollapse.setLayoutData(
      new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout manualGrid = new GridLayout(2, true);
    manualGrid.marginWidth = 10;
    manualGrid.marginHeight = 10;
    manualCollapse.setLayout(manualGrid);

    Label collapseLabel = setupLabel(manualCollapse, "Collapse under");
    collapseLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

    masterViewer = new ComboViewer(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    masterViewer.getCombo().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Button eraseCollapse = setupPushButton(manualCollapse, "collapse / erase");
    eraseCollapse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button collapseButton = setupPushButton(manualCollapse, "collapse / add");
    collapseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Label uncollapseLabel = setupLabel(manualCollapse, "Uncollapse");
    uncollapseLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

    final Combo uncollapseOpts =
        new Combo(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    for (String s : uncollapseOptions) {
      uncollapseOpts.add(s);
    }
    uncollapseOpts.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Button deleteCollapse = setupPushButton(manualCollapse, "uncollapse / Delete");
    deleteCollapse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button uncollapseButton = setupPushButton(manualCollapse, "uncollapse");
    uncollapseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button uncollapseAll = setupPushButton(manualCollapse, "Uncollapse All Selected");
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
    deleteCollapse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseButton(uncollapseOpts.getSelectionIndex(), true);
      }
    });
    uncollapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseButton(uncollapseOpts.getSelectionIndex(), false);
      }
    });
    uncollapseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseAllSelected();
      }
    });
  }

  private void setupAutoCollapseGroup(Composite parent) {
    Group autoCollapse = new Group(parent, SWT.NONE);
    autoCollapse.setText("Automatic collapsing based on a relation set");

    autoCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout autoGrid = new GridLayout(2, false);
    autoGrid.marginWidth = 10;
    autoGrid.marginHeight = 10;
    autoCollapse.setLayout(autoGrid);

    autoHierarchyPicker =
        new HierarchyViewer<NodeDisplayProperty>(autoCollapse, false);
    autoHierarchyPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));
    Button doAutoGrouping = setupPushButton(autoCollapse, "Collapse");
    doAutoGrouping.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    // actions
    doAutoGrouping.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        autoCollapse();
      }
    });
  }

  private Label setupLabel(Group manualCollapse, String text) {
    Label result = new Label(manualCollapse, SWT.NONE);
    result.setText(text);
    return result;
  }

  private Button setupPushButton(Composite parent, String text) {
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

    prefsListener = new ViewPrefsListener.Simple()  {

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
    HierarchyCache<NodeDisplayProperty> hierarchies = getEditor().getHierarchies();
    RelationshipSet selectedRelSet = getEditor().getContainerRelSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    autoHierarchyPicker.setInput(hierarchies, selectedRelSet, choices);

    updateCollapseView();
  }

  /**
   * Autocollapse the current graph.
   * Redraws are triggered by event propagation.
   *
   * @param finder
   */
  protected void autoCollapse() {
    // TODO(leeca): How can this be active if there is no ViewModel
    if (!hasEditor()) {
      return;
    }

    GraphData<NodeDisplayProperty> graphData =
        autoHierarchyPicker.getGraphData();
    getEditor().collapseTree(graphData.getTreeModel(), null);
  }

  /**
   * uncollapse operation.
   *
   * @param selectionIndex index selected in the uncollapse option list.
   * @param deleteGroup if true, delete the group after uncollapsing.
   */
  protected void uncollapseButton(int selectionIndex, boolean deleteGroup) {
    GraphNode master = collapseMaster.getElementAtIndex(
        masterViewer.getCombo().getSelectionIndex());
    getEditor().uncollapse(master, deleteGroup, null);
  }
  
  /**
   * Uncollapses <b>all</b> selected nodes.
   */
  protected void uncollapseAllSelected() {
    int selectionNumber = masterViewer.getCombo().getItemCount();
    for (int i = 0; i < selectionNumber; i++) {
      GraphNode master = collapseMaster.getElementAtIndex(i);
      getEditor().uncollapse(master, false, null);
    }
  }

  /**
   * Collapsing operation.
   *
   * @param erase if true, if there is an existing group with the same master,
   *        it will be erased. if not, the selected nodes will be added to the
   *        existing group.
   */
  protected void collapse(boolean erase) {
    GraphNode master = collapseMaster.getElementAtIndex(
        masterViewer.getCombo().getSelectionIndex());
    Collection<GraphNode> objects = collapseMaster.getObjects();
    getEditor().collapse(master, objects, erase, null);
  }

  @Override
  public void emptySelection() {
    if (null != collapseMaster) {
      collapseMaster.clear();
    }
    if (null != masterViewer) {
      masterViewer.refresh(false);
    }
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
  public void selectedSetChanged(RelationshipSet set) {
    // nothing to do here, use the RelationshipSet only when clicking a button.
  }

  /**
   * Add newly selected nodes to the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    for (GraphNode node : extension) {
      collapseMaster.add(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }

  /**
   * Remove unselected nodes from the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    for (GraphNode node : reduction) {
      collapseMaster.remove(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
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

    CollapseTreeModel collapseTreeModel = getEditor().getCollapseTreeModel();
    collapseView.updateData(collapseTreeModel);
  }
}
