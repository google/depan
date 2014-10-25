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

import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.stats.ElementKindStats;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.utils.ListenerManager;
import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptor;
import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptors;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptors;
import com.google.devtools.depan.eclipse.views.tools.RelationCount;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutContext;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerators;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutScaler;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutUtil;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.basic.ForwardIdentityRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.TreeModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.XStream;

import edu.uci.ics.jung.algorithms.importance.KStepMarkov;
import edu.uci.ics.jung.graph.DirectedGraph;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * An Editor for a DepAn ViewDocument.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditor extends MultiPageEditorPart {

  public static final String ID =
      "com.google.devtools.depan.eclipse.editors.ViewEditor";

  private static final Logger logger =
      Logger.getLogger(ViewEditor.class.getName());

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

  private ListenerManager<DrawingListener> drawingListeners =
      new ListenerManager<DrawingListener>();

  /////////////////////////////////////
  // Alternate graph perspectives and derived data
  // used in various tools and viewers

  private GraphModel viewGraph;

  private GraphModel exposedGraph;

  /** Used for rendering and ranking in the rending pipe. */
  private DirectedGraph<GraphNode, GraphEdge> jungGraph;

  private Map<GraphNode, Double> ranking;

  private RelationCount.Settings relationCountData =
    new RelationCount.Settings();

  private List<RelSetDescriptor> relSetChoices;

  private Collection<ElementKindDescriptor> elementKindChoices;

  private Collection<ElementKindStats.Info> elementKindStats;

  /////////////////////////////////////
  // Basic Getters and Setters

  public GraphModel getViewGraph() {
    return viewGraph;
  }

  public GraphModel getParentGraph() {
    return viewInfo.getParentGraph();
  }

  public List<SourcePlugin> getBuiltinAnalysisPlugins() {
    return viewInfo.getBuiltinAnalysisPlugins();
  }

  public Collection<RelationshipSet> getBuiltinAnalysisRelSets() {
    return viewInfo.getBuiltinAnalysisRelSets();
  }

  public List<RelSetDescriptor> getRelSetChoices() {
    return relSetChoices;
  }

  public RelationshipSet getContainerRelSet() {
    return viewInfo.getDefaultContainerRelSet();
  }

  public RelationshipSet getDisplayRelationSet() {
    return viewInfo.getDisplayRelationSet();
  }

  public void setDisplayRelationSet(RelationshipSet newDisplay) {
    viewInfo.setDisplayRelationSet(newDisplay);
  }

  /**
   * @return
   */
  public Collection<ElementKindDescriptor> getElementKinds() {
    return elementKindChoices;
  }

  public Collection<ElementKindStats.Info> getElementKindStats() {
    return elementKindStats;
  }

  public DirectedGraph<GraphNode, GraphEdge> getJungGraph() {
    return jungGraph;
  }

  public Map<GraphNode, Double> getNodeRanking() {
    return ranking;
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
    createDiagramPage();
    createDetailsPage();
  }

  private void createDiagramPage() {
    Composite parent = new Composite(getContainer(), SWT.H_SCROLL | SWT.V_SCROLL);

    GridLayout pageLayout = new GridLayout();
    pageLayout.numColumns = 1;
    parent.setLayout(pageLayout);

    // bottom composite containing main diagram
    renderer = new View(parent, SWT.NONE, this);
    renderer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // Configure the rendering pipe before listening for changes.
    renderer.setGraphModel(getViewGraph(), getJungGraph(), getNodeRanking());
    renderer.initCameraPosition(viewInfo.getScenePrefs());
    renderer.initializeNodeLocations(viewInfo.getNodeLocations());
    initCollapseRendering(viewInfo.getCollapseState());
    initSelectedNodes(getSelectedNodes());
    initEdgeRendering();
    renderer.start();

    // Force a layout if there are no locations.
    if (viewInfo.getNodeLocations().isEmpty()) {
      addDrawingListener(new DrawingListener() {

        @Override
        public void updateDrawingBounds(
            Rectangle2D drawing, Rectangle2D viewport) {
          // Don't layout nodes if no layout is defined
          LayoutGenerator selectedLayout = getSelectedLayout();
          if (null == selectedLayout ) {
            return;
          }

          // Run the layout process on all nodes in the view.
          applyLayout(selectedLayout,
              viewInfo.getLayoutFinder(), viewInfo.getViewNodes());

          // Only need to do this once on startup
          removeDrawingListener(this);
        }});
    }

    int index = addPage(parent);
    setPageText(index, "Graph View");
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
    name.setText(viewInfo.getDescription());

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
      } catch (IOException e) {
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
    updateExposedGraph();

    hierarchies = new HierarchyCache<NodeDisplayProperty>(
        viewInfo.getNodeDisplayPropertyProvider(),
        getViewGraph());

    relSetChoices = RelSetDescriptors.buildViewChoices(viewInfo);
    elementKindChoices = ElementKindDescriptors.buildViewChoices(viewInfo);

    ElementKindStats stats = new ElementKindStats(elementKindChoices);
    stats.incrStats(viewInfo.getViewNodes());
    elementKindStats = stats.createStats();

    jungGraph = buildJungGraph();
    ranking = rankGraph(jungGraph);
  }

  /**
   * Associate to each node a value based on it's "importance" in the graph.
   * The selected algorithm is a Page Rank algorithm.
   *
   * The result is stored in the map {@link #ranking}.
   */
  @SuppressWarnings("unused") // Retained legacy code
  private Map<GraphNode, Double> rankGraphX(
          DirectedGraph<GraphNode, GraphEdge> graph) {

    KStepMarkov<GraphNode, GraphEdge> ranker =
        new KStepMarkov<GraphNode, GraphEdge>(graph, null, 6, null);
    ranker.setRemoveRankScoresOnFinalize(false);
    ranker.evaluate();

    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : exposedGraph.getNodes()) {
      result.put(node, ranker.getVertexRankScore(node));
    }

    return result;
  }

  private Map<GraphNode, Double> rankGraph(
      DirectedGraph<GraphNode, GraphEdge> graph) {

    Double unit = 1.0;
    Map<GraphNode, Double> result = Maps.newHashMap();
    for (GraphNode node : exposedGraph.getNodes()) {
      result.put(node, unit);
    }

    return result;
  }

  private DirectedGraph<GraphNode, GraphEdge> buildJungGraph( ) {
    LayoutContext context = new LayoutContext();
    context.setGraphModel(getExposedGraph());
    context.setMovableNodes(viewGraph.getNodes());
    // TODO: Compute ranking based on selected relations
    context.setRelations(ForwardIdentityRelationFinder.FINDER);

    return LayoutUtil.buildJungGraph(context);
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
  private static ViewDocument loadViewDocument(IFile viewFile)
      throws IOException {
    ObjectXmlPersist persist =
        new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
    return (ViewDocument) persist.load(viewFile.getRawLocationURI());
  }

  /**
   * Save a view document to a file.
   */
  private static void saveViewDocument(IFile viewFile, ViewDocument viewInfo)
      throws IOException {
    XStream xstream = XStreamFactory.newStaxXStream();
    XStreamFactory.configureRefXStream(xstream);
    ObjectXmlPersist persist = new ObjectXmlPersist(xstream);
    persist.save(viewFile.getRawLocationURI(), viewInfo);
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
    if (null != monitor)
      monitor.setTaskName("Writing file " + file.getName());

    try {
      saveViewDocument(file, viewInfo);
    } catch (IOException err) {
      logger.log(Level.SEVERE,
          "Unable to " + opLabel + " " + file.getName(), err);
      if (null != monitor)
        monitor.setCanceled(true);
    }

    try {
      // WEIRD:  refreshLocal() directly on the resource often works,
      // but here we sometimes get a conflict.
      file.refreshLocal(1, monitor);
    } catch (CoreException errCore) {
      logger.log(Level.WARNING,
          "Failed resource refresh after " + opLabel + " to "
              + file.getFullPath().toString(),
          errCore);
    }

    setDirtyState(false);
  }

  /**
   * Ensure that we have a file extension on the file name.
   * 
   * @param savePath Initial save path from user
   * @return valid IFile with an extension.
   */
  private IFile calcViewFile(IPath savePath) {
    if (null == savePath.getFileExtension()) {
      savePath = savePath.addFileExtension(ViewDocument.EXTENSION);
    }
    return ResourcesPlugin.getWorkspace().getRoot().getFile(savePath);
  }

  /////////////////////////////////////
  // Graph elements

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

  public void setRelationVisible(Relation relation, boolean isVisible) {
    EdgeDisplayProperty edgeProp = getRelationProperty(relation);
    if (null == edgeProp) {
      edgeProp = new EdgeDisplayProperty();
      edgeProp.setVisible(isVisible);
      viewInfo.setRelationProperty(relation, edgeProp);
      return;
    }

    // Don't change a relation that is already visible
    if (edgeProp.isVisible() == isVisible) {
      return;
    }
    edgeProp.setVisible(isVisible);
    viewInfo.setRelationProperty(relation, edgeProp);
  }

  public Collection<Relation> getDisplayRelations() {
    return viewInfo.getDisplayRelations();
  }

  public EdgeDisplayProperty getRelationProperty(Relation relation) {
    return viewInfo.getRelationProperty(relation);
  }

  private void initEdgeRendering() {
    for (GraphEdge edge : viewGraph.getEdges()) {

      // If the edge has explicit display properties, use those.
      EdgeDisplayProperty edgeProp = viewInfo.getEdgeProperty(edge);
      if (null != edgeProp) {
        renderer.updateEdgeProperty(edge, edgeProp);
        continue;
      }

      EdgeDisplayProperty relationProp =
          getRelationProperty(edge.getRelation());
      if (null == relationProp) {
        renderer.setEdgeVisible(edge, false);
        continue;
      }

      renderer.updateEdgeProperty(edge, relationProp);
    }
  }

  private void updateEdgesToRelations() {
    for (GraphEdge edge : viewGraph.getEdges()) {
      // If the edge has explicit display properties, leave those.
      EdgeDisplayProperty edgeProp = viewInfo.getEdgeProperty(edge);
      if (null != edgeProp) {
        continue;
      }

      EdgeDisplayProperty relationProp =
          getRelationProperty(edge.getRelation());
      if (null == relationProp) {
        renderer.setEdgeVisible(edge, false);
        continue;
      }

      renderer.updateEdgeProperty(edge, relationProp);
    }
  }

  /////////////////////////////////////
  // Collapsed presentations

  public GraphModel getExposedGraph() {
    return exposedGraph;
  }

  public void autoCollapse(DirectedRelationFinder finder, Object author) {
    GraphData<NodeDisplayProperty> tree = hierarchies.getHierarchy(finder);
    TreeModel treeData = tree.getTreeModel();
    collapseTree(treeData, author);
  }

  public void collapseTree(TreeModel treeData, Object author) {
    viewInfo.collapseTree(getViewGraph(), treeData, author);
  }

  public void collapse(
      GraphNode master, Collection<GraphNode> picked,
      boolean erase, Object author) {
    viewInfo.collapse(master, picked, erase, author);
  }

  public void uncollapse(
      GraphNode master, boolean deleteGroup, Object author) {
    viewInfo.uncollapse(master, deleteGroup, author);
  }

  private void updateExposedGraph() {
    exposedGraph = viewInfo.buildExposedGraph(viewGraph);
  }

  private void initCollapseRendering(Collection<CollapseData> state) {
    renderer.updateCollapseChanges(state, CollapseData.EMPTY_LIST);
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
          LayoutGenerator layout, DirectedRelationFinder relationFinder) {

    Collection<GraphNode> layoutNodes = getLayoutNodes();
    if (layoutNodes.size() < 2) {
      // TODO: Notify user that a single node cannot be positioned.
      return;
    }
    applyLayout(layout, relationFinder, layoutNodes);
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
   * @param relationFinder {@link DirectedRelationFinder} to restrict
   *        relations for layout.
   * @param layoutNodes nodes that participate in the layout
   */
  private void applyLayout(
      LayoutGenerator layout, DirectedRelationFinder relationFinder,
      Collection<GraphNode> layoutNodes) {

    LayoutContext context = new LayoutContext();
    context.setGraphModel(getExposedGraph());
    context.setMovableNodes(layoutNodes);
    context.setRelations(relationFinder);
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
        logger.warning(errAny.toString());
      }
    });
  }

  /**
   * Capture stable points in the diagram rendering.
   */
  public void sceneChanged() {
    markDirty();
    ScenePreferences prefs = viewInfo.getScenePrefs();
    if (null == prefs) {
      prefs = ScenePreferences.getDefaultScenePrefs();
      viewInfo.setScenePrefs(prefs);
    }

    // Capture the newly stable current camera position in the prefs object.
    renderer.saveCameraPosition(prefs);
  }

  /////////////////////////////////////
  // Notifications from ViewEditor toward Tools and other ViewEditor listeners

  private abstract static class SimpleDispatcher
      implements ListenerManager.Dispatcher<SelectionChangeListener> {

    @Override
    public void captureException(RuntimeException errAny) {
      logger.log(Level.WARNING, "Listener dispatch failure", errAny);
    }
  }

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

    selectionListeners.fireEvent(new SimpleDispatcher() {
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

    selectionListeners.fireEvent(new SimpleDispatcher() {
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
      DirectedRelationFinder relFinder) {
    return hierarchies.getHierarchy(relFinder);
  }

  public RelationCount.Settings getRelationCountData() {
    return relationCountData;
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
  // Receiver for notifications from the ViewDocument
  // Most events are mapped to standard re-dispatch methods.

  /**
   * Handle notifications from ViewDocument (mostly UserPreferences) that
   * some user-controlled feature of the view has changed.
   */
  private class Listener implements ViewPrefsListener {
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
      updateEdgesToRelations();
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
    public void collapseChanged(
        Collection<CollapseData> created,
        Collection<CollapseData> removed,
        Object author) {
      updateExposedGraph();
      renderer.updateCollapseChanges(created, removed);
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
    public void descriptionChanged(String description) {
      // TODO(leeca): update description widget, if it is not the source
      // of the change.
      markDirty();
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
