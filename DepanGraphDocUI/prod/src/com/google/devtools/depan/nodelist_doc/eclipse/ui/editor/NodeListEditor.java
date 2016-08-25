/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.editor;

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.CheckNodeTreeView;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer.HierarchyChangeListener;
import com.google.devtools.depan.graph_doc.GraphDocLogger;
import com.google.devtools.depan.graph_doc.eclipse.ui.editor.GraphEditorNodeViewProvider;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResourceBuilder;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.eclipse.ui.widgets.FromGraphDocListControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListDocXmlPersist;
import com.google.devtools.depan.persistence.StorageTools;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import java.util.Collection;

/**
 * Show the subset of nodes in a NodeList.  Allow the user
 * to select interesting subsets for more detailed investigation.
 * 
 * Based on version of {@code GraphEditor}, {@code ViewEditor},
 * and {@code RelationDisplay Editor}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditor";

  private static final boolean RECURSIVE_SELECT_DEFAULT = true;

  /////////////////////////////////////
  // Editor input data

  private IFile file;

  private String baseName;

  private NodeListDocument nodeListInfo;

  // Reported to isDirty for unsaved nodeLists.
  private boolean needsSave;

  /////////////////////////////////////
  // Derived details

  private GraphResources graphResources;

  private HierarchyCache<GraphNode> hierarchies;

  /////////////////////////////////////
  // UX Elements

  private HierarchyViewer<GraphNode> hierarchyView;

  private Button recursiveSelect;

  private FromGraphDocListControl fromGraphDoc;

  private CheckNodeTreeView checkNodeTreeView;

  /////////////////////////////////////
  // Public methods

  /**
   * A node list document is not editable, so it is never dirty.
   * However, it is not saved on initial construction.
   */
  @Override
  public boolean isDirty() {
    return needsSave;
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    initFromInput(input);
    initDetails();
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    if (null == file) {
      IFile saveFile = doSaveAsDialog();
      if (null == saveFile) {
        return;
      }
      file = saveFile;
    }

    persistDocument(monitor);
    handleDocumentChange(file);
  }

  @Override
  public void doSaveAs() {
    IFile saveFile = doSaveAsDialog();
    if (null == saveFile) {
      return;
    }
    file = saveFile;
    persistDocument(null);
    handleDocumentChange(file);
  }

  private IFile doSaveAsDialog() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    IFile saveAs = getSaveAsFile();
    saveas.setOriginalFile(saveAs);
    saveas.setOriginalName(saveAs.getName());
    if (saveas.open() != SaveAsDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    IFile saveFile = WorkspaceTools.calcViewFile(
        saveas.getResult(), NodeListDocument.EXTENSION);

    return saveFile;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  /////////////////////////////////////
  // Editor setup

  private void initFromInput(IEditorInput input) throws PartInitException {
    if (input instanceof NodeListEditorInput) {
      NodeListEditorInput docInput = (NodeListEditorInput) input;
      file = null;
      nodeListInfo = docInput.getNodeListDocument();
      baseName = docInput.getBaseName();
      setPartName(docInput.getName());
      needsSave = true;
    } else if (input instanceof IFileEditorInput) {
      IFileEditorInput fileInput = (IFileEditorInput) input;
      file = fileInput.getFile();

      NodeListDocXmlPersist persist = NodeListDocXmlPersist.buildForLoad(file);
      nodeListInfo = persist.load(file.getRawLocationURI());

      baseName = buildFileInputBaseName(file);
      setPartName(fileInput.getName());
      needsSave = false;
    } else {
      // Something unexpected
      throw new PartInitException(
          "Input is not suitable for the NodeList editor.");
    }
  }

  private void initDetails() {
    graphResources = GraphResourceBuilder.forModel(
        nodeListInfo.getDependencyModel());

    hierarchies = new HierarchyCache<GraphNode>(
        NodeTreeProviders.GRAPH_NODE_PROVIDER,
        nodeListInfo.getNodeListGraph());
  }

  private IFile getSaveAsFile() {
    IFile infoFile = getInputFile();
    if (null != infoFile) {
      return infoFile;
    }

    IContainer parent = nodeListInfo.getReferenceLocation().getParent();
    String filebase = baseName + '.' + NodeListDocument.EXTENSION;
    String filename = StorageTools.guessNewFilename(
        parent, filebase, 1, 10);

    IPath filePath = Path.fromOSString(filename);
    return parent.getFile(filePath);
  }

  private IFile getInputFile() {
    IEditorInput input = getEditorInput();
    if (input instanceof IFileEditorInput) {
      return ((IFileEditorInput) input).getFile();
    }

    return null;
  }

  private String buildFileInputBaseName(IFile file) {
    IPath result = Path.fromOSString(file.getName());
    return result.removeFileExtension().toOSString();
  }

  /**
   * Set the dirtyState for <code>this</code> editor.
   */
  private void setDirtyState(boolean dirty) {
    if (dirty != isDirty()) {
      this.needsSave = dirty;
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }

  private void handleDocumentChange(IFile file) {
    setPartName(file.getName());

    FileEditorInput effInput = new FileEditorInput(file);
    setInputWithNotify(effInput);
  }

  /**
   * Save the current {@link RelationDisplayDocument} at the supplied
   * location.
   */
  private void persistDocument(IProgressMonitor monitor) {
    NodeListDocXmlPersist persist = NodeListDocXmlPersist.buildForSave();
    StorageTools.saveDocument(file, nodeListInfo, persist, monitor);

    setDirtyState(false);
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  public void createPartControl(Composite parent) {
    createPage(parent);
    handleHierarchyChanged();
  }

  private void createPage(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    Composite controls = setupControls(result);
    controls.setLayoutData(Widgets.buildHorzFillData());

    checkNodeTreeView = setupTree(result);
    checkNodeTreeView.setLayoutData(Widgets.buildGrabFillData());
    checkNodeTreeView.setRecursive(recursiveSelect.getSelection());
  }

  private Composite setupControls(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);

    RowLayout toplayout = new RowLayout();
    toplayout.fill = true;
    toplayout.pack = true;
    toplayout.wrap = true;
    toplayout.type = SWT.HORIZONTAL;
    result.setLayout(toplayout);

    hierarchyView = createHierarchyViewer(result);

    // recursive select options
    recursiveSelect = new Button(result, SWT.CHECK);
    recursiveSelect.setText("Recursive select in tree");
    recursiveSelect.setSelection(RECURSIVE_SELECT_DEFAULT);
    recursiveSelect.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setRecursiveSelect(recursiveSelect.getSelection());
      }
    });

    Button create = new Button(result, SWT.PUSH);
    create.setText("Create");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        try {
          createContentEditor();
        } catch (IllegalArgumentException ex) {
          // bad layout. don't do anything for the layout, but still finish the
          // creation of the view.
          GraphDocLogger.LOG.warning("Bad layout selected.");
        } catch (Exception errView) {
          GraphDocLogger.logException("Unable to create view", errView);
        }
      }
    });

    fromGraphDoc = new FromGraphDocListControl(result);
    return result;
  }

  private CheckNodeTreeView setupTree(Composite parent) {
    CheckNodeTreeView result = new CheckNodeTreeView(parent);
    return result;
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

  /////////////////////////////////////
  // Create Views

  /**
   * Create a new content editor from the selected tree elements
   * and other {@code GraphEditor} settings.
   */
  private void createContentEditor() {
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
    String detail = FromGraphDocWizard.calcDetailName(topNode);
    wizard.init(
        file, nodeListInfo.getGraphDocument(), graphResources, nodes, detail);

    // Run the wizard.
    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  /////////////////////////////////////
  // Run the new ViewEditor

  /**
   * Activate a new ViewEditor.
   * 
   * This is an asynchronous activate, as the new editor will execute
   * separately from the other workbench windows.
   */
  public static void startNodeListEditor(NodeListEditorInput input) {
    WorkspaceTools.asyncExec(new NodeListEditorRunnable(input));
  }

  private static class NodeListEditorRunnable implements Runnable {
    private final NodeListEditorInput input;

    private NodeListEditorRunnable(NodeListEditorInput input) {
      this.input = input;
    }

    @Override
    public void run() {
      IWorkbenchPage page = PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage();
      try {
        page.openEditor(input, NodeListEditor.ID);
      } catch (PartInitException errInit) {
        GraphDocLogger.logException("Unable to start NodeListEditor", errInit);
      }
    }
  }
}
