/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.editors.ViewEditorInput;
import com.google.devtools.depan.eclipse.utils.NodeLabelProvider;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Sasher;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.ViewModel;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Tool for expanding a selection with a set of relationships, and create a new
 * view.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class SelectionEditorTool extends ViewSelectionListenerTool {

  private TableContentProvider<GraphNode> selectedNodesContent = null;
  private TableViewer selectedNodes;

  private TableContentProvider<GraphNode> previewListContent = null;
  private TableViewer previewList;

  private CCombo layoutChoice = null;
  private static final String LAYOUT_KEEP = "Keep positions";

  private CCombo modeChoice = null;

  // Define a static list of selection modes that are used in the UI
  // TODO(leeca): Maybe turn into extensible list
  private static final List<SelectionMode> 
      SELECTION_MODES = Arrays.asList(
          SelectionMode.INCLUDE_MODEL, SelectionMode.INCLUDE_SELECTED,
          SelectionMode.RESULTS_ONLY, SelectionMode.NEW_ONLY);

  /**
   * Holds the relationship picker and the path expression editor.
   */
  private TabFolder selectorTab = null;

  /** List of node selectors installed in the selector tab */
  private List<NodeSelectorPart> selectorTabParts = Lists.newArrayList();

  /**
   * How should discovered nodes be merged with the current selection
   * set.
   */
  // TODO(leeca): Have PathExpressions use these modes between steps?
  private static enum SelectionMode {
    INCLUDE_MODEL("Extend graph") {
      @Override
      public void updatePendingList(SelectionEditorTool context) {
        // Include all nodes in current view
        for (GraphNode node : context.getViewModel().getNodes()) {
          context.previewListContent.add(node);
        }
      }
    },
    INCLUDE_SELECTED("Selected + New nodes") {
      @Override
      public void updatePendingList(SelectionEditorTool context) {
        // Add any currently selected nodes
        for (GraphNode node : context.getSelectedNodes()) {
          context.previewListContent.add(node);
        }
      }
    },
    RESULTS_ONLY("Only nodes in results") {
      @Override
      public void updatePendingList(SelectionEditorTool context) {
        // Do nothing, just show nodes in the result set
      }
    },
    NEW_ONLY("Only new nodes") {
      @Override
      public void updatePendingList(SelectionEditorTool context) {
        // Remove selected nodes, we just what the new nodes
        for (GraphNode node : context.getSelectedNodes()) {
          context.previewListContent.remove(node);
        }
      }
    };

    private final String label;

    private SelectionMode(String label) {
      this.label = label;
    }

    abstract public void updatePendingList(SelectionEditorTool context);

    public String getLabel() {
      return label;
    }
  }

  /**
   * A UI Part that allows a user to configure a PathMatcher.
   */
  public static interface NodeSelectorPart {
    Composite createControl(Composite Parent, int style, ViewEditor viewEditor);

    /**
     * Reset the node selector to the choices defined in the {@code ViewEditor}.
     * @param viewEditor
     */
    void updateControl(ViewEditor viewEditor);

    /**
     * Provide a node selector that corresponds to the current values in the UI.
     * 
     * @return node selector derived from the UI's values.
     */
    PathMatcher getNodeSelector();
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_SELECTIONEDITOR;
  }

  @Override
  public String getName() {
    return Resources.NAME_SELECTIONEDITOR;
  }

  private Layouts getLayoutChoice() {
    if (layoutChoice.getSelectionIndex() == 0) {
      // return null, which means that a static layout must be used, using the
      // previously existing positions.
      return null;
    }
    Layouts l = Layouts.valueOf(
        layoutChoice.getItem(layoutChoice.getSelectionIndex()));
    return l;
  }

  @Override
  public Control setupComposite(Composite parent) {
    // Create components
    Composite baseComposite = new Composite(parent, SWT.NONE);

    Sasher outerSash = new Sasher(baseComposite, SWT.NONE);
    Sasher innerSash = new Sasher(outerSash, SWT.NONE);
    Control selectedNodesControl = setupSelectedNodesContent(innerSash);

    // Create the tab folder that will hold the various node selectors
    selectorTab = new TabFolder(innerSash, SWT.BORDER);
    selectorTabParts = Lists.newArrayList();

    NodeSelectorPart relationPickerPart = new RelationNodeSelectorPart();
    installNodeSelectorTab("Relation Path Selector", relationPickerPart);

    NodeSelectorPart relationCountEditor = 
        new RelationCountNodeSelectorTool.SelectorPart();
    installNodeSelectorTab("Relation Count Selector", relationCountEditor);

    NodeSelectorPart nodeKindEditor = 
        new ElementKindSelectorTool.SelectorPart();
   installNodeSelectorTab("Node Kind Selector", nodeKindEditor);

    NodeSelectorPart pathPickerPart = new PathExpressionEditorTool();
    installNodeSelectorTab("Path Expression Tool", pathPickerPart);

    Control previewListControl = setupPreviewList(outerSash);

    // Create and configure the Selection Mode controls
    new Label(baseComposite, SWT.NONE).setText("Creation mode");
    modeChoice = createModeCombo(baseComposite,
        SELECTION_MODES, SelectionMode.INCLUDE_SELECTED);
    modeChoice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    new Label(baseComposite, SWT.NONE).setText("Apply layout");
    layoutChoice = new CCombo(baseComposite, SWT.READ_ONLY | SWT.BORDER);

    Composite actionButtons = new Composite(baseComposite, SWT.NONE);
    Button preview = new Button(actionButtons, SWT.PUSH);
    Button justSelect = new Button(actionButtons, SWT.PUSH);
    Button doit = new Button(actionButtons, SWT.PUSH);

    // layouts
    // warning: if you change the order of the combo items, change the indexes
    // in createNewView() !
    layoutChoice.add(LAYOUT_KEEP);
    for (Layouts l : Layouts.values()) {
      layoutChoice.add(l.toString());
    }
    layoutChoice.select(0);

    // the middle table (relationTable occupies 50% of all space, both
    // top (selectedNodes) and bottom (previewList) share the 50% left.
    innerSash.init(selectedNodesControl, selectorTab, SWT.HORIZONTAL, 25);
    outerSash.init(innerSash, previewListControl, SWT.HORIZONTAL, 75);
    int minSasherSize = 20;
    innerSash.setLimit(minSasherSize);
    outerSash.setLimit(2 * minSasherSize);

    preview.setText("Preview");
    preview.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        refreshPreview();
      }
    });

    justSelect.setText("Select");
    justSelect.setToolTipText("Only select nodes present in the current View.");
    justSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        select();
      }
    });

    doit.setText("Create new view");
    doit.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createNewView();
      }
    });

    // setup layouts
    GridLayout baseGrid = new GridLayout();
    baseGrid.numColumns = 2;
    baseComposite.setLayout(baseGrid);

    actionButtons.setLayout(new GridLayout(3, true));
    outerSash.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    layoutChoice.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    actionButtons.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    preview.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    justSelect.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    doit.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return baseComposite;
  }

  private static CCombo createModeCombo(Composite parent,
        List<SelectionMode> modeChoices, SelectionMode initialMode) {
    CCombo modeControl = new CCombo(parent, SWT.READ_ONLY | SWT.BORDER);
    for (SelectionMode mode : modeChoices) {
      modeControl.add(mode.getLabel());
    }

    int modeIndex = modeChoices.indexOf(initialMode);
    modeControl.select(modeIndex >=0 ? modeIndex : 0);
    return modeControl;
  }

  /**
   * Install a new node selector part into the selectors tab.
   * 
   * @param label text for tab
   * @param selectorPart source of UI and node selector
   */
  private void installNodeSelectorTab(
      String label, NodeSelectorPart selectorPart) {
    Composite selectionControl =
        selectorPart.createControl(selectorTab, SWT.NONE, getEditor());
    selectionControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    TabItem tabItem = new TabItem(selectorTab, SWT.NONE);
    tabItem.setText(label);
    tabItem.setControl(selectionControl);
    selectorTabParts.add(selectorPart);
  }

  private Control setupPreviewList(Composite parent) {
    // control
    previewList = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);

    // Preview list
    previewListContent = new TableContentProvider<GraphNode>();
    previewListContent.initViewer(previewList);
    previewList.setLabelProvider(new NodeLabelProvider());
    //previewList.setSorter(new NodeSorter());

    return previewList.getTable();
  }

  private Control setupSelectedNodesContent(Composite parent) {
    // component
    selectedNodes = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);

    // currently selected list of nodes
    selectedNodesContent = new TableContentProvider<GraphNode>();
    selectedNodesContent.initViewer(selectedNodes);

    // it's easy to set up a different laberProvider for setting up icons,
    // columns...
    selectedNodes.setLabelProvider(new NodeLabelProvider());
    //selectedNodes.setSorter(new NodeSorter());

    return selectedNodes.getTable();
  }

  /**
   * Select in the currently selected view the resulting nodes.
   */
  protected void select() {
    // compute the result
    refreshPreview();
    // apply the new selection
    getView().setPickedNodes(previewListContent.getObjects());
  }

  protected void createNewView() {
    // compute the result
    refreshPreview();

    // create a new ViewModel with the nodes
    ViewModel newView = new ViewModel(getViewModel().getParentGraph());
    newView.setNodes(previewListContent.getObjects());
    // apply the layout
    if (layoutChoice.getSelectionIndex() == 0) {
      newView.setLocations(getViewModel().getLayoutMap());
    }

    // and the ViewEditorInput
    final ViewEditorInput input = new ViewEditorInput(
        newView, getLayoutChoice(),
        getEditor().getParentUri());

    ViewEditor.startViewEditor(input);
  }

  /**
   * Obtains a node selector instance based on the current selectorTab.
   *
   * @return <code>PathMatcher</code> that generates a set of new nodes
   */
  // TODO(leeca): rename PathMatcher class to NodeSelector
  private PathMatcher getNodeSelector() {
    int selectionIndex = selectorTab.getSelectionIndex();
    return selectorTabParts.get(selectionIndex).getNodeSelector();
  }

  protected void refreshPreview() {
    previewListContent.clear();

    for (GraphNode node : computeSelectorNodes()) {
      previewListContent.add(node);
    }

    SelectionMode mode = SELECTION_MODES.get(modeChoice.getSelectionIndex());
    mode.updatePendingList(this);

    previewList.refresh(false);
  }

  @Override
  protected void updateControls() {
    int activeTab = selectorTab.getSelectionIndex();
    if (activeTab >= 0) {
      selectorTabParts.get(activeTab).updateControl(getEditor());
    }

    super.updateControls();
  }


  private Iterable<GraphNode> computeSelectorNodes() {
    PathMatcher nodeSelector = getNodeSelector();
    if (null == nodeSelector) {
      return Collections.emptyList();
    }

    Set<GraphNode> currSelection = getSelectedNodes();

    // TODO(leeca): Source graph model should be baked into node selector
    return nodeSelector.nextMatch(
        getViewModel().getParentGraph(), currSelection);
  }

  private Set<GraphNode> getSelectedNodes() {
    return selectedNodesContent.getObjects();
  }

  @Override
  public void emptySelection() {
    selectedNodesContent.clear();
    previewListContent.clear();

    selectedNodes.refresh(false);
    previewList.refresh(false);
  }

  /**
   * Add the given nodes to the list of selected nodes.
   */
  @Override
  public void updateSelectedAdd(GraphNode[] selection) {
    for (GraphNode node : selection) {
      selectedNodesContent.add(node);
    }
    selectedNodes.refresh(false);
    refreshPreview();
  }

  /**
   * Remove the given nodes from the list of selected nodes.
   */
  @Override
  public void updateSelectedRemove(GraphNode[] selection) {
    for (GraphNode node : selection) {
      selectedNodesContent.remove(node);
    }
    selectedNodes.refresh(false);
    refreshPreview();
  }

  /**
   * Set the list of selected nodes to contains exactly the given set of nodes.
   */
  @Override
  public void updateSelectionTo(GraphNode[] selection) {
    selectedNodesContent.clear();
    updateSelectedAdd(selection);
  }
}
