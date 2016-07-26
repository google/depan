/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.CheckNodeTreeView;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer.HierarchyChangeListener;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.graph_doc.GraphDocLogger;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResourceBuilder;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.eclipse.ui.widgets.FromGraphDocListControl;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.ResourceCache;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import java.util.Collection;

/**
 * Show the entire set of nodes from an analysis tree.  Allow the user
 * to select interesting subsets for more detailed investigation.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEditor extends MultiPageEditorPart {

  public static final String ID =
      "com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor";

  private static final boolean RECURSIVE_SELECT_DEFAULT = true;

  private IFile file = null;

  private GraphDocument graph = null;

  private List associatedViews = null;

  /////////////////////////////////////
  // UX Elements

  private CheckNodeTreeView checkNodeTreeView = null;

  private FromGraphDocListControl fromGraphDoc;

  // TODO(leeca): Figure out how to turn this back on
  // private Binop<GraphModel> binop = null;

  private HierarchyViewer<GraphNode> hierarchyView = null;

  private HierarchyCache<GraphNode> hierarchies;

  private GraphResources graphResources;

  /////////////////////////////////////
  // Public methods

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    super.init(site, input);
    if (!(input instanceof IFileEditorInput)) {
      throw new PartInitException("Invalid Input: Must be IFileEditorInput");
    }

    // load the graph
    file = ((IFileEditorInput) input).getFile();

    GraphDocLogger.LOG.info("Reading " + file.getRawLocationURI());
    graph = ResourceCache.fetchGraphDocument(file);
    GraphDocLogger.LOG.info("  DONE");

    hierarchies = new HierarchyCache<GraphNode>(
        NodeTreeProviders.GRAPH_NODE_PROVIDER, graph.getGraph());
    handleHierarchyChanged();

    DependencyModel model = graph.getDependencyModel();
    graphResources = GraphResourceBuilder.forModel(model);

    // set the title to the filename, excepted the file extension
    String title = file.getName();
    title = title.substring(0, title.lastIndexOf('.'));
    this.setPartName(title);
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
  }

  @Override
  public void doSaveAs() {
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected void createPages() {
    createPage0();
    createPage1();
  }

  private void createPage0() {
    Composite composite = new Composite(getContainer(), SWT.NONE);

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    composite.setLayout(layout);

    // top panel ---------------
    Composite top = new Composite(composite, SWT.NONE);
    top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    RowLayout toplayout = new RowLayout();
    toplayout.fill = true;
    toplayout.pack = true;
    toplayout.wrap = true;
    toplayout.type = SWT.HORIZONTAL;
    top.setLayout(toplayout);

    hierarchyView = createHierarchyViewer(top);

    // recursive select options
    final Button recursiveSelect = new Button(top, SWT.CHECK);
    recursiveSelect.setText("Recursive select in tree");
    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setRecursiveSelect(recursiveSelect.getSelection());
      }
    });

    Button create = new Button(top, SWT.PUSH);
    create.setText("Create view");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          createViewEditor();
        } catch (IllegalArgumentException ex) {
          // bad layout. don't do anything for the layout, but still finish the
          // creation of the view.
          GraphDocLogger.LOG.warning("Bad layout selected.");
        } catch (Exception errView) {
          GraphDocLogger.logException("Unable to create view", errView);
        }
      }
    });

    fromGraphDoc = new FromGraphDocListControl(top);

    // tree --------------------
    checkNodeTreeView = new CheckNodeTreeView(composite);
    checkNodeTreeView.setLayoutData(Widgets.buildGrabFillData());

    recursiveSelect.setSelection(RECURSIVE_SELECT_DEFAULT);
    checkNodeTreeView.setRecursive(recursiveSelect.getSelection());
    handleHierarchyChanged();

    int index = addPage(composite);
    setPageText(index, "New View");
  }

  private HierarchyViewer<GraphNode> createHierarchyViewer(Composite parent) {
    HierarchyViewer<GraphNode> result = 
        new HierarchyViewer<GraphNode>(parent, false);

    GraphEdgeMatcherDescriptor selectedRelSet = getDefaultEdgeMatcher();
    java.util.List<GraphEdgeMatcherDescriptor> choices = getEdgeMatcherChoices();
    result.setInput(hierarchies, selectedRelSet, choices);

    result.addChangeListener(new HierarchyChangeListener() {

        @Override
        public void hierarchyChanged() {
          handleHierarchyChanged(); 
        }
      });
    return result;
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

    associatedViews = new List(
        leftPanel, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    associatedViews.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    // fill associated Views list.
    updateList();
    associatedViews.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectView();
      }
    });

    // Right panel --------------
    // TODO(leeca): Figure out how to turn this back on
    // binop = new Binop<GraphModel>(composite, SWT.None, this, this);
    // binop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    int index = addPage(composite);
    setPageText(index, "Opened related Views");
  }

  /////////////////////////////////////
  // UX Actions

  private void setRecursiveSelect(boolean state) {
    checkNodeTreeView.setRecursive(state);
  }

  private void handleHierarchyChanged() {
    if (null == checkNodeTreeView) {
      return;
    }
    if (null == hierarchyView) {
      return;
    }

    GraphDocLogger.LOG.info("Initialize graph...");
    GraphData<GraphNode> graphData = hierarchyView.getGraphData();

    GraphEditorNodeViewProvider<GraphNode> provider =
        new GraphEditorNodeViewProvider<GraphNode>(graphData);
    checkNodeTreeView.setNvProvider(provider);
    checkNodeTreeView.refresh();

    GraphDocLogger.LOG.info("  DONE");
  }

  /////////////////////////////////////
  // Support methods

  /**
   * Really should have separate edge matchers from default display
   * relation.
   * 
   * TODO: Separate hierarchy edge matcher from display relation set.
   */
  private GraphEdgeMatcherDescriptor getDefaultEdgeMatcher() {
    return graphResources.getDefaultEdgeMatcher();
  }

  private java.util.List<GraphEdgeMatcherDescriptor> getEdgeMatcherChoices() {
    return graphResources.getEdgeMatcherChoices();
  }

  protected void selectView() {
/* TODO(leeca):  Need richer ReferencedGraphModel
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
*/
  }

  private void updateList() {
/* TODO(leeca):  Need richer ReferencedGraphModel
    associatedViews.removeAll();
    for (ViewModel v : graph.getViews()) {
      associatedViews.add(v.getName());
    }
    associatedViews.redraw();
*/
  }

  /////////////////////////////////////
  // Create Views

  /**
   * Create a new Graph Visualization editor from the selected tree elements
   * and other {@code GraphEditor} settings.
   */
  private void createViewEditor() {
    GraphNode topNode = checkNodeTreeView.getFirstNode();
    if (null == topNode) {
      GraphDocLogger.LOG.info("no topNode");
      return;
    }

    Collection<GraphNode> nodes = checkNodeTreeView.getSelectedNodes();
    if (nodes.isEmpty()) {
      GraphDocLogger.LOG.info("empty nodes");
      return;
    }

    runGraphFromDocWizard(topNode, nodes);
  }

  private void runGraphFromDocWizard(
      GraphNode topNode, Collection<GraphNode> nodes) {

    // Prepare the wizard.
    FromGraphDocContributor choice = fromGraphDoc.getChoice();
    if (null == choice) {
      return;
    }
    FromGraphDocWizard wizard = choice.newWizard();
    wizard.init(file, graph, graphResources, topNode, nodes);

    // Run the wizard.
    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }
}
