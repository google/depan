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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.eclipse.preferences.LabelPreferencesIds;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.LabelPosition;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;
import com.google.devtools.depan.eclipse.stats.ElementKindStats;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.utils.ListenerManager;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptor;
import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptors;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptors;
import com.google.devtools.depan.eclipse.views.tools.RelationCount;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.eclipse.visualization.ogl.GLRegion;
import com.google.devtools.depan.eclipse.visualization.ogl.RendererChangeListener;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.EdgeDisplayProperty;
import com.google.devtools.depan.view.NodeDisplayProperty;

import com.thoughtworks.xstream.XStream;

import edu.uci.ics.jung.algorithms.importance.PageRank;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * An Editor for a DepAn ViewDocument.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditor extends MultiPageEditorPart
    implements IPreferenceChangeListener {

  public static final String ID =
      "com.google.devtools.depan.eclipse.editors.ViewEditor";

  private static final Logger logger =
      Logger.getLogger(ViewEditor.class.getName());

  private static final List<GraphNode> EMPTY_NODE_LIST =
      Collections.<GraphNode>emptyList();

  /** How much room to consume for full viewport layout scaling. */
  private static final double FULLSCALE_MARGIN = 0.9;

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

  /** Dirty state. */
  private boolean isDirty = true;

  /**
   * Skip layout on initial render if existing positions should 
   * be preserved.
   */
  private boolean skipLayout;

  /////////////////////////////////////
  // Resources to release in the dispose() method

  /** Handle changes to the user preferences, such a node locations. */
  private ViewPrefsListener viewPrefsListener;

  /** Results from defining hierarchies over the nodes. */
  private HierarchyCache<NodeDisplayProperty> hierarchies;

  /** The visualization View that handles rendering. */
  private View renderer;

  /** Callback for changes from the renderer. */
  private RendererChangeListener rendererCallback;

  /**
   * Forward only selection change events to interested parties.
   */
  private ListenerManager<SelectionChangeListener> selectionListeners =
      new ListenerManager<SelectionChangeListener>();

  /////////////////////////////////////
  // Alternate graph perspectives and derived data
  // used in various tools and viewers

  private GraphModel viewGraph;

  private GraphModel exposedGraph;

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

  /**
   * Provide access to the underlying OpenGL renderer.  This is available
   * primarily to assist with configuration and preference settings.
   * 
   * @return reference to configurable renderer
   */
  public View getRenderer() {
    return renderer;
  }

  public RendererChangeListener getRendererCallback() {
    return rendererCallback;
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

  /**
   * Provide the relationship set to use for excluding edges in the diagram.
   * This should be a preference setting, and it should play well with the
   * notion of relationship set roles, but both of those efforts are pending.
   * 
   * @return the EMPTY relationship set.
   */
  public RelationshipSet getEdgeDisplayRelSet() {
    return RelationshipSet.EMTPY;
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
    Composite parent = new Composite(getContainer(), SWT.NONE);
    GridLayout pageLayout = new GridLayout();
    pageLayout.numColumns = 1;
    parent.setLayout(pageLayout);

    // bottom composite containing main diagram
    rendererCallback = new RendererChangeReceiver();
    renderer = new View(parent, SWT.NONE, this);

    renderer.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // Configure the rendering pipe before listening for changes.
    layoutKludge();
    setPreferences();
    initSelectedNodes(getSelectedNodes());

    int index = addPage(parent);
    setPageText(index, "Graph View");
  }

  /**
   * If we could scale the layout to the view-port without setting up the
   * renderer, we wouldn't need this.  But that will require viewport
   * persistence in the ViewPrefs, and the ability for ViewEditor to scale
   * positions to the viewport size.
   */
  private void layoutKludge() {
    renderer.initializeNodeLocations(viewInfo.getNodeLocations());

    // Don't layout nodes if previous location is good.
    if (skipLayout) {
      return;
    }

    // Re-layout nodes if necessary.
    Layouts selectedLayout = viewInfo.getSelectedLayout();
    if (null != selectedLayout ) {
      applyLayout(selectedLayout, viewInfo.getLayoutFinder());
    }

    // Otherwise, just use default positions - user will need to choose layout.
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
    setSite(site);
    setInput(input);

    if (input instanceof ViewEditorInput) {
      ViewEditorInput editorInput = (ViewEditorInput) input;
      viewFile = null; // not yet saved
      viewInfo = editorInput.getViewDocument();
      skipLayout = editorInput.skipLayout();
      String graphName = viewInfo.getGraphModelLocation().getName();
      String partName = NewEditorHelper.newEditorLabel(
          graphName + " - New View");
      setPartName(partName);
      markDirty();
    } else if (input instanceof IFileEditorInput) {
      try {
        viewFile = ((IFileEditorInput) input).getFile();
        viewInfo = loadViewDocument(viewFile);
        skipLayout = (viewInfo.getNodeLocations().size() > 0);
        setPartName(viewFile.getName());
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

    // Synthesize derived graph perspectives
    deriveDetails();

    // Listen to changes in the underlying ViewModel
    viewPrefsListener = new Listener();
    viewInfo.addPrefsListener(viewPrefsListener);

    // TODO(leeca): What does this do?
    // listen the changes in the configuration
    new InstanceScope().getNode(Resources.PLUGIN_ID)
        .addPreferenceChangeListener(this);
  }

  /**
   * Derive a number of alternative presentations and details from the
   * newly open graph view.
   */
  private void deriveDetails() {
    // Synthesize derived graph perspectives
    viewGraph = viewInfo.buildGraphView();
    jungGraph = createJungGraph(getViewGraph());
    updateExposedGraph();

    hierarchies = new HierarchyCache<NodeDisplayProperty>(
        viewInfo.getNodeDisplayPropertyProvider(),
        getViewGraph());

    relSetChoices = RelSetDescriptors.buildViewChoices(viewInfo);
    elementKindChoices = ElementKindDescriptors.buildViewChoices(viewInfo);

    ElementKindStats stats = new ElementKindStats(elementKindChoices);
    stats.incrStats(viewInfo.getViewNodes());
    elementKindStats = stats.createStats();

    ranking = rankGraph();
  }

  /**
   * Associate to each node a value based on it's "importance" in the graph.
   * The selected algorithm is a Page Rank algorithm.
   *
   * The result is stored in the map {@link #ranking}.
   */
  private Map<GraphNode, Double> rankGraph() {
    Map<GraphNode, Double> result = Maps.newHashMap();

    PageRank<GraphNode, GraphEdge> pageRank =
        new PageRank<GraphNode, GraphEdge>(jungGraph, 0.15);
    pageRank.setRemoveRankScoresOnFinalize(false);
    pageRank.evaluate();

    for (GraphNode node : exposedGraph.getNodes()) {
      result.put(node, pageRank.getVertexRankScore(node));
    }
    return result;
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

    if (null != rendererCallback) {
      rendererCallback = null;
    }

    super.dispose();
  }

  @Override
  public void setFocus() {
  }

  private void setPreferences() {
    // setup label preferences
    setLabelPreferences();
    // setup size and shape for nodes with preferences.
    setNodePreferences();
    // setup color preferences.
    setColorsPreferences();
  }

  /////////////////////////////////////
  // Preference handling

  /**
   * Read and setup label preferences.
   */
  private void setLabelPreferences() {
    IEclipsePreferences node =
        new InstanceScope().getNode(Resources.PLUGIN_ID);

    // set label position
    try {
      String val = node.get(LabelPreferencesIds.LABEL_POSITION,
          LabelPreferencesIds.LABEL_POSITION_DEFAULT);
      renderer.getNodeLabel().setLabelPosition(LabelPosition.valueOf(val));
    } catch (IllegalArgumentException ex) {
      // bad label position in the preferences. ignore the change.
      System.err.println("Bad label position in preferences");
    }
  }

  /**
   * Read and setup node rendering preferences (Colors, size, shape, ratio).
   */
  private void setNodePreferences() {
    IEclipsePreferences node =
        new InstanceScope().getNode(Resources.PLUGIN_ID);
    NodeSizePlugin<GraphEdge> nodeSize = renderer.getNodeSize();
    NodeColorPlugin<GraphEdge> nodeColor = renderer.getNodeColor();
    NodeShapePlugin<GraphEdge> nodeShape = renderer.getNodeShape();

    // read enable/disable preferences
    boolean colorEnabled = node.getBoolean(
        NodePreferencesIds.NODE_COLOR_ON, true);
    boolean shapeEnabled = node.getBoolean(
        NodePreferencesIds.NODE_SHAPE_ON, true);
    boolean resizeEnabled = node.getBoolean(
        NodePreferencesIds.NODE_SIZE_ON, false);
    boolean ratioEnabled = node.getBoolean(
        NodePreferencesIds.NODE_RATIO_ON, false);

    // set enable/disable preferences
    nodeColor.setColor(colorEnabled);
    nodeShape.setShapes(shapeEnabled);
    nodeSize.setRatio(ratioEnabled);
    nodeSize.setResize(resizeEnabled);

    // set color mode color
    try {
      NodeColors color = NodeColors.valueOf(node.get(
          NodePreferencesIds.NODE_COLOR,
          NodeColors.getDefault().toString()));
      nodeColor.setColorMode(color);
    } catch (IllegalArgumentException ex) {
      // bad node rendering option. ignore.
      System.err.println("Bad node rendering option (color) in preferences.");
    }

    // set shape mode
    try {
      NodeShape shape = NodeShape.valueOf(node.get(
          NodePreferencesIds.NODE_SHAPE,
          NodeShape.getDefault().toString()));
      nodeShape.setShapeMode(shape);
    } catch (IllegalArgumentException ex) {
      // bad node rendering option. ignore.
      System.err.println("Bad node rendering option (shape) in preferences.");
    }

    // set size mode
    try {
      NodeSize size = NodeSize.valueOf(node.get(
          NodePreferencesIds.NODE_SIZE,
          NodeSize.getDefault().toString()));
      nodeSize.setSizeMode(size);
    } catch (IllegalArgumentException ex) {
      // bad node rendering option. ignore.
      System.err.println("Bad node rendering option (size) in preferences.");
    }

  }

  /**
   * read and setup color preferences.
   */
  private void setColorsPreferences() {
    IEclipsePreferences node =
      new InstanceScope().getNode(Resources.PLUGIN_ID);

    Color back = Tools.getRgb(node.get(
        ColorPreferencesIds.COLOR_BACKGROUND, "255,255,255"));
    Color front = Tools.getRgb(node.get(
        ColorPreferencesIds.COLOR_FOREGROUND, "0,0,0"));
    renderer.setColors(back, front);
  }

  public void preferenceChange(PreferenceChangeEvent event) {
    // changes in the configuration for the views, so redraw the graph.
    if (event.getKey().startsWith(LabelPreferencesIds.LABEL_PREFIX)) {
      setLabelPreferences();
    }
    if (event.getKey().startsWith(ColorPreferencesIds.COLORS_PREFIX)) {
      setColorsPreferences();
    }
    if (event.getKey().startsWith(NodePreferencesIds.NODE_PREFIX)) {
      setNodePreferences();
    }
  }

  /////////////////////////////////////
  // Provide the JUNG graph

  public DirectedGraph<GraphNode, GraphEdge> getJungGraph() {
    return jungGraph;
  }

  /**
   * Create a JUNG Graph for the underlying ViewModel.
   */
  private DirectedGraph<GraphNode, GraphEdge> createJungGraph(
      GraphModel exposedGraph) {
    DirectedGraph<GraphNode, GraphEdge> result =
        new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    for (GraphNode node : exposedGraph.getNodes()) {
      result.addVertex(node);
    }

    for (GraphEdge edge : exposedGraph.getEdges()) {
      addJungEdge(result, edge);
    }

    return result;
  }

  /**
   * Add an edge to a jung graph.
   * <p>
   * This method exist primarily to limit the scope of the SuppressWarnings.
   *
   * @param graph
   * @param edge
   */
  private static void addJungEdge(
      Graph<GraphNode, GraphEdge> graph,
      GraphEdge edge) {
    graph.addEdge(edge, edge.getHead(), edge.getTail());
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

    saveFile(viewFile, monitor);
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }

    // get the file relatively to the workspace.
    IFile saveFile = calcViewFile(saveas.getResult());

    // TODO(leeca): Consolidate with doSave() flow and saveFile() below and
    // add a progress monitor.
    try {
      saveViewDocument(saveFile, viewInfo);
      viewFile = saveFile;
      setPartName(viewFile.getName());
      setDirtyState(false);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to saveAs to " + saveFile.getFullPath().toString(), errIo);
    }
  }

  /**
   * Save the view document , managing the progress monitor too.
   * @param file where to save the view document
   * @param monitor progress indicator to update
   */
  private void saveFile(IFile file, IProgressMonitor monitor) {
    try {
      monitor.setTaskName("Writing file " + file.getName());
      saveViewDocument(viewFile, viewInfo);
      setDirtyState(false);
    } catch (IOException errIo) {
      logger.warning(errIo.toString());
      monitor.setCanceled(true);
    }
    monitor.done();
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

  /////////////////////////////////////
  // Collapsed presentations

  public GraphModel getExposedGraph() {
    return exposedGraph;
  }

  public void autoCollapse(DirectedRelationFinder finder, Object author) {
    viewInfo.autoCollapse(getViewGraph(), finder, author);
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

  /////////////////////////////////////
  // Update Graph Layouts

  private static Point2D newPoint2D(double xPos, double yPos) {
    return Point2dUtils.newPoint2D(xPos, yPos);
  }

  public Layouts getSelectedLayout() {
    return viewInfo.getSelectedLayout();
  }

  public Map<GraphNode, Point2D> getNodeLocations() {
    return viewInfo.getNodeLocations();
  }

  private void editNodeLocations(Map<GraphNode, Point2D> nodeLocations) {
    renderer.editNodeLocations(nodeLocations);
  }

  /**
   * Provides the OpenGL distance between two points that should be considered
   * equivalent to zero.
   * 
   * <p>Ideally, this should be obtained from the member field for GLPanel/View.
   * A reasonable value might be half the OpenGL distance between two pixels.
   * But that will have to wait.
   */
  private double getZeroThreshold() {
    return ZERO_THRESHOLD;
  }

  private double scaleWithMargin(
      LayoutScaler scaler, GLRegion viewport) {
    return FULLSCALE_MARGIN
        * scaler.getFullViewScale(viewport, getZeroThreshold());
    
  }

  /**
   * For the listed nodes, move their provided location by the given delta
   * values.  The location map can be the node's current location, or a newly
   * computed location for the nodes.  If a listed node has no entry in the
   * location map, the node's location is moved relative to the origin and will
   * be (xDelta, yDelta).
   * 
   * @param moveNodes defines the nodes to move
   * @param locations defines the initial locations of nodes
   * @param xDelta amount of x shift for node move
   * @param yDelta amount of y shift for node move
   */
  private static Map<GraphNode, Point2D> translateNodes(
      Collection<GraphNode> moveNodes,
      Map<GraphNode, Point2D> locations,
      Point2dUtils.Translater translater) {
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    for (GraphNode node : moveNodes) {
      Point2D location = locations.get(node);
      result.put(node, translater.translate(location));
    }
    return result;
  }

  /**
   * Scale and position the nodes so that they fit in the viewport, and they
   * are within the viewport.
   * 
   * @param layoutNodes
   * @param layoutLocations
   * @param viewport
   * @return updated node locations that are within the viewport
   */
  private Map<GraphNode, Point2D> computeInViewportLocations(
      Collection<GraphNode> layoutNodes,
      Map<GraphNode, Point2D> layoutLocations,
      GLRegion viewport) {
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    if (layoutNodes.size() <= 0) {
      return result;
    }

    // If there is only one node, force it to the center of the viewport
    if (layoutNodes.size() <= 1) {
      result.put(layoutNodes.iterator().next(), viewport.getCenter());
      return result;
    }

    LayoutScaler scaler = new LayoutScaler(layoutNodes, layoutLocations);
    double scaleView = scaleWithMargin(scaler, viewport);
    double originX = scaler.getCenterX();
    double originY = scaler.getCenterY();
    Point2dUtils.Translater translater = Point2dUtils.newAdjustTranslater(
        -originX, -originY, scaleView, scaleView);

    return translateNodes(layoutNodes, layoutLocations, translater);
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
      GLRegion viewport) {
    Map<GraphNode, Point2D> result = Maps.newHashMap();
    if (layoutNodes.size() <= 0) {
      return result;
    }

    // If there is only one node, don't change its location
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
    return translateNodes(layoutNodes, locations, translater);
  }

  /**
   * Run a canned layout over the set of nodes.  The computed locations may
   * be nowhere near the OpenGL viewport.
   * 
   * @param layoutDef
   * @param relationfinder
   * @return
   */
  private Map<GraphNode, Point2D> computeLayoutLocations(
    Layouts layoutDef, DirectedRelationFinder relationfinder) {
  AbstractLayout<GraphNode, GraphEdge> newLayout =
      layoutDef.getLayout(this, relationfinder);

  // Run the layout until it stabilizes.
  if (newLayout instanceof IterativeContext) {
    IterativeContext it = (IterativeContext) newLayout;
    int maxSteps = 1000;
    while (maxSteps > 0 && !it.done()) {
      it.step();
      maxSteps--;
    }
  }

  // Convert the layout positions to standard node positions.
  Map<GraphNode, Point2D> result = Maps.newHashMap();
  for (GraphNode n : getViewGraph().getNodes()) {

    // minus Y, since in openGL, y coordinates are inverted
    result.put(n, newPoint2D(newLayout.getX(n), -newLayout.getY(n)));
  }
  return result;
}

  /////////////////////////////////////
  // Update node positions in the View Document

  /**
   * Scale the coordinates for all exposed nodes as indicated by the paramters.
   * 
   * @param scaleX scale factor for X coordinates
   * @param scaleY scale factor for Y coordinates
   */
  public void layoutScale(double scaleX, double scaleY) {
    Point2dUtils.Translater translater =
        Point2dUtils.newScaleTranslater(scaleX, scaleY);
    Map<GraphNode, Point2D> changes = translateNodes(
        getExposedGraph().getNodes(), getNodeLocations(),
        translater);

    viewInfo.editNodeLocations(changes, null);
  }

  private void layoutBestFit(
      Collection<GraphNode> layoutNodes, Map<GraphNode, Point2D> locations) {
    GLRegion viewport = renderer.getOGLViewport();
    Map<GraphNode, Point2D> changes =
        computeFullViewScale(layoutNodes, locations, viewport);
    viewInfo.editNodeLocations(changes, null);
  }

  /**
   * Scale the exposed nodes so they would fit in the viewport, if the viewport
   * was centered over the nodes.  This has been the historical behavior of the
   * {@code FactorPlugin}.
   */
  public void layoutBestFit() {
    layoutBestFit(getExposedGraph().getNodes(), getNodeLocations());
  }

  /**
   * Apply the given layout with the given {@link DirectedRelationFinder} to
   * build a tree if the layout need one, to the graph.
   *
   * @param newLayout the new Layout to apply
   * @param relationfinder {@link DirectedRelationFinder} helping to build a
   *        tree if necessary
   */
  public void applyLayout(
      Layouts newLayout, DirectedRelationFinder relationfinder) {

    // Run the layout process to compute new locations.
    Map<GraphNode, Point2D> layoutLocations =
        computeLayoutLocations(newLayout, relationfinder);

    // Adjust those locations to be at the origin 
    // and scaled to fill the current viewport.
    Collection<GraphNode> layoutNodes = getExposedGraph().getNodes();
    GLRegion viewport = renderer.getOGLViewport().newOriginRegion();
    Map<GraphNode, Point2D> changes =
        computeInViewportLocations(layoutNodes, layoutLocations, viewport);

    // Change the node locations.
    viewInfo.editNodeLocations(changes, null);
  }

  public void applyLayout(Layouts layout) {
    applyLayout(layout, viewInfo.getLayoutFinder());
  }

  public void clusterize(Layouts layout, DirectedRelationFinder finder) {
    applyLayout(layout, finder);
  }

  // TODO(leeca):  cleanup/consolidate applyLayout and clusterize
  // The should be different entry points for the same algorithm.
  // But with the broken cluster() below, making clusterize() == applyLayout(),
  // at least something useful happens.
  public void x_clusterize(Layouts layout, DirectedRelationFinder finder) {
    cluster(layout, finder);

    // TODO(leeca): Is this necessary if setNodeLocations
    // fires an update event?
    markDirty();
  }

  /**
   * Call {@link #cluster(Layouts, DirectedRelationFinder)} with a
   * default {@link DirectedRelationFinder}.
   *
   * @param layout
   */
  public void cluster(Layouts layout) {
    cluster(layout, viewInfo.getLayoutFinder());
  }

  /**
   * Try to clusterize the selected nodes (if more than two nodes are selected),
   * or, if no nodes are selected, apply the layout to the entire graph.
   *
   * @param layout
   * @param relationFinder a relation finder if needed by the layout.
   */
  public void cluster(Layouts layout, DirectedRelationFinder relationFinder) {
    Collection<GraphNode> picked = getSelectedNodes();
    if (0 == picked.size()) {
      applyLayout(layout, relationFinder);
      return;
    }
    if (2 < picked.size()) {
      return;
    }
    int clusterSize = picked.size();

    // Find the weighted center of all picked points to place the new node
    // that represents the collapsed nodes.  Also compute the bounding box.
    Map<GraphNode, Point2D> locations = viewInfo.getNodeLocations();
    double minX = Double.MAX_VALUE;
    double maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE;
    double maxY = Double.MIN_VALUE;
    double spanX = 0.0;
    double spanY = 0.0;
    for (GraphNode node : picked) {
      Point2D p = locations.get(node);
      spanX += p.getX();
      spanY += p.getY();
      minX = Math.min(minX, p.getX());
      maxX = Math.max(maxX, p.getX());
      minY = Math.min(minY, p.getY());
      maxY = Math.max(maxY, p.getY());
    }
    Point2D center = 
        new Point2D.Double(spanX / clusterSize, spanY / clusterSize);

    Graph<GraphNode, GraphEdge> subGraph =
        new DirectedSparseMultigraph<GraphNode, GraphEdge>();

    Set<GraphEdge> edges = Sets.newHashSet();
    for (GraphNode node : picked) {
      subGraph.addVertex(node);
      edges.addAll(getJungGraph().getIncidentEdges(node));
    }
    for (GraphEdge edge : edges) {
      GraphNode head = edge.getHead();
      GraphNode tail = edge.getTail();
      boolean containsHead = false;
      boolean containsTail = false;
      for (GraphNode node : picked) {
        if (node == head) {
          containsHead = true;
        }
        if (node == tail) {
          containsTail = true;
        }
      }
      subGraph.addEdge(edge, head, tail);
    }

    // FIXME: reimplement
/*
    // try to not move the not selected nodes
    for (GraphNode node : viewModel.getGraph().getNodes()) {
      if (picked.contains(node)) {
        staticLayout.lock(node, false);
      } else {
        staticLayout.lock(node, true);
      }
    }

    // add +0.5 to round to the upper bound.
    Dimension size = new Dimension(
          (int) (maxx - minx + 0.5), (int) (maxy - miny + 0.5));

    AggregateLayout<GraphNode, GraphEdge> aggregate =
       new AggregateLayout<GraphNode, GraphEdge>(staticLayout);

    Layout<GraphNode, GraphEdge> subLayout;
    subLayout = layout.getLayout(this, relationFinder);
    subLayout.setInitializer(vv.getGraphLayout());
    subLayout.setSize(size);
    aggregate.put(subLayout, center);
    vv.setGraphLayout(aggregate);
    */
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
    viewInfo.editSelectedNodes(EMPTY_NODE_LIST, extendNodes, author);
  }

  public void reduceSelection(
      Collection<GraphNode> reduceNodes, Object author) {
    viewInfo.editSelectedNodes(reduceNodes, EMPTY_NODE_LIST, author);
  }

  public void moveSelectionDelta(
      double deltaX, double deltaY, Object author) {
    Point2dUtils.Translater translater =
        Point2dUtils.newDeltaTranslater(deltaX, deltaY);

    Map<GraphNode, Point2D> changes = translateNodes(
        getSelectedNodes(), getNodeLocations(), translater);
    viewInfo.editNodeLocations(changes, author);
  }

  /////////////////////////////////////
  // Notifications from ViewEditor toward Tools and other ViewEditor listeners

  private abstract static class SimpleDispatcher
      implements ListenerManager.Dispatcher<SelectionChangeListener> {
    public void captureException(RuntimeException errAny) {
      logger.warning(errAny.toString());
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
    updateSelectedNodes(EMPTY_NODE_LIST, selection, null);
  }

  /////////////////////////////////////
  // Specialized features

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
  // Callbacks from the rendering engine
  // Most events are converted to ViewDocument method calls, and those
  // changes lead to change events for the tools and the renderer.

  private class RendererChangeReceiver
      implements RendererChangeListener {

    @Override
    public void locationsChanged(Map<GraphNode, Point2D> changes) {
      viewInfo.editNodeLocations(changes, renderer);
    }

    @Override
    public void selectionMoved(double x, double y) {
      moveSelectionDelta(x, y, renderer);
    }

    @Override
    public void selectionChanged(Collection<GraphNode> pickedNodes) {
      selectNodes(pickedNodes);
    }

    @Override
    public void selectionExtended(Collection<GraphNode> extendNodes) {
      extendSelection(extendNodes, null);
    }

    @Override
    public void selectionReduced(Collection<GraphNode> reduceNodes) {
      reduceSelection(reduceNodes, null);
    }
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
      if (null != renderer) {
        renderer.updateEdgeProperty(edge, newProperty);
      }
      markDirty();
    }

    @Override
    public void nodePropertyChanged(
        GraphNode node, NodeDisplayProperty newProperty) {
      if (null != renderer) {
        renderer.updateNodeProperty(node, newProperty);
      }
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
      editNodeLocations(newLocations);
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
   * This is an asynchronous active, as the new editor will execute separately
   * from the other workbench windows.
   * 
   * @param newInfo graph to display
   * @param skipLayout {@code true} if layout should be skipped on initial
   *     rendering.
   */
  public static void startViewEditor(
      ViewDocument newInfo, boolean skipLayout) {
    final ViewEditorInput input = new ViewEditorInput(newInfo, skipLayout);
    getWorkbenchDisplay().asyncExec(new Runnable() {
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
