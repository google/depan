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

package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.collapse.model.CollapseTreeModel;
import com.google.devtools.depan.collapse.model.Collapser;
import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.TreeViewerObject;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.ogl.EdgeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRenderingProperty;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererChangeListener;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererEvent;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererEvents;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.SteppingFilter;
import com.google.devtools.depan.nodes.trees.HierarchicalTreeModel;
import com.google.devtools.depan.nodes.trees.SuccessorEdges;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.nodes.trees.Trees;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.resources.DirectDocumentReference;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtensionRegistry;
import com.google.devtools.depan.view_doc.eclipse.ui.trees.NodeCompactor;
import com.google.devtools.depan.view_doc.eclipse.ui.trees.ViewEditorNodeViewerProvider;
import com.google.devtools.depan.view_doc.eclipse.ui.views.NodeFilterViewPart;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutUtil;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.ExtensionData;
import com.google.devtools.depan.view_doc.model.ExtensionDataListener;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;
import com.google.devtools.depan.view_doc.model.OptionPreferences;
import com.google.devtools.depan.view_doc.model.Point2dUtils;
import com.google.devtools.depan.view_doc.model.ScenePreferences;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;
import com.google.devtools.depan.view_doc.persistence.ViewDocXmlPersist;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * An Editor for a DepAn ViewDocument.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditor extends MultiPageEditorPart {

  public static final String ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor";

  // TODO: Expected to evolve ..
  private static final LayoutPlan[] OGL_LAYOUTS =
      new LayoutPlan[] { GridLayoutPlan.GRID_LAYOUT_PLAN };

  // TODO: Could be user option
  public static final double ZOOM_IN_FACTOR = 1.1;

  // TODO: Could be user option
  public static final double ZOOM_OUT_FACTOR = 0.9;

  /////////////////////////////////////
  // Editor state for persistence

  /** State of the view.  Only this data is saved. */
  private ViewDocument viewInfo;

  /**
   * Standard resources for manipulating this graphs and its set of nodes
   * and relations.
   */
  private GraphResources viewResources;

  /**
   * Base name to use for created files. Typically set through the
   * {@link ViewEditorInput} supplied at editor startup. It does
   * not include the extension for {@link ViewDocument}s.
   */
  private String baseName;

  /**
   * {@link LayoutPlan} to use if the nodes arrive in the editor with
   * no locations.  This is typical for new {@link ViewDocument}s coming
   * from a {@link GraphDocument}. Typically set through the
   * {@link ViewEditorInput} supplied at editor startup.
   */
  private LayoutPlan initialLayout;

  /** Dirty state. */
  private boolean isDirty = true;

  /////////////////////////////////////
  // Resources to release in the dispose() method

  /** Handle changes to the user preferences, such a node locations. */
  private ViewPrefsListener viewPrefsListener;

  /** Notice changes to extension data, mostly to mark dirty. */
  private ExtensionDataListener extDataListener;

  /** Results from defining hierarchies over the nodes. */
  private HierarchyCache<NodeDisplayProperty> hierarchies;

  /** The visualization View that handles rendering. */
  private View renderer;

  /**
   * Forward only selection change events to interested parties.
   */
  private ListenerManager<SelectionChangeListener> selectionListeners =
      new ListenerManager<SelectionChangeListener>();

  private ListenerManager<ScenePreferences.Listener> sceneListeners =
      new ListenerManager<ScenePreferences.Listener>();

  private ListenerManager<DrawingListener> drawingListeners =
      new ListenerManager<DrawingListener>();

  /////////////////////////////////////
  // Alternate graph perspectives and derived data
  // used in various tools and viewers

  /**
   * A {@link GraphModel} derived from the {@link GraphNode}s explicitly
   * listed in the {@link ViewDocument}.  The {@link GraphEdge}s for are
   * {@code viewGraph} are inferred (and reused) from the {@link GraphEdge}s
   * defined in the {@link ViewDocument}'s referenced {@link GraphModel}.
   * 
   * This also defines the full set of nodes and edges that the OGL engine
   * is prepared to draw.  As subset of these, from the {@link #exposedGraph},
   * might actually be displayed.
   */
  private GraphModel viewGraph;

  //$ private NodeSupplierFactory nodeSupplierFactory;

  /**
   * The active {@link ContextualFilter} for this editing session.
   * This is not persisted as part of the {@link ViewDocument},
   * but it is maintained during the activation of a {@link ViewEditor}.
   * 
   * The Node Filter view allows this to be saved as a separate resource.
   */
  private SteppingFilter nodeFilter;

  /**
   * Handle details of compact node view, including collapser and
   * tree hierarchies.
   */
  private NodeCompactor compactor = new NodeCompactor(this);

  private NodeViewerProvider nvProvider =
      new ViewEditorNodeViewerProvider(this);

  /////////////////////////////////////
  // Node view compression

  private void updateExposedGraph() {
    Collection<GraphNode> nodes = viewInfo.getViewNodes();
    compactor.updateExposedNodes(nodes);
  }

  /**
   * Provide the {@link GraphModel} that is currently being rendered.
   * Some nodes or edges of the basis graph may be hidden, compressed,
   * or aliased.
   */
  public GraphModel getExposedGraph() {
    return compactor.getExposedGraph();
  }

  /**
   * Provide the {@link GraphNode}s that are currently being rendered.
   */
  public Collection<GraphNode> getExposedNodes() {
    return compactor.getExposedNodes();
  }

  public NodeViewerProvider getNodeViewProvider() {
    return nvProvider;
  }

  /**
   * Provides the results for the 
   * {@link ViewEditorNodeViewerProvider#buildViewerRoots()}
   * this is returned by {@link #getNodeViewProvider()}.
   */
  public ViewerRoot buildViewerRoot() {
    Collection<GraphNode> nodes = viewInfo.getViewNodes();
    PlatformObject[] roots = compactor.buildRoots(nodes);

    String label = buildNodeViewerLabel(roots.length, nodes.size());

    TreeViewerObject view = new TreeViewerObject(label, roots);
    return new ViewerRoot(new Object[] {view});
  }

  public Object findViewerNodeObject(GraphNode node) {
    return compactor.findNodeObject(node);
  }

  private String buildNodeViewerLabel(int rootCnt, int nodeCnt) {
    String name = getPartName();
    if (rootCnt < nodeCnt) {
      return MessageFormat.format(
          "{0} [{1} roots, {2} nodes]", name, rootCnt, nodeCnt);
    }

    // Everything is a root
    return MessageFormat.format(
        "{0} [{1} exposed nodes]", name, nodeCnt);
  }

  public  List<GraphEdgeMatcherDescriptor> getTreeDescriptors() {
    return viewInfo.getTreeDescriptors();
  }

  public void addNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    viewInfo.addNodeTreeHierarchy(matcher);
  }

  public void removeNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    viewInfo.removeNodeTreeHierarchy(matcher);
  }

  // TODO: Other tree descriptor operations:
  // 1) set tree descriptors (handles list reorder, etc.)

  /**
   * Provide an immutable view of the current {@link Collapser} state.
   */
  public CollapseTreeModel getCollapseTreeModel() {
    return new CollapseTreeModel(viewInfo.getCollapser());
  }

  public void collapseTreeHierarchy(TreeModel treeModel) {
    viewInfo.collapseTree(getViewGraph(), treeModel);
  }

  public void collapseNodesByHierarchy(
      Collection<GraphNode> nodes, GraphEdgeMatcherDescriptor matcher) {
    GraphModel treeGraph = GraphBuilders.buildFromNodes(
        getViewGraph(), nodes);
    TreeModel treeModel = new HierarchicalTreeModel(
        Trees.computeSpanningHierarchy(treeGraph, matcher.getInfo()));

    // OPEN: or, is 1st arg treeGraph??
    viewInfo.collapseTree(getViewGraph(), treeModel);
  }

  public void collapseNodeList(
      GraphNode master, Collection<GraphNode> children) {
    viewInfo.collapseNodeList(master, children);
  }

  public void uncollapseMasterNode(GraphNode master) {
    viewInfo.uncollapseMasterNode(master);
  }

  private void handleCollapseRendering(
      Collection<CollapseData> created,
      Collection<CollapseData> removed) {

      for (CollapseData data : removed) {
        GraphNode master = data.getMasterNode();

        // uncollapse every children
        for (GraphNode child : data.getChildrenNodes()) {
          renderer.unCollapse(child, master);
        }
      }

      for (CollapseData data : created) {
        GraphNode master = data.getMasterNode();

        // collapse each child under the parent
        for (GraphNode child : data.getChildrenNodes()) {
          renderer.collapseUnder(child, master);
        }
      }
    }

  /////////////////////////////////////
  // Dispatch errors to go our logger

  private abstract static class SimpleDispatcher<T>
      implements ListenerManager.Dispatcher<T> {

    @Override
    public void captureException(RuntimeException errAny) {
      ViewDocLogger.LOG.error("Listener dispatch failure", errAny);
    }
  }

  /////////////////////////////////////
  // Basic Getters and Setters

  /**
   * Provide the project to use for saving resource associated with this
   * instance.  Under normal circumstances, it's the same project that contains
   * the underlying view document.
   */
  public IProject getResourceProject() {
    IFile input = getInputFile();
    if (null != input) {
      return input.getProject();
    }

    return viewInfo.getGraphModelLocation().getProject();
  }

  public GraphModel getViewGraph() {
    return viewGraph;
  }

  public void addViewPrefsListener(ViewPrefsListener listener) {
    viewInfo.addPrefsListener(listener);
  }

  public void removeViewPrefsListener(ViewPrefsListener listener) {
    viewInfo.removePrefsListener(listener);
  }

  public GraphDocument getParentGraphDoc() {
    return viewInfo.getComponents().getParentGraph().getGraph();
  }

  public IResource getParentGraphPath() {
    return viewInfo.getComponents().getParentGraph().getLocation();
  }

  public GraphModel getParentGraph() {
    return viewInfo.getParentGraph();
  }

  public DependencyModel getDependencyModel() {
    return viewInfo.getDependencyModel();
  }

  public GraphResources getGraphResources() {
    return viewResources;
  }

  public Collection<Relation> getDisplayRelations() {
    return viewResources.getDisplayRelations();
  }

  public List<PropertyDocumentReference<RelationSetDescriptor>> getRelationSetsChoices() {
    return viewResources.getRelationSetsChoices();
  }

  public PropertyDocumentReference<RelationSetDescriptor>
      getDefaultRelationSet() {
    return viewResources.getDefaultRelationSet();
  }

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      getLayoutEdgeMatcherRef() {
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> result =
        viewInfo.getLayoutMatcherRef();
    if (null != result) {
      return result;
    }

    return viewResources.getDefaultEdgeMatcher();
  }

  public void setLayoutEdgeMatcherRef(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef) {
    viewInfo.setLayoutEdgeMatcher(matcherRef);
  }

  public ScenePreferences getScenePrefs() {
    return viewInfo.getScenePrefs();
  }

  public String getOption(String optionId) {
    return viewInfo.getOption(optionId);
  }

  public boolean isOptionChecked(String optionId) {
    String value = viewInfo.getOption(optionId);
    return OptionPreferences.isOptionChecked(optionId, value);
  }

  public void setOption(String optionId, String value) {
    viewInfo.setOption(optionId, value);
  }

  public void setBooleanOption(String optionId, boolean value) {
    viewInfo.setOption(optionId, OptionPreferences.booleanValue(value));
  }

  /**
   * Since the {@link NodeFilterViewPart} is always going to cast the result
   * to {@link SteppingFilter}, return that type anyway.
   * 
   * Conceptually, the {@link ViewEditor}'s node filter could be any
   * type derived from {@link ContextualFilter}.  In practice, editability
   * and the {@link SteppingFilter}'s ability to encapsulate any
   * {@link ContextualFilter} ensures that the concrete type will always be
   * {@link SteppingFilter}.
   */
  public SteppingFilter getActiveNodeFilter( ) {
    if (null == nodeFilter) {
      String name = MessageFormat.format("{0} node filters", baseName);
      nodeFilter = new SteppingFilter(name);
    }
    return nodeFilter;
  }

  /////////////////////////////////////
  // Extension Data API

  public ExtensionData getExtensionData(ViewExtension extension) {
    return viewInfo.getExtensionData(extension);
  }

  public ExtensionData getExtensionData(
      ViewExtension extension, Object instance) {
    return viewInfo.getExtensionData(extension, instance);
  }

  public void setExtensionData(
      ViewExtension extension, Object instance, ExtensionData data) {
    viewInfo.setExtensionData(extension, instance, data);
  }

  public void addExtensionDataListener(ExtensionDataListener listener) {
    viewInfo.addExtensionDataListener(listener);
  }

  public void removeExtensionDataListener(ExtensionDataListener listener) {
    viewInfo.removeExtensionDataListener(listener);
  }

  /////////////////////////////////////
  // Manage dirty state for editor

  private void setDirtyState(boolean dirty) {
    this.isDirty = dirty;
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  @Override
  public boolean isDirty() {
    return isDirty;
  }

  private void markDirty() {
    setDirtyState(true);
  }

  /////////////////////////////////////
  // Eclipse rendering integration

  @Override
  public void setInitializationData(
      IConfigurationElement cfig,
      String propertyName, Object data) {
    super.setInitializationData(cfig, propertyName, data);
  }

  @Override
  protected void createPages() {
    try {
      createDiagramPage();
      createDetailsPage();
    } catch (Exception err) {
      ViewDocLogger.LOG.error("Unable to create View pages", err);
    }
  }

  private void createDiagramPage() {
    Composite parent = new Composite(getContainer(), SWT.H_SCROLL | SWT.V_SCROLL);
    parent.setLayout(Widgets.buildContainerLayout(1));

    // bottom composite containing main diagram
    renderer = createView(parent);
    renderer.setLayoutData(Widgets.buildGrabFillData());

    // Configure the rendering pipe before listening for changes.
    prepareView();
    renderer.start();

    int index = addPage(parent);
    setPageText(index, "Graph View");
  }

  /**
   * Ensure that any failures in View creation, and the underlying JOGL
   * rendering system are visible to users.
   * 
   * @param parent
   * @return 
   */
  private View createView(Composite parent) {
    // bottom composite containing main diagram
    try {
      return new View(parent, getPartName(), new RendererChangeReceiver(this));
    } catch (Exception err) {
      ViewDocLogger.LOG.error("Unable to create View pages", err);
      throw err;
    }
  }

  protected String edgeToolTip(GraphEdge edge) {
    final String str = edge.toString();
    updateStatusLine(str);
    return str;
  }

  protected String vertexToolTip(GraphNode node) {
    final String str = node.toString();
    updateStatusLine(str);
    return str;
  }

  @SuppressWarnings("unused")
  private void createDetailsPage() {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.marginTop = 9;
    parent.setLayout(layout);

    Label nameLabel = Widgets.buildCompactLabel(parent, "Description");
    final Text name = Widgets.buildGridBoxedText(parent);

    String descr = Strings.nullToEmpty(viewInfo.getDescription());
    name.setText(descr);

    name.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        if (viewInfo != null) {
          String newDescription = name.getText();
          viewInfo.setDescription(newDescription);
          markDirty();
        }
      }
    });
    int index = addPage(parent);
    setPageText(index, "Properties");
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException  {
    super.init(site, input);

    initFromInput(input);

    // Synthesize derived graph perspectives
    deriveDetails();

    // Listen to changes in the underlying ViewModel
    viewPrefsListener = new Listener();
    addViewPrefsListener(viewPrefsListener);
    extDataListener = new PartExtDataListener();
    addExtensionDataListener(extDataListener);

    if (null != initialLayout) {
      addDrawingListener(new DrawingListener() {

        @Override
        public void updateDrawingBounds(
            Rectangle2D drawing, Rectangle2D viewport) {

          // TODO: Use edge matcher from ViewEditorInput, too.
          GraphEdgeMatcherDescriptor matcher =
              getLayoutEdgeMatcherRef().getDocument();
          applyLayout(initialLayout, matcher, getExposedNodes());

          // Only need to do this once on startup
          removeDrawingListener(this);
        }});
    }
  }

  private void initFromInput(IEditorInput input) throws PartInitException {
    if (input instanceof ViewEditorInput) {
      ViewEditorInput editorInput = (ViewEditorInput) input;
      baseName = editorInput.getBaseName();
      initialLayout = editorInput.getInitialLayout();
      setPartName(input.getName());

      viewInfo = editorInput.getViewDocument();
      markDirty();
      return;
    }
    if (input instanceof IFileEditorInput) {
      IFile viewFile = ((IFileEditorInput) input).getFile();
      baseName = buildFileInputBaseName(viewFile);
      initialLayout = null;
      setPartName(input.getName());

      try {
        ViewDocXmlPersist loader =
            ViewDocXmlPersist.buildForLoad(viewFile, "load");
        viewInfo = loader.load(viewFile.getLocationURI());
        setDirtyState(false);
      } catch (RuntimeException err) {
        viewInfo = null;
        String msg = MessageFormat.format(
            "Unable to load view from {0}",
            viewFile.getFullPath().toString());
        ViewDocLogger.LOG.error(msg, err);
        throw new PartInitException(msg);
      }
      return;
    }
    throw new PartInitException(
        "Input for editor is not suitable for the ViewEditor");
  }

  private String buildFileInputBaseName(IFile file) {
    IPath result = Path.fromOSString(file.getName());
    return result.removeFileExtension().toOSString();
  }

  /**
   * Derive a number of alternative presentations and details from the
   * newly open graph view.
   */
  private void deriveDetails() {
    // Synthesize derived graph perspectives
    viewGraph = viewInfo.buildGraphView();

    hierarchies = new HierarchyCache<NodeDisplayProperty>(
        getNodeDisplayPropertyProvider(),
        getViewGraph());

    viewResources = viewInfo.buildGraphResources();

    updateExposedGraph();
    ViewExtensionRegistry.deriveRegistryDetails(this);
  }

  /**
   * Release the resource held by the ViewEditor:
   * - ViewModelListener
   * - Underlying view.
   */
  @Override
  public void dispose() {
    if (null != viewPrefsListener) {
      removeViewPrefsListener(viewPrefsListener);
      viewPrefsListener = null;
    }

    if (null != extDataListener) {
      removeExtensionDataListener(extDataListener);
      extDataListener = null;
    }

    if (null != hierarchies) {
      hierarchies = null;
    }

    if (null != renderer) {
      renderer.dispose();
      renderer = null;
    }

    super.dispose();
  }

  @Override
  public void setFocus() {
  }

  /////////////////////////////////////
  // Actions on the rendering system

  /**
   * Take a screenshot of the given view. Ask the user a filename, and use
   * this filename to determine which type of file format has to be used.
   * PNG format is used as default.
   *
   * @param view the view to capture.
   */
  public void takeScreenshot() {
    // make the screenshot first, so that the overlapping file selection window
    // does not Interfere with the process of taking the screenshot
    // (apparently, otherwise, it does)
    BufferedImage screenshot = renderer.takeScreenshot();

    // ask the user a filename where to save the screenshot
    FileDialog fd = new FileDialog( getSite().getShell(), SWT.SAVE);
    fd.setText("Save Screenshot:");
    String[] filterExt = { "*.png", "*.jpg", "*.gif", "*.bmp", "*.*" };
    fd.setFilterExtensions(filterExt);
    String selected = fd.open();

    IActionBars bars = getEditorSite().getActionBars();

    // user canceled operation. Print a message in the status line, and return.
    if (null == selected) {
      bars.getStatusLineManager().setErrorMessage(
          "To take a screenshot, you must specify a filename.");
      return;
    }

    // check if the file has an extension. otherwise, use .png as default
    // extension.
    if (selected.lastIndexOf('.') == -1) {
      selected = selected+".png";
    }

    try {
      // finally, write the image on a file.
      ImageIO.write(screenshot, selected.substring(
          selected.lastIndexOf('.')+1), new File(selected));
      bars.getStatusLineManager().setMessage("Image saved to "+selected);
    } catch (IOException e) {
      e.printStackTrace();
      bars.getStatusLineManager().setErrorMessage(
          "Error while saving screenshot");
    }
  }

  /////////////////////////////////////
  // Persistence Support

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    // If there are any file problems, do this as a Save As ..
    IFile infoFile = getInputFile();
    if (null == infoFile) {
      doSaveAs();
      return;
    }

    saveFile(infoFile, monitor, "save");
    if (null != monitor) {
      monitor.done();
    }
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    IFile saveAs = getSaveAsFile();
    saveas.setOriginalFile(saveAs);
    saveas.setOriginalName(saveAs.getName());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }

    // get the file relatively to the workspace.
    IFile saveFile = calcViewFile(saveas.getResult());
    // TODO: set up a progress monitor
    saveFile(saveFile, null, "saveAs");

    baseName = buildFileInputBaseName(saveFile);
    setPartName(saveFile.getName());

    FileEditorInput effInput = new FileEditorInput(saveFile);
    setInputWithNotify(effInput);
  }

  public IFile getSaveAsFile() {
    IFile infoFile = getInputFile();
    if (null != infoFile) {
      return infoFile;
    }

    IContainer parent = viewInfo.getGraphModelLocation().getParent();
    String filebase = baseName + '.' + ViewDocument.EXTENSION;
    String filename = PlatformTools.guessNewFilename(
        parent, filebase, 1, 10);

    IPath filePath = Path.fromOSString(filename);
    return parent.getFile(filePath);
  }

  /**
   * For unsaved ViewDocuments, the suggested creation name.
   * 
   * For saved ViewDocuments, the last segment of the file name
   * without the extension.
   */
  public String getBaseName() {
    return baseName;
  }

  /**
   * Provide the file association with the editor input, if any.
   * @return 
   */
  private IFile getInputFile() {
    // If there are any file problems, do this as a Save As ..
    IEditorInput input = getEditorInput();
    if (input instanceof IFileEditorInput) {
      return ((IFileEditorInput) input).getFile();
    }

    return null;
  }

  /**
   * Save the view document, managing the progress monitor too.
   * Trigger a resource refresh for the supplied file if the save is successful.
   * 
   * @param file where to save the view document
   * @param monitor progress indicator to update
   * @param opLabel save operation being performed
   * @throws IOException if the save is unsuccessful
   */
  private void saveFile(IFile file, IProgressMonitor monitor, String opLabel) {
    ViewDocXmlPersist persist = ViewDocXmlPersist.buildForSave(opLabel);
    persist.saveDocument(file, viewInfo, monitor);

    setDirtyState(false);
  }

  /**
   * Ensure that we have a file extension on the file name.
   * 
   * @param savePath Initial save path from user
   * @return valid IFile with an extension.
   */
  private IFile calcViewFile(IPath savePath) {
    return WorkspaceTools.calcViewFile(savePath, ViewDocument.EXTENSION);
  }

  /////////////////////////////////////
  // Graph elements

  public NodeTreeProvider<NodeDisplayProperty> getNodeDisplayPropertyProvider() {
    return viewInfo.getNodeDisplayPropertyProvider();
  }

  /**
   * Provide a {@code NodeDisplayProperty} for any {@code GraphNode}.
   * If there is no persistent value, synthesize one, but it won't be saved
   * until some process (e.g. NodeEditor) modifies it's properties.
   */
  public NodeDisplayProperty getNodeProperty(GraphNode node) {
    NodeDisplayProperty result = viewInfo.getNodeProperty(node);
    if (null != result) {
      return result;
    }
    return new NodeDisplayProperty();
  }

  public void setNodeProperty(
      GraphNode node, NodeDisplayProperty newProperty) {
    viewInfo.setNodeProperty(node, newProperty);
  }

  /////////////////////////////////////
  // Provide standardized access to this view's
  // relation visibility

  public PropertyDocumentReference<RelationSetDescriptor> getVisibleRelationSet() {
    return viewInfo.getVisibleRelationSet();
  }

  public void setVisibleRelationSet(
      PropertyDocumentReference<RelationSetDescriptor> visRelSet) {
    viewInfo.setVisibleRelationSet(visRelSet);
  }

  public boolean isVisibleRelation(Relation relation) {
    return viewInfo.isVisibleRelation(relation);
  }

  public void setVisibleRelation(Relation relation, boolean isVisible) {
    boolean nowVisible = isVisibleRelation(relation);
    if (nowVisible == isVisible) {
      return;
    }

    Collection<Relation> visibleRelations = getVisibleRelations();
    if (isVisible) {
      visibleRelations.add(relation);
    } else {
      visibleRelations.remove(relation);
    }
    PropertyDocumentReference<RelationSetDescriptor> visibleRelationSet =
        buildAdHocRelationSet(visibleRelations);
    viewInfo.setVisibleRelationSet(visibleRelationSet);
  }

  private Collection<Relation> getVisibleRelations() {
    RelationSet visibleRelationSet =
        getVisibleRelationSet().getDocument().getInfo();
    return RelationSets.filterRelations(
        visibleRelationSet, getDisplayRelations());
  }

  private PropertyDocumentReference<RelationSetDescriptor>
      buildAdHocRelationSet(Collection<Relation> visibleRelations) {
    Builder builder = RelationSetDescriptor.createBuilder("custom", null);
    for(Relation relation : visibleRelations) {
      builder.addRelation(relation);
    }
    RelationSetDescriptor relSetDesc = builder.build();
    PropertyDocumentReference<RelationSetDescriptor> result =
        DirectDocumentReference.buildDirectReference(relSetDesc);
    return result;
 }

  /////////////////////////////////////
  // Provide standardized access to this view's
  // edge property repository

  /**
   * Provide an {@code EdgeDisplayProperty} for any {@code GraphEdge}.
   * If there is no persistent value, synthesize one, but it won't be save
   * until some process (e.g. EdgeEditor) modifies it's properties.
   */
  public EdgeDisplayProperty getEdgeProperty(GraphEdge edge) {
    EdgeDisplayProperty result = viewInfo.getEdgeProperty(edge);
    if (null != result) {
      return result;
    }
    return new EdgeDisplayProperty();
  }

  public void setEdgeProperty(
      GraphEdge edge, EdgeDisplayProperty newProperty) {
    viewInfo.setEdgeProperty(edge, newProperty);
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return viewInfo.getRelationProperty(relation);
  }

  public void setRelationProperty(
      Relation relation, EdgeDisplayProperty relProp) {
    viewInfo.setRelationProperty(relation, relProp);
  }

  /////////////////////////////////////
  // Rendering details and render access

  /////////////////////////////////////
  // Update Graph Layouts

  public LayoutPlan getSelectedLayout() {
    PropertyDocumentReference<LayoutPlanDocument<? extends LayoutPlan>> planRef =
        viewInfo.getSelectedLayout();
    if (null != planRef) {
      return planRef.getDocument().getInfo();
    }
    return null;
  }

  public Map<GraphNode, Point2D> getNodeLocations() {
    return viewInfo.getNodeLocations();
  }

  /////////////////////////////////////
  // Update node positions in the View Document

  /**
   * Move the camera to the supplied x and y coordinates.
   */
  public void moveToCamera(float camX, float camY) {
    renderer.moveToPosition(camX, camY);
  }

  /**
   * Zoom by moving camera to supplied z coordinate.
   */
  public void zoomToCamera(float camZ) {
    renderer.zoomToCamera(camZ);
  }

  /**
   * Zoom by moving camera to supplied z coordinate.
   */
  public void rotateToDirection(float xRot, float yRot, float zRot) {
    renderer.rotateToDirection(xRot, yRot, zRot);
  }

  /**
   * Zoom to supplied scale.
   * A value of 1.0 places the camera at the default location.
   */
  public void setZoom(float scale) {
    renderer.setZoom(scale);
  }

  public void editNodeLocations(
      Map<GraphNode, Point2D> nodeLocations, Object author) {
    viewInfo.editNodeLocations(nodeLocations, author);
  }

  /**
   * For internal use to avoid the null author.
   */
  private void editNodeLocations(Map<GraphNode, Point2D> nodeLocations) {
    editNodeLocations(nodeLocations, null);
  }

  /**
   * Scale the exposed nodes so they would fit in the viewport, if the viewport
   * was centered over the nodes.  This has been the historical behavior of the
   * {@code FactorPlugin}.
   */
  public void scaleToViewport() {
    scaleToViewport(getExposedNodes(), getNodeLocations());
  }

  /**
   * Scale the coordinates for all exposed nodes as indicated by the parameters.
   * 
   * @param scaleX scale factor for X coordinates
   * @param scaleY scale factor for Y coordinates
   */
  public void scaleLayout(double scaleX, double scaleY) {
    Point2dUtils.Translater translater =
        Point2dUtils.newScaleTranslater(scaleX, scaleY);
    Map<GraphNode, Point2D> changes = Point2dUtils.translateNodes(
        getExposedNodes(), getNodeLocations(),
        translater);

    editNodeLocations(changes);
  }

  private void scaleToViewport(
      Collection<GraphNode> layoutNodes, Map<GraphNode, Point2D> locations) {
    Rectangle2D viewport = renderer.getOGLViewport();
    Map<GraphNode, Point2D> changes = 
        LayoutUtil.computeFullViewScale(layoutNodes, locations, viewport);
    editNodeLocations(changes);
  }

  /////////////////////////////////////
  // Compute new positions based on a LayoutGenerator

  public Point2D getPosition(GraphNode node) {
    return viewInfo.getNodeLocations().get(node);
  }

  public double getXPos(GraphNode node) {
    Point2D position = getPosition(node);
    return position.getX();
  }

  public double getYPos(GraphNode node) {
    Point2D position = getPosition(node);
    return position.getY();
  }

  public void applyLayout(LayoutPlan layoutPlan) {
    applyLayout(layoutPlan, getLayoutEdgeMatcherRef().getDocument());
  }

  public void applyLayout(
      LayoutPlan layoutPlan, GraphEdgeMatcherDescriptor edgeMatcher) {

    Collection<GraphNode> layoutNodes = getLayoutNodes();
    if (layoutNodes.size() < 2) {
      // TODO: Notify user that a single node cannot be positioned.
      return;
    }
    applyLayout(layoutPlan, edgeMatcher, layoutNodes);
  }

  private Collection<GraphNode> getLayoutNodes() {
    Collection<GraphNode> picked = getSelectedNodes();
    if (picked.isEmpty()) {
      return getExposedNodes();
    }

    return picked;
  }

  /**
   * Apply the given layout with the given {@link DirectedRelationFinder} to
   * build a tree if the layout need one, to the graph.
   *
   * @param layout the new Layout to apply
   * @param edgeMatcher {@link GraphEdgeMatcherDescriptor} to defined edges
   *        considered for layout.
   * @param layoutNodes nodes that participate in the layout
   */
  private void applyLayout(
      LayoutPlan layoutPlan, GraphEdgeMatcherDescriptor edgeMatcher,
      Collection<GraphNode> layoutNodes) {

    LayoutContext context = new LayoutContext();
    context.setGraphModel(getExposedGraph());
    context.setMovableNodes(layoutNodes);
    context.setEdgeMatcher(edgeMatcher);
    context.setNodeLocations(getNodeLocations());

    Rectangle2D viewport = renderer.getOGLViewport();
    Rectangle2D layoutViewport = Point2dUtils.scaleRectangle(viewport, 0.7);
    context.setViewport(layoutViewport);

    Map<GraphNode, Point2D> changes =
        LayoutUtil.calcPositions(layoutPlan, context, layoutNodes);

    // Change the node locations.
    editNodeLocations(changes);
  }

  /////////////////////////////////////
  // Node selection and event handling
  // TODO(leeca): Consolidate all Selection change notifications into a
  // uniform data structure (e.g. always Collection<GraphNode> or GraphNode[]).

  public Collection<GraphNode> getSelectedNodes() {
    return viewInfo.getSelectedNodes();
  }

  public boolean isSelected(GraphNode test) {
    return getSelectedNodes().contains(test);
  }

  public void selectAllNodes() {
    selectNodes(viewInfo.getViewNodes(), this);
  }

  public void selectNodes(Collection<GraphNode> nodes) {
    viewInfo.setSelectedNodes(nodes, null);
  }

  public void selectNodes(Collection<GraphNode> nodes, Object author) {
    viewInfo.setSelectedNodes(nodes, author);
  }

  public void extendSelection(
      Collection<GraphNode> extendNodes, Object author) {
    viewInfo.editSelectedNodes(GraphNode.EMPTY_NODE_LIST, extendNodes, author);
  }

  public void reduceSelection(
      Collection<GraphNode> reduceNodes, Object author) {
    viewInfo.editSelectedNodes(reduceNodes, GraphNode.EMPTY_NODE_LIST, author);
  }

  public void moveSelectionDelta(
      double deltaX, double deltaY, Object author) {
    Point2dUtils.Translater translater =
        Point2dUtils.newDeltaTranslater(deltaX, deltaY);

    Map<GraphNode, Point2D> changes = Point2dUtils.translateNodes(
        getSelectedNodes(), getNodeLocations(), translater);
    editNodeLocations(changes, author);
  }

  /////////////////////////////////////
  // Listeners for camera preferences

  public void addSceneListener(ScenePreferences.Listener listener) {
    sceneListeners.addListener(listener);
  }

  public void removeSceneListener(ScenePreferences.Listener listener) {
    sceneListeners.removeListener(listener);
  }

  private void firePositionChanged(final ScenePreferences camera) {
    sceneListeners.fireEvent(new SimpleDispatcher<ScenePreferences.Listener>() {

      @Override
      public void dispatch(ScenePreferences.Listener listener) {
        listener.positionChanged(camera);
      }
    });
    
  }

  private void fireDirectionChanged(final ScenePreferences camera) {
    sceneListeners.fireEvent(new SimpleDispatcher<ScenePreferences.Listener>() {

      @Override
      public void dispatch(ScenePreferences.Listener listener) {
        listener.directionChanged(camera);
      }
    });
  }

  /////////////////////////////////////
  // Listeners for drawing metrics

  public void addDrawingListener(DrawingListener listener) {
    drawingListeners.addListener(listener);
  }

  public void removeDrawingListener(DrawingListener listener) {
    drawingListeners.removeListener(listener);
  }

  public void updateDrawingBounds(
      final Rectangle2D drawing, final Rectangle2D viewport) {

    // Tell the scroll bar directly
    renderer.updateDrawingBounds(drawing, viewport);

    drawingListeners.fireEvent(new ListenerManager.Dispatcher<DrawingListener>() {
      @Override
      public void dispatch(DrawingListener listener) {
        listener.updateDrawingBounds(drawing, viewport);
      }

      @Override
      public void captureException(RuntimeException errAny) {
        ViewDocLogger.LOG.error(
            "Drawing bounds update bounds", errAny);
      }
    });
  }

  /**
   * Capture stable points in the diagram rendering.
   */
  public void sceneChanged() {
    markDirty();
    ScenePreferences prefs = getScenePrefs();
    if (null == prefs) {
      prefs = ScenePreferences.getDefaultScenePrefs();
      viewInfo.setScenePrefs(prefs);
    }

    // Capture the newly stable current camera position in the prefs object.
    renderer.saveCameraPosition(prefs);
    renderer.saveCameraDirection(prefs);
    firePositionChanged(prefs);
    fireDirectionChanged(prefs);
  }

  /////////////////////////////////////
  // Notifications from ViewEditor toward Tools and other ViewEditor listeners

  public void addSelectionChangeListener(SelectionChangeListener listener) {
    selectionListeners.addListener(listener);
  }

  public void removeSelectionChangeListener(SelectionChangeListener listener) {
    selectionListeners.removeListener(listener);
  }

  private void fireExtendSelection(
      final Collection<GraphNode> extension) {
    if (extension.isEmpty()) {
      return;
    }

    selectionListeners.fireEvent(new SimpleDispatcher<SelectionChangeListener>() {
      @Override
      public void dispatch(SelectionChangeListener listener) {
        listener.extendSelection(extension);
      }
    });
  }

  private void fireReduceSelection(
      final Collection<GraphNode> reduction) {
    if (reduction.isEmpty()) {
      return;
    }

    selectionListeners.fireEvent(new SimpleDispatcher<SelectionChangeListener>() {
      @Override
      public void dispatch(SelectionChangeListener listener) {
        listener.reduceSelection(reduction);
      }
    });
  }

  private Collection<GraphNode> subtractNodes(
      Collection<GraphNode> from, Collection<GraphNode> minus) {
    if (minus.isEmpty()) {
      return from;
    }
    List<GraphNode> result = Lists.newArrayList(from);
    result.removeAll(minus);
    return result;
  }

  private void updateSelectedNodes(
      Collection<GraphNode> previous,
      Collection<GraphNode> current, Object author) {

    Collection<GraphNode> removeNodes = subtractNodes(previous, current);
    Collection<GraphNode> extendNodes = subtractNodes(current, previous);

    if (isOptionChecked(OptionPreferences.ONLY_SELECTED_NODE_EDGES_ID)) {
      setVisibleSelectedNodeEdges();
    }
    if (author != renderer) {
      renderer.updateSelectedNodes(removeNodes, extendNodes);
    }
    fireReduceSelection(removeNodes);
    fireExtendSelection(extendNodes);
  }

  private void initSelectedNodes(Collection<GraphNode> selection) {
    updateSelectedNodes(GraphNode.EMPTY_NODE_LIST, selection, null);
  }

  /////////////////////////////////////
  // Specialized features

  public HierarchyCache<NodeDisplayProperty> getHierarchies() {
    return hierarchies;
  }

  public GraphData<NodeDisplayProperty> getHierarchy(
      GraphEdgeMatcherDescriptor edgeMatcher) {
    return hierarchies.getHierarchy(edgeMatcher.getInfo());
  }

  /**
   * Post an update on the status line.
   * @param statusText text to display on status line
   */
  private void updateStatusLine(final String statusText) {
    WorkspaceTools.asyncExec(new Runnable() {

      @Override
      public void run() {
        getEditorSite().getActionBars().getStatusLineManager()
            .setMessage(statusText);
      }
    });
  }

  public ViewDocument buildNewViewDocument(Collection<GraphNode> nodes) {
    return viewInfo.newViewDocument(nodes);
  }

  /////////////////////////////////////
  // Handle configuration and changes from options

  /**
   * Everything that should happen before the dry-run and the start.
   * Since {@link #setGraphModel} is invoked early, all display property
   * entities should be defined.
   */
  private void prepareView() {
    // Prepare renderer with full set of nodes and edges.
    renderer.initializeScenePrefs(getScenePrefs());
    initGraphRenderer();
    initEdgeRendering();

    initNodeRendering();
    initSelectedNodes(getSelectedNodes());
    renderer.initializeNodeLocations(viewInfo.getNodeLocations());
    handleCollapseRendering(
        getCollapseTreeModel().computeDepthFirst(), CollapseData.EMPTY_LIST);

    prepareRenderOptions();
    ViewExtensionRegistry.prepareRegistryView(this);

    // Force any animation to completion.
    renderer.finishSteps();
  }

  private void initGraphRenderer() {
    GraphModel view = getViewGraph();
    Map<GraphNode, ? extends SuccessorEdges> edgeMap =
        Trees.computeSuccessorHierarchy(
            view, GraphEdgeMatcherDescriptors.FORWARD.getInfo());

    renderer.setGraphModel(view, edgeMap);
  }

  /**
   * Initialize rendering properties for those nodes in the view graph that
   * have explicit property settings.
   */
  private void initNodeRendering() {
    for (GraphNode node : viewGraph.getNodes()) {
      NodeDisplayProperty prop = viewInfo.getNodeProperty(node);
      if (null != prop) {
        renderer.updateNodeProperty(node, prop);
      }
    }
  }

  private void initEdgeRendering() {
    for (GraphEdge edge : viewGraph.getEdges()) {

      boolean isVisible = isVisibleRelation(edge.getRelation());
      renderer.setEdgeVisible(edge, isVisible);

      // If the edge has explicit display properties, use those.
      EdgeDisplayProperty edgeProp = viewInfo.getEdgeProperty(edge);
      if (null != edgeProp) {
        renderer.updateEdgeProperty(edge, edgeProp);
        continue;
      }

      EdgeDisplayProperty relationProp =
          getRelationProperty(edge.getRelation());
      if (null != relationProp) {
        renderer.updateEdgeProperty(edge, relationProp);
      }
    }
  }

  private void updateEdgesToVisible(RelationSet relationSet) {
    for (GraphEdge edge : viewGraph.getEdges()) {
      if (relationSet.contains(edge.getRelation())) {
        // Set edge visibility
        boolean isVisible = isVisibleRelation(edge.getRelation());
        renderer.setEdgeVisible(edge, isVisible);
      }
    }
  }

  /**
   * Edges that lack an explicit {@link EdgeDisplayProperty} are
   * updated to render with the latest {@link EdgeDisplayProperty}
   * associated with the edge's {@link Relation}.
   * 
   * This does not directly affect edge visibility, which is
   * handled separately. (However, poor rendering choices
   * may lead to invisibly rendered lines.)
   */
  private void updateEdgesToRelationProperties() {
    for (GraphEdge edge : viewGraph.getEdges()) {

      // If the edge has explicit display properties, leave those.
      EdgeDisplayProperty edgeProp = viewInfo.getEdgeProperty(edge);
      if (null != edgeProp) {
        continue;
      }

      EdgeDisplayProperty relationProp =
          getRelationProperty(edge.getRelation());
      if (null != relationProp) {
        renderer.updateEdgeProperty(edge, relationProp);
        continue;
      }

      // Nothing to do if no properties have changed.
    }
  }

  /////////////////////////////////////
  // Rendering options from user preferences

  public void setNodeColorByMode(
      GraphNode node, NodeColorMode mode, NodeColorSupplier supplier) {
    renderer.setNodeColorByMode(node, mode, supplier);
  }

  public void setNodeRatioByMode(
      GraphNode node, NodeRatioMode mode, NodeRatioSupplier supplier) {
    renderer.setNodeRatioByMode(node, mode, supplier);
  }

  public void setNodeShapeByMode(
      GraphNode node, NodeShapeMode mode, NodeShapeSupplier supplier) {
    renderer.setNodeShapeByMode(node, mode, supplier);
  }

  public void setNodeSizeByMode(
      GraphNode node, NodeSizeMode mode, NodeSizeSupplier supplier) {
    renderer.setNodeSizeByMode(node, mode, supplier);
  }

  private void prepareRenderOptions() {
    updateNodeStrokeHighlight(
        isOptionChecked(OptionPreferences.STROKEHIGHLIGHT_ID));

    updateRootColorMode(
        viewInfo.getOption(OptionPreferences.ROOTHIGHLIGHT_ID));

    updateNodeColorMode(
        viewInfo.getOption(OptionPreferences.COLOR_MODE_ID));
    updateNodeShapeMode(
        viewInfo.getOption(OptionPreferences.SHAPE_ID));
    updateNodeSizeMode(
        viewInfo.getOption(OptionPreferences.SIZE_ID));
    updateNodeRatioMode(
        viewInfo.getOption(OptionPreferences.STRETCHRATIO_ID));
    ViewExtensionRegistry.prepareRegistryView(this);

    updateOnlySelectedNodeEdges(
        isOptionChecked(OptionPreferences.ONLY_SELECTED_NODE_EDGES_ID));
 }

  /**
   * @param optionId
   * @param value
   */
  private void handleOptionChange(String optionId, String value) {
    markDirty();
    if (OptionPreferences.STROKEHIGHLIGHT_ID.equals(optionId)) {
      updateNodeStrokeHighlight(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.OPTION_DESCRIPTION.equals(optionId)) {
      updateOptionDescription(value);
      return;
    }
    if (OptionPreferences.ROOTHIGHLIGHT_ID.equals(optionId)) {
      updateRootColorMode(value);
      return;
    }
    if (OptionPreferences.COLOR_MODE_ID.equals(optionId)) {
      updateNodeColorMode(value);
    }
    if (OptionPreferences.SHAPE_ID.equals(optionId)) {
      updateNodeShapeMode(value);
      return;
    }
    if (OptionPreferences.SIZE_ID.equals(optionId)) {
      updateNodeSizeMode(value);
      return;
    }
    if (OptionPreferences.STRETCHRATIO_ID.equals(optionId)) {
      updateNodeRatioMode(value);
      return;
    }
    if (OptionPreferences.ONLY_SELECTED_NODE_EDGES_ID.equals(optionId)) {
      updateOnlySelectedNodeEdges(Boolean.parseBoolean(value));
      return;
    }
    ViewDocLogger.LOG.info(
        "Unrecognized option {} cannot be set to value {}", optionId, value);
  }

  private void updateNodeStrokeHighlight(boolean enable) {
    renderer.activateNodeStroke(enable);
  }

  private void updateNodeColorMode(String value) {
    NodeColorMode mode =
        ViewExtensionRegistry.getRegistryGetNodeColorMode(value);
    renderer.setNodeColorMode(mode);
  }

  private void updateRootColorMode(String value) {
    NodeColorMode mode =
        ViewExtensionRegistry.getRegistryGetNodeColorMode(value);
    renderer.setRootColorMode(mode);
  }

  private void updateNodeShapeMode(String value) {
    NodeShapeMode mode =
        ViewExtensionRegistry.getRegistryGetNodeShapeMode(value);
    renderer.setNodeShapeMode(mode);
  }

  private void updateNodeSizeMode(String value) {
    NodeSizeMode mode =
        ViewExtensionRegistry.getRegistryGetNodeSizeMode(value);
    renderer.setNodeSizeMode(mode);
  }

  private void updateNodeRatioMode(String value) {
    NodeRatioMode mode =
        ViewExtensionRegistry.getRegistryGetNodeRatioMode(value);
    renderer.setNodeRatioMode(mode);
  }

  private void updateOnlySelectedNodeEdges(boolean onlySelected) {
    if (onlySelected) {
      setVisibleSelectedNodeEdges();
      return;
    }

    setVisibleAllNodeEdges();
  }

  private void setVisibleSelectedNodeEdges() {
    Set<GraphNode> nodes =  new HashSet<>(viewInfo.getSelectedNodes());

    for (GraphEdge edge : viewGraph.getEdges()) {
      if (!nodes.contains(edge.getHead())) {
        renderer.setEdgeVisible(edge, false);
        continue;
      }
      if (nodes.contains(edge.getTail())) {
        renderer.setEdgeVisible(edge, false);
        continue;
      }
      renderer.setEdgeVisible(edge, true);
    }
  }

  private void setVisibleAllNodeEdges() {
    for (GraphEdge edge : viewGraph.getEdges()) {
      renderer.setEdgeVisible(edge, true);
    }
  }

  private void updateOptionDescription(String value) {
    // No need to update name member.
    // That might cause an update loop.
    // Just the markDirty() from the main handler method.
  }

  /////////////////////////////////////
  // Receiver for notifications from the ViewDocument
  // Most events are mapped to standard re-dispatch methods.

  public void dumpRenderingProperties() {
    System.out.println("\nNode Rendering properties:");
    for (NodeRenderingProperty node : renderer.getNodeProperties()) {
      System.out.println(node.toString());
    }
    System.out.println("\nEdge Rendering properties:");
    for (EdgeRenderingProperty edge : renderer.getEdgeProperties()) {
      System.out.println(edge.toString());
    }
  }

  /**
   * Handle notifications from ViewDocument (mostly UserPreferences) that
   * some user-controlled feature of the view has changed.
   */
  private class Listener implements ViewPrefsListener {
    @Override
    public void relationSetVisibleChanged(
        PropertyDocumentReference<RelationSetDescriptor> visibleSet) {
      if (null == renderer) {
        return;
      }
      // The visible relations in view preferences has already
      // changed to the supplied visibleSet.
      updateEdgesToVisible(RelationSets.ALL);
      markDirty();
    }

    @Override
    public void edgePropertyChanged(
        GraphEdge edge, EdgeDisplayProperty newProperty) {
      if (null == renderer) {
        return;
      }
      renderer.updateEdgeProperty(edge, newProperty);
      markDirty();
    }

    @Override
    public void relationPropertyChanged(
        Relation relation,  EdgeDisplayProperty newProperty) {
      updateEdgesToRelationProperties();
      markDirty();
    }

    @Override
    public void nodePropertyChanged(
        GraphNode node, NodeDisplayProperty newProperty) {
      if (null == renderer) {
        return;
      }
      renderer.updateNodeProperty(node, newProperty);
      markDirty();
    }

    @Override
    public void nodeLocationsSet(Map<GraphNode, Point2D> newLocations) {
      renderer.editNodeLocations(newLocations);
      markDirty();
    }

    @Override
    public void nodeLocationsChanged(
        Map<GraphNode, Point2D> newLocations, Object author) {
      // Skip animation if the renderer itself made the move
      if (author == renderer) {
        renderer.updateNodeLocations(newLocations);
      }
      else {
        renderer.editNodeLocations(newLocations);
      }
      markDirty();
    }

    @Override
    public void selectionChanged(Collection<GraphNode> previous,
        Collection<GraphNode> current, Object author) {
      updateSelectedNodes(previous, current, author);
      markDirty();
    }

    @Override
    public void optionChanged(String optionId, String value) {
      handleOptionChange(optionId, value);
    }

    @Override
    public void collapseChanged(Collection<CollapseData> created,
        Collection<CollapseData> removed, Object author) {
      updateExposedGraph();
      handleCollapseRendering(created, removed);
      markDirty();
    }

    @Override
    public void nodeTreeChanged() {
      // Open views should register their own listener.
      markDirty();
    }
  }

  /////////////////////////////////////
  // Notice changes to extension data

  private class PartExtDataListener implements ExtensionDataListener {

    @Override
    public void extensionDataChanged(
        ViewExtension ext, Object instance,
        Object propertyId, Object updates) {
      markDirty();
    }
  }

  /////////////////////////////////////
  // Handle feedback from OGL renderer

  /**
   * Convert Renderer events into editor actions.
   */
  public void handleRendererEvent(RendererEvent event) {
    if (event instanceof RendererEvents.LayoutEvents) {
      int index = ((RendererEvents.LayoutEvents) event).ordinal();
      if (index < OGL_LAYOUTS.length) {
        applyLayout(OGL_LAYOUTS[index]);
      }
      return;
    }

    if (RendererEvents.ScaleEvents.ZOOM_IN.equals(event)) {
      scaleLayout(ZOOM_IN_FACTOR, ZOOM_IN_FACTOR);
      return;
    }
    if (RendererEvents.ScaleEvents.ZOOM_OUT.equals(event)) {
      scaleLayout(ZOOM_OUT_FACTOR, ZOOM_OUT_FACTOR);
      return;
    }
    if (RendererEvents.ScaleEvents.SCALE_TO_VIEWPORT.equals(event)) {
      scaleToViewport();
      return;
    }
  }

  private static class RendererChangeReceiver
      implements RendererChangeListener {

    private final ViewEditor editor;

    public RendererChangeReceiver(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public void locationsChanged(Map<GraphNode, Point2D> changes) {
      editor.editNodeLocations(changes, this);
    }

    @Override
    public void selectionMoved(double x, double y) {
      editor.moveSelectionDelta(x, y, this);
    }

    @Override
    public void selectionChanged(Collection<GraphNode> pickedNodes) {
      editor.selectNodes(pickedNodes);
    }

    @Override
    public void selectionExtended(Collection<GraphNode> extendNodes) {
      editor.extendSelection(extendNodes, null);
    }

    @Override
    public void selectionReduced(Collection<GraphNode> reduceNodes) {
      editor.reduceSelection(reduceNodes, null);
    }

    @Override
    public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport) {
      editor.updateDrawingBounds(drawing, viewport);
    }

    @Override
    public void sceneChanged() {
      editor.sceneChanged();
    }

    @Override
    public void handleEvent(RendererEvent event) {
      editor.handleRendererEvent(event);
    }
  }

  /////////////////////////////////////
  // Run the new ViewEditor

  /**
   * Activate a new ViewEditor.
   * 
   * This is an asynchronous activate, as the new editor will execute
   * separately from the other workbench windows.
   */
  public static void startViewEditor(ViewEditorInput viewInput) {
    WorkspaceTools.asyncExec(new ViewEditorRunnable(viewInput));
  }

  private static class ViewEditorRunnable implements Runnable {
    private final ViewEditorInput input;

    private ViewEditorRunnable(ViewEditorInput input) {
      this.input = input;
    }

    @Override
    public void run() {
      IWorkbenchPage page = PlatformUI.getWorkbench()
          .getActiveWorkbenchWindow().getActivePage();
      try {
        page.openEditor(input, ViewEditor.ID);
      } catch (PartInitException errInit) {
        ViewDocLogger.LOG.error("Unable to start NodeListEditor", errInit);
      }
    }
  }
}
