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

import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.LayoutChoicesControl;
import com.google.devtools.depan.eclipse.utils.NodeLabelProvider;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Sasher;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutContext;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerators;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutUtil;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;

import com.google.common.collect.Lists;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import java.util.Arrays;
import java.util.Collection;
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

  private Composite toolPanel;

  private TableContentProvider<GraphNode> selectedNodesContent = null;
  private TableViewer selectedNodes;

  private TableContentProvider<GraphNode> previewListContent = null;
  private TableViewer previewList;
  private LayoutChoicesControl layoutChoices;

  private Combo modeChoice = null;

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
  private TabSelectorListener selectorTabListener;

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
        GraphModel graph = context.getEditor().getViewGraph();
        for (GraphNode node : graph.getNodes()) {
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

  @Override
  public Control setupComposite(Composite parent) {
    toolPanel = new Composite(parent, SWT.NONE);
    toolPanel.setLayout(new GridLayout(1, false));

    Sasher outerSash = new Sasher(toolPanel, SWT.NONE);
    outerSash.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    Sasher innerSash = new Sasher(outerSash, SWT.NONE);
    Control selectedNodesControl = setupSelectedNodesContent(innerSash);

    // Create the tab folder that will hold the various node selectors
    selectorTabParts = Lists.newArrayList();
    selectorTab = new TabFolder(innerSash, SWT.BORDER);
    selectorTabListener = new TabSelectorListener();
    selectorTab.addSelectionListener(selectorTabListener);

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
    Composite modeRegion = setupModeCombo(toolPanel,
        SELECTION_MODES, SelectionMode.INCLUDE_SELECTED);
    modeRegion.setLayoutData(
        new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

    layoutChoices = new LayoutChoicesControl(
        toolPanel, LayoutChoicesControl.Style.LINEAR);
    layoutChoices.setLayoutData(
        new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

    layoutChoices.setLayoutChoices(LayoutGenerators.getLayoutNames(true));
    updateSelectedLayout();

    // the middle table (relationTable occupies 50% of all space, both
    // top (selectedNodes) and bottom (previewList) share the 50% left.
    innerSash.init(selectedNodesControl, selectorTab, SWT.HORIZONTAL, 25);
    outerSash.init(innerSash, previewListControl, SWT.HORIZONTAL, 75);
    int minSasherSize = 20;
    innerSash.setLimit(minSasherSize);
    outerSash.setLimit(2 * minSasherSize);

    Composite actionButtons = createActionButtons(toolPanel);
    actionButtons.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return toolPanel;
  }

  /**
   * Set the selected layout control to show the current layout for the editor.
   */
  private void updateSelectedLayout() {
    if (null != getEditor()) {
      layoutChoices.selectLayout(getEditor().getLayoutName());
    }
  }

  private Composite setupModeCombo(Composite parent,
        List<SelectionMode> modeChoices, SelectionMode initialMode) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, false));

    Label label = new Label(result, SWT.NONE);
    label.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));

    label.setText("Creation mode:");

    modeChoice = new Combo(result, SWT.READ_ONLY | SWT.BORDER);
    modeChoice.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));

    for (SelectionMode mode : modeChoices) {
      modeChoice.add(mode.getLabel());
    }

    int modeIndex = modeChoices.indexOf(initialMode);
    modeChoice.select(modeIndex >= 0 ? modeIndex : 0);

    return result;
  }

  private Composite createActionButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(3, true));

    GridDataFactory buttonLayoutData =
        GridDataFactory.fillDefaults().grab(true, false);

    Button preview = new Button(result, SWT.PUSH);
    buttonLayoutData.applyTo(preview);
    preview.setText("Preview");
    preview.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        refreshPreview();
      }
    });

    Button justSelect = new Button(result, SWT.PUSH);
    buttonLayoutData.applyTo(justSelect);
    justSelect.setText("Select");
    justSelect.setToolTipText("Only select nodes present in the current View.");
    justSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        select();
      }
    });

    Button doit = new Button(result, SWT.PUSH);
    buttonLayoutData.applyTo(doit);
    doit.setText("Create new view");
    doit.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createNewView();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // Handle NodeSelectorParts

  private class TabSelectorListener implements SelectionListener {

    @Override
    public void widgetDefaultSelected(SelectionEvent event) {
      widgetSelected(event);
    }

    @Override
    public void widgetSelected(SelectionEvent event) {
      // It would be better to use event.item, but its still hard to
      // get from that to the selectorTabPart that needs to receive the
      // updateControl() call.
      ViewEditor editor = getEditor();
      if (null == editor) {
        return;
      }
      int activeTab = selectorTab.getSelectionIndex();
      if (activeTab >= 0) {
        selectorTabParts.get(activeTab).updateControl(editor);
      }
    }
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
    selectorTabParts.add(selectorPart);

    TabItem tabItem = new TabItem(selectorTab, SWT.NONE);
    tabItem.setText(label);
    tabItem.setControl(selectionControl);
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
    getEditor().selectNodes(previewListContent.getObjects());
  }

  protected void createNewView() {
    // compute the result
    refreshPreview();
    Collection<GraphNode> viewNodes = previewListContent.getObjects();

    // create a new ViewModel with the nodes
    // node locations for current nodes are copied to the new view
    ViewEditor viewEditor = getEditor();
    ViewDocument viewDoc = viewEditor.buildNewViewDocument(viewNodes);

    String layoutName = layoutChoices.getLayoutName();
    viewDoc.setSelectedLayout(layoutName);
    viewDoc.setLayoutFinder(layoutChoices.getRelationSet());

    // Preserve locations if no new layout is selected
    if (null == layoutName) {
      viewDoc.setNodeLocations(viewEditor.getNodeLocations());
    } else {
      LayoutContext layoutContext = LayoutUtil.newLayoutContext(
          viewEditor.getParentGraph(), viewNodes, layoutChoices.getRelationSet());
      layoutContext.setNodeLocations(getEditor().getNodeLocations());

      // Do the layout for these nodes
      LayoutGenerator layout = layoutChoices.getLayoutGenerator();
      viewDoc.setNodeLocations(
          LayoutUtil.calcPositions(layout, layoutContext, viewNodes));
    }

    ViewEditor.startViewEditor(viewDoc);
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
    updateSelectedLayout();
    int activeTab = selectorTab.getSelectionIndex();
    if (activeTab >= 0) {
      selectorTabParts.get(activeTab).updateControl(getEditor());
    }

    super.updateControls();

    // Update the RelSet picker
    RelationshipSet selectedRelSet = getEditor().getContainerRelSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    layoutChoices.setRelSetInput(selectedRelSet, choices );
    toolPanel.layout();
  }


  private Iterable<GraphNode> computeSelectorNodes() {
    PathMatcher nodeSelector = getNodeSelector();
    if (null == nodeSelector) {
      return Collections.emptyList();
    }

    Set<GraphNode> currSelection = getSelectedNodes();

    // TODO(leeca): Source graph model should be baked into node selector
    return nodeSelector.nextMatch(
        getEditor().getParentGraph(), currSelection);
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
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    for (GraphNode node : extension) {
      selectedNodesContent.add(node);
    }
    selectedNodes.refresh(false);
    refreshPreview();
  }

  /**
   * Remove the given nodes from the list of selected nodes.
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    for (GraphNode node : reduction) {
      selectedNodesContent.remove(node);
    }
    selectedNodes.refresh(false);
    refreshPreview();
  }

  /**
   * Set the list of selected nodes to contains exactly the given set of nodes.
   */
  @Override
  public void updateSelectionTo(Collection<GraphNode> selection) {
    selectedNodesContent.clear();
    updateSelectedExtend(selection);
  }
}
