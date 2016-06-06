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
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.graph_doc.GraphDocLogger;
import com.google.devtools.depan.graph_doc.eclipse.ui.registry.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.registry.FromGraphDocRegistry;
import com.google.devtools.depan.graph_doc.eclipse.ui.registry.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.eclipse.ui.widgets.FromGraphDocListControl;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.ResourceCache;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEditor
    extends MultiPageEditorPart
    implements NodeTreeProvider<GraphNode> {

  public static final String ID =
      "com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditor";

  private IFile file = null;
  private GraphDocument graph = null;

  private CheckboxTreeViewer tree = null;
  private List associatedViews = null;
  private boolean recursiveTreeSelect = true;
  private CheckNodeTreeView<GraphNode> checkNodeTreeView = null;

  /////////////////////////////////////
  // UX Components
  private FromGraphDocListControl fromGraphDoc;

  // TODO(leeca): Figure out how to turn this back on
  // private Binop<GraphModel> binop = null;

  private HierarchyViewer<GraphNode> hierarchyView = null;

  private HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // Access state

  protected void setRecursiveSelect(boolean state) {
    this.recursiveTreeSelect = state;
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
    recursiveSelect.setSelection(recursiveTreeSelect);
    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setRecursiveSelect(recursiveSelect.getSelection());
      }
    });

    // TODO: Launch installable sub-editors
    // Composite layoutRegion = setupLayoutChoice(top);
    // layoutChoices = setupLayoutChoices(top);

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

    Map<String, FromGraphDocContributor> contribs =
        FromGraphDocRegistry.getRegistryContributionMap();
    fromGraphDoc =
        new FromGraphDocListControl(top, contribs);

    // tree --------------------
    checkNodeTreeView = new CheckNodeTreeView<GraphNode>(
        composite, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);

    tree = checkNodeTreeView.getCheckboxTreeViewer();
    tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    tree.addCheckStateListener(new ICheckStateListener() {
      @Override
      public void checkStateChanged(CheckStateChangedEvent event) {
        if (recursiveTreeSelect) {
          tree.setSubtreeChecked(event.getElement(), event.getChecked());
        }
      }
    });
    handleHierarchyChanged();

    int index = addPage(composite);
    setPageText(index, "New View");
  }

//  private LayoutChoicesControl setupLayoutChoices(Composite parent) {
//    LayoutChoicesControl result = new LayoutChoicesControl(
//        parent, LayoutChoicesControl.Style.LINEAR);
//    result.setLayoutChoices(LayoutGenerators.getLayoutNames(false));
//
//    GraphEdgeMatcherDescriptor edgeMatcher = getDefaultEdgeMatcher();
//    java.util.List<GraphEdgeMatcherDescriptor> choices =
//        GraphEdgeMatcherDescriptors.buildGraphChoices(graph);
//    result.setEdgeMatcherInput(edgeMatcher, choices);
//
//    return result;
//  }

  private HierarchyViewer<GraphNode> createHierarchyViewer(Composite parent) {
    HierarchyViewer<GraphNode> result = new HierarchyViewer<GraphNode>(parent, false);

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

  /**
   * Really should have separate edge matchers from default display
   * relation.
   * 
   * TODO: Separate hierarchy edge matcher from display relation set.
   */
  private GraphEdgeMatcherDescriptor getDefaultEdgeMatcher() {
    return GraphEdgeMatcherDescriptors.FORWARD;
  }

  private java.util.List<GraphEdgeMatcherDescriptor> getEdgeMatcherChoices() {
    return Arrays.asList(
        GraphEdgeMatcherDescriptors.FORWARD,
        GraphEdgeMatcherDescriptors.EMPTY);
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

  /**
   * Create a new Graph Visualization editor from the selected tree elements
   * and other {@code GraphEditor} settings.
   */
  protected void createViewEditor() {
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
    FromGraphDocContributor choice = fromGraphDoc.getFromGraphDocChoice();
    if (null == choice) {
      return;
    }
    FromGraphDocWizard wizard = choice.newWizard();
    wizard.init(file, graph, topNode, nodes);

    // Run the wizard.
    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

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

    hierarchies = new HierarchyCache<GraphNode>(this, graph.getGraph());
    handleHierarchyChanged();

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

  @Override
  public GraphNode getObject(GraphNode node) {
    return node;
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
    checkNodeTreeView.updateData(graphData);
    GraphDocLogger.LOG.info("  DONE");
  }
}
