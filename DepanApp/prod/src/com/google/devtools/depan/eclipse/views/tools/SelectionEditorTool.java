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

import com.google.devtools.depan.collect.Sets;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.editors.ViewEditorInput;
import com.google.devtools.depan.eclipse.utils.NodeLabelProvider;
import com.google.devtools.depan.eclipse.utils.RelationshipPicker;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Sasher;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.util.Collection;

/**
 * Tool for expanding a selection with a set of relationships, and create a new
 * view.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class SelectionEditorTool extends ViewSelectionListenerTool {

  private TableContentProvider<GraphNode> selectedNodesContent = null;
  private TableContentProvider<GraphNode> previewListContent = null;

  private TableViewer previewList;
  private TableViewer selectedNodes;

  private CCombo modeChoice = null;

  private CCombo layoutChoice = null;

  private Composite parentWidget = null;

  /**
   * The picker where users can specify what filters to apply to the selected
   * nodes.
   */
  private RelationshipPicker relationshipPicker = null;

  /**
   * Provides the interface to create a Path Expression using a list of
   * filters.
   */
  private PathExpressionEditorTool pathExpressionEditor = null;

  /**
   * Holds the relationship picker and the path expression editor.
   */
  private TabFolder tabFolder = null;

  // some strings used in the UI.
  private static final String MODE_EXTENDS = "Extend graph";
  private static final String MODE_SELECTED = "Selected + New nodes";
  private static final String MODE_RESULTS = "Only nodes in results";
  private static final String MODE_NEW = "Only new nodes";
  private static final String LAYOUT_KEEP = "Keep positions";

  /**
   * Recursively apply the extension with selected relations.
   */
  private boolean recursiveSearch = false;

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
    this.parentWidget = parent;
    // create components
    Composite baseComposite = new Composite(parent, SWT.NONE);

    Sasher outerSash = new Sasher(baseComposite, SWT.NONE);
    Sasher innerSash = new Sasher(outerSash, SWT.NONE);
    Control selectedNodesControl = setupSelectedNodesContent(innerSash);

    // create the tab folder that will hold relationshipPicker and
    // pathExpressionEditor
    tabFolder = new TabFolder(innerSash, SWT.BORDER);

    // this panel holds the RelationshipPicker object and a check box that
    // determines recursiveness.
    Composite relationshipPickerPanel = new Composite(tabFolder, SWT.NONE);
    relationshipPickerPanel.setLayout(new GridLayout());

    relationshipPicker = new RelationshipPicker();
    Control relationTable =
        relationshipPicker.getControl(relationshipPickerPanel);
    relationTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    final Button recursiveSelect =
        new Button(relationshipPickerPanel, SWT.CHECK);

    pathExpressionEditor = new PathExpressionEditorTool(tabFolder, SWT.NONE);

    // Create tabs
    createTabItem(tabFolder, "Relation Picker Tool", relationshipPickerPanel);
    createTabItem(tabFolder, "Path Expression Tool", pathExpressionEditor);

    Control previewListControl = setupPreviewList(outerSash);

    new Label(baseComposite, SWT.NONE).setText("Creation mode");
    modeChoice = new CCombo(baseComposite, SWT.READ_ONLY | SWT.BORDER);
    new Label(baseComposite, SWT.NONE).setText("Apply layout");
    layoutChoice = new CCombo(baseComposite, SWT.READ_ONLY | SWT.BORDER);

    Composite actionButtons = new Composite(baseComposite, SWT.NONE);
    Button preview = new Button(actionButtons, SWT.PUSH);
    Button justSelect = new Button(actionButtons, SWT.PUSH);
    Button doit = new Button(actionButtons, SWT.PUSH);

    // mode
    // warning: if you change the order of the combo items, change the indexes
    // in preview() !
    modeChoice.setItems(
        new String[] {MODE_EXTENDS, MODE_SELECTED, MODE_RESULTS, MODE_NEW});
    modeChoice.select(0);

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
    innerSash.init(selectedNodesControl, tabFolder, SWT.HORIZONTAL, 25);
    outerSash.init(innerSash, previewListControl, SWT.HORIZONTAL, 75);
    int minSasherSize = 20;
    innerSash.setLimit(minSasherSize);
    outerSash.setLimit(2 * minSasherSize);

    // actions buttons
    recursiveSelect.setText("Recursive apply expansion");
    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        recursiveSearch = recursiveSelect.getSelection();
      }
    });

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
    recursiveSelect.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    modeChoice.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
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

  /**
   * Creates a new <code>TabItem</code> that is owned by <code>owner</code>, has
   * given label and contains the specified control.
   *
   * @param owner Owner this <code>TabItem</code> belongs to.
   * @param label Text displayed on the tab.
   * @param content The <code>Control</code> that will be shown in this tab.
   * @return The new <code>TabItem</code>.
   */
  private TabItem createTabItem(
      TabFolder owner, String label, Control content) {
    TabItem newTabItem = new TabItem(owner, SWT.NONE);
    newTabItem.setText(label);
    newTabItem.setControl(content);
    return newTabItem;
  }

  @SuppressWarnings("unchecked")
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

  @SuppressWarnings("unchecked")
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

    // open the editor GUI.
    parentWidget.getDisplay().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage pge = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          IEditorPart openEditor = pge.openEditor(input, ViewEditor.ID);
          if (openEditor instanceof ViewEditor
              && layoutChoice.getSelectionIndex() > 0) {
            Layouts l = getLayoutChoice();
            View view = ((ViewEditor) openEditor).getView();
            view.applyLayout(l, relationshipPicker.getRelationShips());
          }
        } catch (PartInitException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException ex) {
          // bad layout. don't do anything for the layout, but still finish
          // the creation of the view.
          System.err.println("Bad layout.");
        }
      }
    });
  }

  /**
   * Creates and returns the associated <code>PathMatcher</code> object
   * depending on which tab is selected.
   *
   * @return <code>PathMatcher</code> object that will filter the nodes in the
   * view.
   */
  private PathMatcher getPathMatcherModel() {
    int selectionIndex = tabFolder.getSelectionIndex();
    if (tabFolder.getItem(selectionIndex).getControl() !=
        pathExpressionEditor) {
      relationshipPicker.createPathMatcherModel(recursiveSearch);
      return relationshipPicker.getPathMatcherModel();
    }
    // it has to be PathExpression then!
    pathExpressionEditor.createPathMatcherModel();
    return pathExpressionEditor.getPathMatcherModel();
  }

  protected void refreshPreview() {
    MultipleDirectedRelationFinder finder =
        relationshipPicker.getRelationShips();

    Collection<GraphNode> newSet =
        Sets.newHashSet(selectedNodesContent.getObjects());

    newSet = getPathMatcherModel().nextMatch(
        getViewModel().getParentGraph(), newSet);

    previewListContent.clear();
    for (GraphNode node : newSet) {
      previewListContent.add(node);
    }

    // add other nodes if necessary
    if (modeChoice.getSelectionIndex() == 0) {
      // combine. Add all nodes from current view
      for (GraphNode node : getViewModel().getNodes()) {
        previewListContent.add(node);
      }
    } else if (modeChoice.getSelectionIndex() == 1) {
      // add selected nodes
      for (GraphNode node : selectedNodesContent.getObjects()) {
        previewListContent.add(node);
      }
    } else if (modeChoice.getSelectionIndex() == 2) {
      // do nothing, just show nodes in the result set.
    } else if (modeChoice.getSelectionIndex() == 3) {
      // remove selected nodes, we just want new nodes
      for (GraphNode node : selectedNodesContent.getObjects()) {
        previewListContent.remove(node);
      }
    }
    previewList.refresh(false);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool
   *      #emptySelection()
   */
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
