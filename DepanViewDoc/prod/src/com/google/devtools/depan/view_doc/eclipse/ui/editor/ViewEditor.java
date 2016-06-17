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

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;
import com.google.devtools.depan.eclipse.preferences.PreferencesIds;
import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeColorSupplier.Monochrome;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeRatioSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeShapeSupplier;
import com.google.devtools.depan.eclipse.visualization.ogl.NodeSizeSupplier;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.NewEditorHelper;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.layout.GridLayoutGenerator;
import com.google.devtools.depan.view_doc.layout.LayoutContext;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.LayoutGenerators;
import com.google.devtools.depan.view_doc.layout.LayoutScaler;
import com.google.devtools.depan.view_doc.layout.LayoutUtil;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.OptionPreferences;
import com.google.devtools.depan.view_doc.model.Point2dUtils;
import com.google.devtools.depan.view_doc.model.ScenePreferences;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;
import com.google.devtools.depan.view_doc.persistence.ViewDocXmlPersist;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import org.eclipse.ui.part.MultiPageEditorPart;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * An Editor for a DepAn ViewDocument.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditor extends MultiPageEditorPart {

  public static final String ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor";

  /** How much room to consume for full viewport layout scaling. */
  public static final double FULLSCALE_MARGIN = 0.9;

  /**
   * Defines the OpenGL distance between two points that should be considered
   * equivalent to zero.
   * 
   * <p>Ideally, this should be obtained from the GLPanel/View, perhaps as the
   * half the OpenGL distance between two pixels.  But that will have to wait.
   */
  private static final double ZERO_THRESHOLD = 0.1;

  /////////////////////////////////////
  // Editor state for persistence

  /** State of the view.  Only this data is saved. */
  private ViewDocument viewInfo;

  /** Persistent location for the viewInfo. */
  private IFile viewFile;

  /**
   * Standard resources for manipulating this graphs and its set of nodes
   * and relations.
   */
  private GraphResources viewResources;

  /**
   * Base name to use for created files. Typically set through the
   * ViewEditorInput supplied at editor startup.
   */
  private String baseName;

  /** Dirty state. */
  private boolean isDirty = true;

  /////////////////////////////////////
  // Resources to release in the dispose() method

  /** Handle changes to the user preferences, such a node locations. */
  private ViewPrefsListener viewPrefsListener;

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

  /**
   * The subset of {@link GraphNode}s and {@link GraphEdge}s
   * (from {@link #viewGraph}) that are currently exposed and therefore
   * being rendered.  The collapser is responsible for these transformations.
   */
  private GraphModel exposedGraph;

  private NodeColorFactory nodeColorFactory;

  /////////////////////////////////////
  // Dispatch errors to go our logger

  private abstract static class SimpleDispatcher<T>
      implements ListenerManager.Dispatcher<T> {

    @Override
    public void captureException(RuntimeException errAny) {
      ViewDocLogger.logException("Listener dispatch failure", errAny);
    }
  }

  /////////////////////////////////////
  // Basic Getters and Setters

  public GraphModel getViewGraph() {
    return viewGraph;
  }

  public void addViewPrefsListener(ViewPrefsListener listener) {
    viewInfo.addPrefsListener(listener);
  }

  public void removeViewPrefsListener(ViewPrefsListener listener) {
    viewInfo.removePrefsListener(listener);
  }

  public GraphModel getParentGraph() {
    return viewInfo.getParentGraph();
  }

  public Collection<RelationSetDescriptor> getRelationSetsChoices() {
    return viewResources.getRelationSetsChoices();
  }

  public RelationSetDescriptor getDefaultRelationSet() {
    return viewResources.getDefaultRelationSet();
  }

  public GraphEdgeMatcherDescriptor getTreeEdgeMatcher() {
    return viewInfo.getLayoutFinder();
  }

  public RelationSetDescriptor getDisplayRelationSet() {
    return viewInfo.getDisplayRelationSetDescriptor();
  }

  public void setDisplayRelationSet(RelationSetDescriptor newDisplay) {
    viewInfo.setDisplayRelationSetDescriptor(newDisplay);
  }

  public ScenePreferences getScenePrefs() {
    return viewInfo.getScenePrefs();
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
      ViewDocLogger.logException("Unable to create View pages", err);
    }
  }

  private void createDiagramPage() {
    Composite parent = new Composite(getContainer(), SWT.H_SCROLL | SWT.V_SCROLL);

    GridLayout pageLayout = new GridLayout();
    pageLayout.numColumns = 1;
    parent.setLayout(pageLayout);

    // bottom composite containing main diagram
    renderer = createView(parent);
    renderer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

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
      return new View(parent, SWT.NONE, this);
    } catch (Exception err) {
      ViewDocLogger.logException("Unable to create View pages", err);
      throw err;
    }
  }

  /**
   * Everything that should happen before the dry-run and the start.
   * Since {@link #setGraphModel} is invoked early, all display property
   * entities should be defined.
   */
  private void prepareView() {
    // Prepare renderer with full set of nodes and edges.
    renderer.setGraphModel(getViewGraph());
    renderer.initializeScenePrefs(getScenePrefs());
    renderer.initializeNodeLocations(viewInfo.getNodeLocations());
    renderer.setNodeNeighbors(nodeColorFactory.getJungGraph());
    initSelectedNodes(getSelectedNodes());
    initEdgeRendering();
    prepareRenderOptions();
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

  private void createDetailsPage() {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.marginTop = 9;
    parent.setLayout(layout);

    GridData fillGrid = new GridData(SWT.FILL, SWT.FILL, true, false);

    Label nameLabel = new Label(parent, SWT.NONE);
    final Text name = new Text(parent, SWT.BORDER | SWT.SINGLE);

    nameLabel.setText("Description");
    String descr = Strings.nullToEmpty(viewInfo.getDescription());
    name.setText(descr);

    name.setLayoutData(fillGrid);

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
    viewInfo.addPrefsListener(viewPrefsListener);

    // Force a layout if there are no locations.
    if (viewInfo.getNodeLocations().isEmpty()) {
      markDirty();
      addDrawingListener(new DrawingListener() {

        @Override
        public void updateDrawingBounds(
            Rectangle2D drawing, Rectangle2D viewport) {
          // Don't layout nodes if no layout is defined
          LayoutGenerator selectedLayout = getSelectedLayout();
          if (null == selectedLayout ) {
            selectedLayout = new GridLayoutGenerator();
          }

          // Run the layout process on all nodes in the view.
          applyLayout(selectedLayout,
              viewInfo.getLayoutFinder(), viewInfo.getViewNodes());

          // Only need to do this once on startup
          removeDrawingListener(this);
        }});
    }
  }

  private void initFromInput(IEditorInput input) throws PartInitException {
    if (input instanceof ViewEditorInput) {
      ViewEditorInput editorInput = (ViewEditorInput) input;
      viewFile = null; // not yet saved
      baseName = editorInput.getBaseName();
      viewInfo = editorInput.getViewDocument();
      setPartName(calcPartName());
      markDirty();
    } else if (input instanceof IFileEditorInput) {
      try {
        viewFile = ((IFileEditorInput) input).getFile();
        baseName = viewFile.getName();
        viewInfo = loadViewDocument(viewFile);
        setPartName(calcPartName());
        setDirtyState(false);
      } catch (RuntimeException e) {
        viewFile = null;
        viewInfo = null;
        throw new PartInitException(
            "Unable to load view from " + viewFile.getFullPath().toString());
      }
    } else {
      throw new PartInitException(
          "Input for editor is not suitable for the ViewEditor");
    }
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

    viewResources = viewInfo.getGraphResources();

    exposedGraph = buildExposedGraph();

    nodeColorFactory = new NodeColorFactory(
        exposedGraph, exposedGraph.getNodes());
    nodeColorFactory.buildJungGraph();
  }

  private GraphModel buildExposedGraph() {
    // TODO : refit with collapser
    return viewGraph;
  }

  /**
   * Provide the {@link GraphModel} that is currently being rendered.
   * Some nodes or edges of the basis graph may be hidden, compressed,
   * or aliased.
   */
  public GraphModel getExposedGraph() {
    return exposedGraph;
  }

  /**
   * Release the resource held by the ViewEditor:
   * - ViewModelListener
   * - Underlying view.
   */
  @Override
  public void dispose() {
    if (null != viewPrefsListener) {
      viewInfo.removePrefsListener(viewPrefsListener);
      viewPrefsListener = new Listener();
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

  /**
   * Load a view document from a file.
   */
  private static ViewDocument loadViewDocument(IFile viewFile) {
    ViewDocXmlPersist loader = ViewDocXmlPersist.build(true, "load");
    return loader.load(viewFile.getLocationURI());
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    // If there are any file problems, do this as a Save As ..
    if (null == viewFile) {
      doSaveAs();
      return;
    }

    saveFile(viewFile, monitor, "save");
    if (null != monitor) {
      monitor.done();
    }
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    saveas.setOriginalFile(getSaveAsFile());
    saveas.setOriginalName(calcSaveAsName());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }

    // get the file relatively to the workspace.
    IFile saveFile = calcViewFile(saveas.getResult());
    // TODO: set up a progress monitor
    saveFile(saveFile, null, "saveAs");

    viewFile = saveFile;
    setPartName(viewFile.getName());
    }

  private IFile getSaveAsFile() {
    if (null != viewFile) {
      return viewFile;
    }

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject[] projs = root.getProjects();

    // Propose a name only if there is exactly one project.
    if (projs.length != 1) {
      return null;
    }

    IProject proj = projs[0];
    String name = calcSaveAsName();
    return proj.getFile(name);
  }

  public String getBaseName() {
    return baseName;
  }

  /**
   * Provide a likely file name for this view editor's contents.
   */
  private String calcSaveAsName() {
    // Basename can be null (on initialization, etc.).
    // Rare, but cope with it.
    if (null != baseName && !baseName.isEmpty()) {
      return baseName;
    }

    String graphName = viewInfo.getGraphModelLocation().getName();
    String label = NewEditorHelper.newEditorLabel(graphName);
    baseName = label;
    return label;
  }

  /**
   * Provide a tab name for this editor./
   */
  private String calcPartName() {
    if (null != baseName && !baseName.isEmpty()) {
      return baseName;
    }

    // TODO: Also set baseName .. see getSaveAsName()
    String graphName = viewInfo.getGraphModelLocation().getName();
    return NewEditorHelper.newEditorLabel(
        graphName + " - New View");
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
    ViewDocXmlPersist persist = ViewDocXmlPersist.build(false, opLabel);
    WorkspaceTools.saveDocument(file, viewInfo, persist, monitor);

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

  public RelationSet getVisibleRelationSet() {
    return viewInfo.getVisibleRelationSet();
  }

  public boolean isVisibleRelation(Relation relation) {
    return viewInfo.isVisibleRelation(relation);
  }

  public void setVisibleRelation(Relation relation, boolean isVisible) {
    viewInfo.setVisibleRelation(relation, isVisible);
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

  public Collection<Relation> getDisplayRelations() {
    // TODO: return viewInfo.getDisplayRelations();
    return RelationRegistry.getRegistryRelations();
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return viewInfo.getRelationProperty(relation);
  }

  public void setRelationProperty(
      Relation relation, EdgeDisplayProperty relProp) {
    viewInfo.setRelationProperty(relation, relProp);
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
   * Edges that lack an explict {@link EdgeDisplayProperty} are
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
  // Update Graph Layouts

  public String getLayoutName() {
    return viewInfo.getSelectedLayout();
  }

  public LayoutGenerator getSelectedLayout() {
    return LayoutGenerators.getByName(getLayoutName());
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
    scaleToViewport(getExposedGraph().getNodes(), getNodeLocations());
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
        getExposedGraph().getNodes(), getNodeLocations(),
        translater);

    editNodeLocations(changes);
  }

  private void scaleToViewport(
      Collection<GraphNode> layoutNodes, Map<GraphNode, Point2D> locations) {
    Rectangle2D viewport = renderer.getOGLViewport();
    Map<GraphNode, Point2D> changes = 
            computeFullViewScale(layoutNodes, locations, viewport);
    editNodeLocations(changes);
  }

  /**
   * Scale the nodes so that they would fit in the viewport, but don't force
   * them into the viewport.
   * 
   * @param layoutNodes
   * @param locations
   * @param viewport
   * @return updated node locations that are the same size as the viewport
   */
  private Map<GraphNode, Point2D> computeFullViewScale(
          Collection<GraphNode> layoutNodes,
          Map<GraphNode, Point2D> locations,
          Rectangle2D viewport) {

    if (layoutNodes.size() <= 0) {
      return Collections.emptyMap();
    }

    // If there is only one node, don't change its location
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    if (layoutNodes.size() == 1) {
      GraphNode singletonNode = layoutNodes.iterator().next();
      Point2D singletonLocation = locations.get(singletonNode);
      if (null != singletonLocation) {
        result.put(singletonNode, singletonLocation);
      }
      return result;
    }

    // Scale all the nodes to fit within the indicated region
    LayoutScaler scaler = new LayoutScaler(layoutNodes, locations);
    double scaleView = scaleWithMargin(scaler, viewport);
    Point2dUtils.Translater translater =
            Point2dUtils.newScaleTranslater(scaleView, scaleView);
    return Point2dUtils.translateNodes(layoutNodes, locations, translater);
  }

  private double scaleWithMargin(
          LayoutScaler scaler, Rectangle2D viewport) {
    return FULLSCALE_MARGIN
            * scaler.getFullViewScale(viewport, ZERO_THRESHOLD);
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

  public void applyLayout(LayoutGenerator layout) {
    applyLayout(layout, viewInfo.getLayoutFinder());
  }

  public void applyLayout(
          LayoutGenerator layout, GraphEdgeMatcherDescriptor edgeMatcher) {

    Collection<GraphNode> layoutNodes = getLayoutNodes();
    if (layoutNodes.size() < 2) {
      // TODO: Notify user that a single node cannot be positioned.
      return;
    }
    applyLayout(layout, edgeMatcher, layoutNodes);
  }

  private Collection<GraphNode> getLayoutNodes() {
    Collection<GraphNode> picked = getSelectedNodes();
    if (picked.isEmpty()) {
      return getExposedGraph().getNodes();
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
      LayoutGenerator layout, GraphEdgeMatcherDescriptor edgeMatcher,
      Collection<GraphNode> layoutNodes) {

    LayoutContext context = new LayoutContext();
    context.setGraphModel(getExposedGraph());
    context.setMovableNodes(layoutNodes);
    context.setEdgeMatcher(edgeMatcher);
    context.setNodeLocations(getNodeLocations());

    Rectangle2D viewport = renderer.getOGLViewport();
    Rectangle2D layoutViewport = Point2dUtils.scaleRectangle(viewport, 0.7);
    context.setViewport(layoutViewport);

    Map<GraphNode, Point2D> changes = LayoutUtil.calcPositions(
            layout, context, layoutNodes);

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
    drawingListeners.fireEvent(new ListenerManager.Dispatcher<DrawingListener>() {
      @Override
      public void dispatch(DrawingListener listener) {
        listener.updateDrawingBounds(drawing, viewport);
      }

      @Override
      public void captureException(RuntimeException errAny) {
        ViewDocLogger.logException(
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
    return hierarchies.getHierarchy(edgeMatcher.getEdgeMatcher());
  }

  /**
   * Provide the application's Display (i.e. the event loop)
   * @return application's Display
   */
  private static Display getWorkbenchDisplay() {
    return PlatformUI.getWorkbench().getDisplay();
  }

  /**
   * Post an update on the status line.
   * @param statusText text to display on status line
   */
  private void updateStatusLine(final String statusText) {
    getWorkbenchDisplay().asyncExec(new Runnable() {

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

  private void prepareRenderOptions() {
    prepareColorSupplier();
    updateRootHighlight(
        isOptionChecked(OptionPreferences.ROOTHIGHLIGHT_ID));
    updateNodeStretchRatio(
        isOptionChecked(OptionPreferences.STRETCHRATIO_ID));
    updateNodeSize(
        isOptionChecked(OptionPreferences.SIZE_ID));
    updateNodeStrokeHighlight(
        isOptionChecked(OptionPreferences.STROKEHIGHLIGHT_ID));
    updateNodeShape(
        isOptionChecked(OptionPreferences.SHAPE_ID));
  }

  private void handleOptionChange(String optionId, String value) {
    markDirty();
    if (OptionPreferences.ROOTHIGHLIGHT_ID.equals(optionId)) {
      updateRootHighlight(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.STRETCHRATIO_ID.equals(optionId)) {
      updateNodeStretchRatio(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.SIZE_ID.equals(optionId)) {
      updateNodeSize(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.STROKEHIGHLIGHT_ID.equals(optionId)) {
      updateNodeStrokeHighlight(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.SHAPE_ID.equals(optionId)) {
      updateNodeShape(Boolean.parseBoolean(value));
      return;
    }
    if (OptionPreferences.OPTION_DESCRIPTION.equals(optionId)) {
      updateOptionDescription(value);
      return;
    }
  }

  private void prepareColorSupplier() {
    for (GraphNode root : getExposedGraph().getNodes()) {
      renderer.setNodeColorSupplier(root, nodeColorFactory.getColorSupplier(root));
    }
    return;
  }

  private void updateRootHighlight(boolean enable) {
    List<GraphNode> roots = nodeColorFactory.getRoots();
    if (enable) {
      Monochrome seedColor = new NodeColorSupplier.Monochrome(Color.GREEN);
      for (GraphNode root : roots) {
        renderer.setNodeColorSupplier(root, seedColor);
      }
      return;
    }

    for (GraphNode root : roots) {
      renderer.setNodeColorSupplier(root, nodeColorFactory.getColorSupplier(root));
    }
    return;
  }

  private void updateNodeStretchRatio(boolean enable) {
    if (enable) {
      for (GraphNode node : getExposedGraph().getNodes()) {
        renderer.setNodeRatioSupplier(node, nodeColorFactory.getRatioSupplier(node));
      }
      return;
    }

    for (GraphNode node : getExposedGraph().getNodes()) {
      renderer.setNodeRatioSupplier(node, NodeRatioSupplier.FULL);
    }
  }

  private void updateNodeSize(boolean enable) {
    if (enable) {
      NodeSize size = NodeSize.valueOf(
          PreferencesIds.getInstanceNode().get(
              NodePreferencesIds.NODE_SIZE,
              NodeSize.getDefault().toString()));

      for (GraphNode node : getExposedGraph().getNodes()) {
        NodeSizeSupplier supplier =
            nodeColorFactory.getSizeSupplier(node, size);
        renderer.setNodeSizeSupplier(node, supplier);
      }
      return;
    }

    for (GraphNode node : getExposedGraph().getNodes()) {
      renderer.setNodeSizeSupplier(node, NodeSizeSupplier.STANDARD);
    }
  }

  private void updateNodeShape(boolean enable) {
    if (enable) {
      for (GraphNode node : getExposedGraph().getNodes()) {
        NodeShape mode = NodeShape.valueOf(
            PreferencesIds.getInstanceNode().get(
                NodePreferencesIds.NODE_SHAPE,
                NodeShape.getDefault().toString()));

        NodeShapeSupplier nodeShape =
            nodeColorFactory.getShapeSupplier(node, mode);
        renderer.setNodeShapeSupplier(node, nodeShape);
      }
      return;
    }

    for (GraphNode node : getExposedGraph().getNodes()) {
      renderer.setNodeShapeSupplier(node, NodeShapeSupplier.STANDARD);
    }
  }


  private void updateNodeStrokeHighlight(boolean enable) {
    renderer.activateNodeStroke(enable);
  }

  private void updateOptionDescription(String value) {
    // No need to update name member.
    // That might cause an update loop.
    // Just the markDirty() from the main handler method.
  }

  /////////////////////////////////////
  // Receiver for notifications from the ViewDocument
  // Most events are mapped to standard re-dispatch methods.

  /**
   * Handle notifications from ViewDocument (mostly UserPreferences) that
   * some user-controlled feature of the view has changed.
   */
  private class Listener implements ViewPrefsListener {
    @Override
    public void relationSetVisibleChanged(RelationSet visibleSet) {
      if (null == renderer) {
        return;
      }
      // The visible relations in view preferences has already
      // changed to the supplied visibleSet.
      updateEdgesToVisible(RelationSets.ALL);
      markDirty();
    }

    @Override
    public void relationVisibleChanged(Relation relation, boolean visible) {
      if (null == renderer) {
        return;
      }
      // The relation's visibility in view preferences has already
      // changed to the supplied value.
      updateEdgesToVisible(RelationSets.createSingle(relation));
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

/* TODO
    @Override
    public void collapseChanged(
        Collection<CollapseData> created,
        Collection<CollapseData> removed,
        Object author) {
      updateSelectedNodes(removed, author);
      updateExposedGraph();
      renderer.updateCollapseChanges(created, removed);
      markDirty();
    }
*/

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
  }

  /////////////////////////////////////
  // Run the new ViewEditor

  /**
   * Activate a new ViewEditor.
   * 
   * This is an asynchronous activate, as the new editor will execute
   * separately from the other workbench windows.
   */
  public static void startViewEditor(ViewDocument newInfo, String baseName) {
    final ViewEditorInput input = new ViewEditorInput(newInfo, baseName);
    getWorkbenchDisplay().asyncExec(new Runnable() {

      @Override
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          page.openEditor(input, ViewEditor.ID);
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
