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

import com.google.devtools.depan.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.eclipse.preferences.LabelPreferencesIds;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.LabelPosition;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.trees.NodeTreeProvider;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.tools.RelationCount;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeColorPlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeShapePlugin;
import com.google.devtools.depan.eclipse.visualization.plugins.impl.NodeSizePlugin;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view.NodeDisplayProperty;
import com.google.devtools.depan.view.PersistAsText;
import com.google.devtools.depan.view.SimpleViewModelListener;
import com.google.devtools.depan.view.ViewModel;
import com.google.devtools.depan.view.ViewModelListener;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewEditor extends MultiPageEditorPart
    implements IPreferenceChangeListener,
    NodeTreeProvider<NodeDisplayProperty> {

  public static final String ID =
      "com.google.devtools.depan.eclipse.editors.ViewEditor";

  private View view = null;

  // Persistence information - where to write this view
  private IFile graphFile = null;
  private IFile parentFile = null;

  // Initialization data - for use only during editor activation
  private ViewModel initViewModel = null;
  private Layouts initViewLayout = null;

  // Transient GraphView data
  // If this persists, it should probably move to ViewModel type.
  private HierarchyCache<NodeDisplayProperty> hierarchies;

  /**
   * Dirty state.
   */
  private boolean isDirty = true;

  private ViewModelListener viewModelListener;

  /////////////////////////////////////
  // Tool/view data

  private RelationCount.Settings relationCountData =
    new RelationCount.Settings();

  /////////////////////////////////////
  // Basic Getters and Setters

  public ViewModel getViewModel() {
    return getView().getViewModel();
  }

  public View getView() {
    return view;
  }

  public IFile getParentFile() {
    return parentFile;
  }

  public void setDirtyState(boolean dirty) {
    this.isDirty = dirty;
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  @Override
  public boolean isDirty() {
    return isDirty;
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
    createPage0();
    createPage1();

    // this.view is not configured until createPage0(), so this is almost
    // the earliest that the hierarchy cache can be set up.
    hierarchies =new HierarchyCache<NodeDisplayProperty>(
        this, getViewModel().getGraph());
  }

  private void createPage0() {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    GridLayout pageLayout = new GridLayout();
    pageLayout.numColumns = 1;
    parent.setLayout(pageLayout);

    // bottom composite containing main graph
    view = createView(parent);

    view.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    setPreferences();

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

  private void createPage1() {
    Composite parent = new Composite(getContainer(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.marginTop = 9;
    parent.setLayout(layout);

    GridData fillGrid = new GridData(SWT.FILL, SWT.FILL, true, false);

    Label nameLabel = new Label(parent, SWT.NONE);
    final Text name = new Text(parent, SWT.BORDER | SWT.SINGLE);

    nameLabel.setText("Name");
    name.setText(getViewModel().getName());

    name.setLayoutData(fillGrid);

    name.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        if (getViewModel() != null) {
          String viewName = name.getText();
          getViewModel().setName(viewName);
          ViewEditor.this.setPartName(viewName);
          ViewEditor.this.setDirtyState(true);
        }
      }
    });
    int index = addPage(parent);
    setPageText(index, "Properties");
  }

  /**
   * Create a View from the initialization parameters.
   *
   * @param parent The parent that holds this <code>View</code>.
   * @return The new View object created using initialization parameters.
   */
  private View createView(Composite parent) {
    return new View(initViewModel, initViewLayout, parent, SWT.BORDER);
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException  {
    setSite(site);
    setInput(input);

    if (input instanceof ViewEditorInput) {
      ViewEditorInput editorInput = (ViewEditorInput) input;
      graphFile = null; // not yet saved
      parentFile =  editorInput.getParentFile();
      initViewModel = editorInput.getView();
      initViewLayout = editorInput.getLayout();
      setDirtyState(true);
    } else if (input instanceof IFileEditorInput) {
      graphFile = ((IFileEditorInput) input).getFile();
      PersistentView persist = PersistentView.load(graphFile.getRawLocationURI());
      parentFile = persist.getParentFile();
      initViewModel = persist.getViewModel();
      initViewLayout = null; // use static layout
      setDirtyState(false);
    } else {
      throw new PartInitException(
          "Input for editor is not suitable for the ViewEditor");
    }
    this.setPartName(initViewModel.getName());

    // listen the changes in the configuration
    new InstanceScope().getNode(Resources.PLUGIN_ID)
        .addPreferenceChangeListener(this);

    // Listen to changes in the underlying ViewModel
    viewModelListener =
        new SimpleViewModelListener() {

          @Override
          public void simpleChange() {
            setDirtyState(true);
          }
    };
    initViewModel.registerListener(viewModelListener);
  }

  /**
   * Release the resource held by the ViewEditor:
   * - ViewModelListener
   * - Underlying view.
   */
  @Override
  public void dispose() {
    if (null != hierarchies) {
      hierarchies = null;
    }

    if (null != viewModelListener) {
      getViewModel().unRegisterListener(viewModelListener);
      viewModelListener = null;
    }

    if (null != getView()) {
      getView().dispose();
      view = null;
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
      getView().getRenderingPipe().getNodeLabel().setLabelPosition(
          LabelPosition.valueOf(val));
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
    NodeSizePlugin<GraphEdge> nodeSize =
      getView().getRenderingPipe().getNodeSize();
    NodeColorPlugin<GraphEdge> nodeColor =
      getView().getRenderingPipe().getNodeColors();
    NodeShapePlugin<GraphEdge> nodeShape =
      getView().getRenderingPipe().getNodeShape();

    // read enable/disable preferences
    boolean colorEnabled = node.getBoolean(
        NodePreferencesIds.NODE_COLOR_ON, true);
    boolean shapeEnabled = node.getBoolean(
        NodePreferencesIds.NODE_SHAPE_ON, true);
    boolean sizeEnabled = node.getBoolean(
        NodePreferencesIds.NODE_SIZE_ON, false);
    boolean ratioEnabled = node.getBoolean(
        NodePreferencesIds.NODE_RATIO_ON, false);

    // set enable/disable preferences
    nodeColor.setColor(colorEnabled);
    nodeSize.setRatio(ratioEnabled);
    nodeSize.setResize(sizeEnabled);
    nodeShape.setShapes(shapeEnabled);

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
    getView().getGLPanel().setColors(back, front);
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
  // Persistence Support

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    // Of there are any file problems, do this as a Save As ..
    if ((null == parentFile) || (null == graphFile)) {
      doSaveAs();
    }

    monitor.setTaskName("Writing file...");
    PersistentView persist =
        new PersistentView(getViewModel(), parentFile.getRawLocationURI());

    persist.setLocations();
    persist.save(graphFile.getRawLocationURI());
    setDirtyState(false);
    monitor.done();
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }
    // get the file relatively to the workspace.
    IPath result = saveas.getResult();
    // get a real IFile representing a file in a project.
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(result);

    URI saveUri = file.getLocationURI();

    // If text output is requested, use it.
    if (saveUri.toString().endsWith(".txt")) {
      PersistAsText textSaver =
          new PersistAsText(getViewModel(), parentFile.getRawLocationURI());
      textSaver.save(saveUri);
      setDirtyState(false);
      return;
    }

    // Otherwise, save using the binary format
    saveUri = getDpanvUri(saveUri);
    PersistentView binSaver =
        new PersistentView(getViewModel(), parentFile.getRawLocationURI());
    binSaver.setLocations();
    binSaver.save(saveUri);
    setDirtyState(false);
    graphFile = file;
  }

  /**
   * @param saveUri
   */
  private URI getDpanvUri(URI saveUri) {
    String uriText = saveUri.toString();

    // check if the name ends in .dpanv. if no, add it.
    if (uriText.endsWith(".dpanv")) {
      return saveUri;
    }

    // Add the missing extension
    try {
      return new URI(uriText + ".dpanv");
    } catch (URISyntaxException errUri) {
      errUri.printStackTrace();
      throw new RuntimeException(
          "Can't add '.dpanv' to '" + uriText + "'", errUri);
    }
  }

  /////////////////////////////////////
  // Specialized features

  public void clusterize(Layouts layout, DirectedRelationFinder finder) {
    getView().cluster(layout, finder);
    setDirtyState(true);
  }

  public GraphData<NodeDisplayProperty> getHierarchy(DirectedRelationFinder relFinder) {
    return hierarchies.getHierarchy(relFinder);
  }

  @Override
  public NodeDisplayProperty getObject(GraphNode node) {
    return getViewModel().getNodeDisplayProperty(node);
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

  /**
   * Activate a new ViewEditor.
   * This is an asynchronous active, as the new editor will execute separately
   * from the other workbench windows.
   * 
   * @param config ViewEditor configuration options.
   */
  public static void startViewEditor(final ViewEditorInput config) {
    getWorkbenchDisplay().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          page.openEditor(config, ViewEditor.ID);
        } catch (PartInitException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
