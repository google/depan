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

package com.google.devtools.depan.eclipse.editors;

import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.trees.CheckNodeTreeView;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.trees.NodeTreeProvider;
import com.google.devtools.depan.eclipse.trees.NodeTreeView.NodeWrapper;
import com.google.devtools.depan.eclipse.utils.DefaultRelationshipSet;
import com.google.devtools.depan.eclipse.utils.LayoutSelector;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.RelationshipSetSelector;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.interfaces.GraphListener;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.ViewModel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import java.io.File;
import java.net.URI;
import java.util.Set;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class GraphEditor
    extends MultiPageEditorPart
    implements TCreator<ViewModel>,
        NodeTreeProvider<GraphNode>,
        RelationshipSelectorListener,
        GraphListener {

  private GraphModel graph = null;
  private URI uri = null;
  private CheckboxTreeViewer tree = null;
  private List associatedViews = null;
  private Binop<ViewModel> binop = null;
  private boolean recursiveTreeSelect = false;
  private CheckNodeTreeView<GraphNode> checkNodeTreeView = null;
  private LayoutSelector layoutChoice = null;

  /**
   * Selector for named relationships sets.
   */
  private RelationshipSetSelector relationshipSetselector = null;

  private HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // Access state

  protected void setRecursiveSelect(boolean state) {
    this.recursiveTreeSelect = state;
  }

  protected Layouts getSelectedLayout() {
    return layoutChoice.getSelectedLayout();
  }

  /////////////////////////////////////
  // Create visual controls

  @Override
  protected void createPages() {
    createPage0();
    createPage1();
  }

  private void createPage0() {
    Composite composite = new Composite(getContainer(), SWT.NONE);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    layout.numColumns = 1;

    // top panel ---------------
    Composite top = new Composite(composite, SWT.NONE);
    top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    RowLayout toplayout = new RowLayout();
    toplayout.fill = true;
    toplayout.pack = false;
    toplayout.wrap = true;
    toplayout.type = SWT.HORIZONTAL;
    top.setLayout(toplayout);

    relationshipSetselector = new RelationshipSetSelector(top);
    relationshipSetselector.selectSet(DefaultRelationshipSet.SET);

    // create layout choice selector
    layoutChoice = new LayoutSelector(top);

    Button create = new Button(top, SWT.PUSH);
    create.setText("Create view");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          Layouts layout = getSelectedLayout();
          createView(layout);
        } catch (IllegalArgumentException ex) {
          // bad layout. don't do anything for the layout, but still finish the
          // creation of the view.
          System.err.println("Bad layout selected.");
        }
      }
    });

    // recursive select options
    final Button recursiveSelect = new Button(top, SWT.CHECK | SWT.BORDER);
    recursiveSelect.setText("Recursive select in tree");
    recursiveSelect.setSelection(recursiveTreeSelect);
    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setRecursiveSelect(recursiveSelect.getSelection());
      }
    });

    // tree --------------------
    System.out.println("Initialize tree...");
    RelationshipSet relSet = relationshipSetselector.getSelection();
    checkNodeTreeView = new CheckNodeTreeView<GraphNode>(
        composite, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);
    selectedSetChanged(relSet);
    System.out.println("  DONE");
    relationshipSetselector.addChangeListener(this);

    tree = checkNodeTreeView.getCheckboxTreeViewer();
    tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    tree.setSorter(new NodeWrapperTreeSorter());
    tree.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        if (recursiveTreeSelect) {
          tree.setSubtreeChecked(event.getElement(), event.getChecked());
        }
      }
    });

    int index = addPage(composite);
    setPageText(index, "New View");
  }

  private void createPage1() {
    Composite composite = new Composite(getContainer(), SWT.NONE);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    layout.numColumns = 2;
    layout.makeColumnsEqualWidth = true;

    Composite leftPanel = new Composite(composite, SWT.NONE);
    GridLayout leftLayout = new GridLayout();
    leftPanel.setLayout(leftLayout);
    leftLayout.numColumns = 1;
    leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Button refresh = new Button(leftPanel, SWT.PUSH);
    refresh.setText("Refresh list");
    refresh.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updateList();
      }
    });

    associatedViews =
      new List(leftPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    associatedViews.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    // fill associated Views list.
    updateList();
    associatedViews.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectView();
      }
    });

    // Right panel --------------
    binop = new Binop<ViewModel>(composite, SWT.None, this, this);
    binop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    int index = addPage(composite);
    setPageText(index, "Opened related Views");
  }


  protected void selectView() {
    associatedViews.getSelectionIndices();
    if (associatedViews.getSelectionCount() == 1) {
      for (ViewModel v : graph.getViews()) {
        if (v.getName().equals(associatedViews.getSelection()[0])) {
          binop.setFirst(v);
        }
      }
    } else if (associatedViews.getSelectionCount() == 2) {
      ViewModel v1 = null;
      ViewModel v2 = null;
      for (ViewModel v : graph.getViews()) {
        if (v.getName().equals(associatedViews.getSelection()[0])) {
          v1 = v;
        } else if (v.getName().equals(associatedViews.getSelection()[1])) {
          v2 = v;
        }
      }
      binop.setBoth(v1, v2);
    }
  }

  private void updateList() {
    associatedViews.removeAll();
    for (ViewModel v : graph.getViews()) {
      associatedViews.add(v.getName());
    }
    associatedViews.redraw();
  }

  @SuppressWarnings("unchecked")
  protected void createView(Layouts layout) {
    Set<GraphNode> nodes = Sets.newHashSet();
    CheckboxTreeViewer treeView = checkNodeTreeView.getCheckboxTreeViewer();

    for (Object o : treeView.getCheckedElements()) {
      if (o instanceof NodeWrapper) {
        nodes.add(((NodeWrapper) o).getNode());
      }
    }
    if (nodes.size() <= 0) {
      return;
    }

    final ViewModel viewModel = new ViewModel(graph);
    viewModel.setNodes(nodes);

    create(viewModel, layout);
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    super.init(site, input);
    if (!(input instanceof IFileEditorInput)) {
      throw new PartInitException("Invalid Input: Must be IFileEditorInput");
    }

    // load the graph
    uri = ((IFileEditorInput) input).getFile().getRawLocationURI();

    System.out.println("Reading " + uri);

    graph = ResourceCache.fetchGraphModel(uri);

    System.out.println("  DONE");

    hierarchies = new HierarchyCache<GraphNode>(this, graph);
    if (null != checkNodeTreeView) {
      System.out.println("Initialize graph...");
      selectedSetChanged(DefaultRelationshipSet.SET);
      System.out.println("  DONE");
    }

    // set the title to the filename, excepted the file extension
    String title = new File(uri).getName();
    title = title.substring(0, title.lastIndexOf('.'));
    this.setPartName(title);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart
   *      #doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {

  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {

  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  public void create(ViewModel view) {
    create(view, getSelectedLayout());
  }

  /**
   * Create a new Graph Visualization editor from the selected tree elements.
   *
   * @param view Source of dependency data
   * @param layout Initial layout for visualization
   */
  public void create(ViewModel view, Layouts layout) {
    final ViewEditorInput input = new ViewEditorInput(view, layout, uri);
    ViewEditor.startViewEditor(input);
    updateList();
  }

  public GraphNode getObject(GraphNode node) {
    return node;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.model.interfaces.GraphListener
   *      #newView(com.google.devtools.depan.view.ViewModel)
   */
  public void newView(ViewModel view) {
    updateList();
  }

  /**
   * Callback when display property of a node is modified.
   *
   * @param node The node whose display property is modified.
   * @param property The display property that holds the modifications.
   */
  @Override
  public void nodePropertyChanged(
      GraphNode node, NodeDisplayProperty property) {
    // No actions needed on node changes.
  }

  @Override
  public void edgePropertyChanged(
      GraphEdge edge, EdgeDisplayProperty property) {
    // No actions needed on edge changes.
  }

  @Override
  public void selectedSetChanged(RelationshipSet relSet) {
    GraphData<GraphNode> hierarchy = hierarchies.getHierarchy(relSet);
    checkNodeTreeView.updateData(hierarchy);
  }
}
