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

package com.google.devtools.depan.view_doc.eclipse.ui.views;

import com.google.devtools.depan.analysis_doc.model.FeatureMatcher;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.GraphNodeViewer;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeListViewProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.context.MapContext;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.ContextualFilterSaveLoadConfig;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterTableEditorControl;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextKey.Base;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.nodes.filters.model.FilterContext;
import com.google.devtools.depan.nodes.filters.sequence.SteppingFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Sasher;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.FromViewDocContributor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.FromViewDocWizard;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.FromViewDocListControl;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Define node filters for the current {@link ViewEditor}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodeFilterViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Node Filter";

  /////////////////////////////////////
  // Internal state

  private Collection<GraphNode> resultNodes;

  /////////////////////////////////////
  // UX Elements

  private FromViewDocListControl fromViewDoc;

  private GraphNodeViewer sources;

  private GraphNodeViewer results;

  private ViewPrefsListener listener;

  private FilterTableEditorControl filterControl;

  /////////////////////////////////////
  //

  @Override
  public Image getTitleImage() {
    return ViewDocResources.IMAGE_SELECTIONEDITOR;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected void createGui(Composite parent) {
    Sasher sasher = new Sasher(parent, SWT.NONE);

    Composite filters = setupFilterEditor(sasher);
    filters.setLayoutData(Widgets.buildHorzFillData());

    Composite nodesPane = setupNodesPane(sasher);
    nodesPane.setLayoutData(Widgets.buildGrabFillData());

    sasher.init(filters, nodesPane, SWT.HORIZONTAL, 35);  // 35% for filters
    sasher.setLimit(200); // 220 pixels
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  private Composite setupFilterEditor(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Filters", 1);

    Composite xfers = setupSaveButtons(result);
    xfers.setLayoutData(Widgets.buildHorzFillData());

    filterControl = new FilterTableEditorControl(result);
    filterControl.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  private Composite setupNodesPane(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = Widgets.buildContainerLayout(2);
    layout.makeColumnsEqualWidth = true;
    result.setLayout(layout);

    Composite sources = setupSourceNodes(result);
    sources.setLayoutData(Widgets.buildGrabFillData());

    Composite results = setupResultNodes(result);
    results.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button saveButton = Widgets.buildGridPushButton(
        result, "Save as NodeFilter...");
    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveFilterTable();
      }
    });

    Button loadButton = Widgets.buildGridPushButton(
        result, "Load from NodeFilter...");
    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        loadFilterTable();
      }
    });

    return result;
  }

  private Composite setupSourceNodes(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Source nodes", 1);

    sources = new GraphNodeViewer(result);
    sources.setLayoutData(Widgets.buildGrabFillData());
    return result;
  }

  private Composite setupResultNodes(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Result nodes", 1);

    results = new ControlGraphNodeViewer(result);
    results.setLayoutData(Widgets.buildGrabFillData());

    Composite newView = setupResolution(result);
    newView.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  private Composite setupResolution(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = Widgets.buildContainerLayout(2);
    layout.horizontalSpacing = 10;
    result.setLayout(layout);

    Button create = new Button(result, SWT.PUSH);
    create.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false));
    create.setText("Select nodes");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        selectResultNodes();
      }

    });

    Composite newView = setupNewView(result);
    newView.setLayoutData(Widgets.buildTrailFillData());

    return result;
  }

  private class ControlGraphNodeViewer extends GraphNodeViewer {

    public ControlGraphNodeViewer(Composite parent) {
      super(parent);
    }

    @Override
    protected Composite createCommands(Composite parent) {
      Composite result = Widgets.buildGridContainer(parent, 1);
      Button compute = Widgets.buildCompactPushButton(
          result, "Compute Results");

      compute.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
          updateResults();
        }
      });

      return result;
    }
  }

  private Collection<GraphNode> computeResults(
      SteppingFilter filter, Collection<GraphNode> source) {

    Collection<ContextKey> ctxtKeys = filter.getContextKeys();
    filter.receiveContext(buildComputeContext(ctxtKeys, filter));
    return filter.computeNodes(source);
  }

  private void updateResults() {
    SteppingFilter filter = filterControl.buildFilter();
    Collection<GraphNode> source = getEditor().getSelectedNodes();
    this.resultNodes = computeResults(filter, source);
    refreshResults(resultNodes);
  }

  private void refreshResults(Collection<GraphNode> nodes) {
    results.setNvProvider(buildProvider(nodes));
    results.refresh();
  }

  private NodeListViewProvider<GraphNode> buildProvider(
      Collection<GraphNode> nodes) {
    if (null == nodes) {
      return new NodeListViewProvider<GraphNode>(
          "No result nodes", Collections.<GraphNode>emptyList());
    }

    String label = MessageFormat.format(
        "{0} result nodes", nodes.size());

    NodeListViewProvider<GraphNode> provider =
        new NodeListViewProvider<GraphNode>(label, nodes);
    provider.setProvider(NodeTreeProviders.GRAPH_NODE_PROVIDER);
    return provider;
  }

  private FilterContext buildComputeContext(
      Collection<ContextKey> ctxtKeys,
      ContextualFilter filter) {
    Map<ContextKey, Object> result = Maps.newHashMap();
    Set<ContextKey> checkKeys = Sets.newHashSet(ctxtKeys);

    Map<ContextKey, Object> ofEditor = buildEditorContext(ctxtKeys);
    for (Entry<ContextKey, Object> entry : ofEditor.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
      checkKeys.remove(entry.getKey());
    }
    if (!checkKeys.isEmpty()) {
      ViewDocLogger.LOG.warn("Filter {} has unresolved context keys {}",
          filter.getName(), Joiner.on(", ").join(checkKeys));
    }
    return new MapContext(result);
  }

  private Map<ContextKey, Object> buildEditorContext(
      Collection<ContextKey> keys) {

    Map<ContextKey, Object> result = Maps.newHashMap();
    for (ContextKey key : keys) {
      if (Base.UNIVERSE == key) {
        result.put(key, getEditor().getParentGraph());
      } else if (Base.VIEWDOC == key) {
        result.put(key, getEditor());
      }
    }
    return result;
  }

  private void selectResultNodes() {
    if (null == resultNodes) {
      getEditor().selectNodes(Collections.<GraphNode>emptyList());
      return;
    }

    // TODO: Fill in with results from filter execution.
    getEditor().selectNodes(resultNodes, this);
  }

  private Composite setupNewView(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button create = Widgets.buildCompactPushButton(result, "Create");
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createViewEditor();
      }
    });

    fromViewDoc = new FromViewDocListControl(result);
    fromViewDoc.setLayoutData(Widgets.buildHorzFillData());
    return result;
  }

  /////////////////////////////////////
  //

  /**
   * Create a new Graph Visualization editor from the selected tree elements
   * and other {@code GraphEditor} settings.
   */
  private void createViewEditor() {
    ViewEditor editor = getEditor();
    if (null == editor) {
      // control active, but no bound editor ..
      return;
    }

    // Refresh displayed nodes.
    updateResults();
    if ((null == resultNodes) || resultNodes.isEmpty()) {
      ViewDocLogger.LOG.info("empty nodes");
      return;
    }

    try {
      runCreateWizard();
    } catch (IllegalArgumentException ex) {
      // bad layout. don't do anything for the layout, but still finish the
      // creation of the view.
      ViewDocLogger.LOG.warn("Bad layout selected.");
    } catch (Exception errView) {
      ViewDocLogger.LOG.error("Unable to create view", errView);
    }
  }

  private IWizard prepareWizard(
      Collection<GraphNode> nodes, String name) {
    ViewEditor editor = getEditor();

    Object choice = fromViewDoc.getChoice();
    if (null == choice) {
      return null;
    }

    if (choice instanceof FromViewDocContributor) {
      FromViewDocWizard wizard = ((FromViewDocContributor) choice).newWizard();
      wizard.init(editor, nodes, name);
      return wizard;
    }

    if (choice instanceof FromGraphDocContributor) {
      FromGraphDocWizard wizard = ((FromGraphDocContributor) choice).newWizard();
      wizard.init(
          (IFile) editor.getParentGraphPath(),
          editor.getParentGraphDoc(),
          editor.getGraphResources(),
          nodes, name);
      return wizard;
    }

    return null;
  }

  private void runCreateWizard() {
    SteppingFilter filter = filterControl.buildFilter();
    IWizard wizard = prepareWizard(resultNodes, filter.getName());
    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  /////////////////////////////////////
  // Persistence integration

  private void saveFilterTable() {
    // Run SaveResource dialog using current editor content.

    ViewEditor editor = getEditor();
    Shell shell = editor.getSite().getShell();
    IProject project = getEditor().getResourceProject();
    DependencyModel model = editor.getDependencyModel();

    ContextualFilter saveFilter = filterControl.buildFilter();
    FeatureMatcher matcher = new FeatureMatcher(model);
    ContextualFilterDocument result =
        new ContextualFilterDocument(matcher, saveFilter);

    ContextualFilterSaveLoadConfig.CONFIG.saveResource(result, shell, project);
  }

  private void loadFilterTable() {
    ViewEditor editor = getEditor();
    Shell shell = editor.getSite().getShell();
    IProject project = getEditor().getResourceProject();

    PropertyDocumentReference<ContextualFilterDocument> ref =
        ContextualFilterSaveLoadConfig.CONFIG.loadResource(shell, project);
    if (null == ref) {
      return;
    }

    ContextualFilter filter = ref.getDocument().getInfo();
    DependencyModel model = editor.getDependencyModel();
    if (filter instanceof SteppingFilter) {
      filterControl.setInput((SteppingFilter) filter, model, project);
      return;
    } 

    String name = MessageFormat.format("Wrapped {0}", filter.getName());
    String summary = MessageFormat.format("From {0}", filter.getSummary());
    SteppingFilter synth = new SteppingFilter(name, summary);
    synth.setSteps(Collections.singletonList(filter));
    filterControl.setInput(synth, model, project);
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();
    filterControl.setInput(
        editor.getActiveNodeFilter(),
        editor.getDependencyModel(),
        editor.getResourceProject());

    refreshSources();
    refreshResults(Collections.<GraphNode>emptyList());

    listener = new PartViewPrefsListener() ;
    editor.addViewPrefsListener(listener);
  }

  @Override
  protected void releaseResources() {
    if (null != listener ) {
      getEditor().removeViewPrefsListener(listener);
      listener = null;
    }
  }

  private class PartViewPrefsListener
      extends ViewPrefsListener.Simple {

    @Override
    public void selectionChanged(Collection<GraphNode> previous,
        Collection<GraphNode> current, Object author) {
      if (author != this) {
        refreshSources();
      }
    }
  }

  private void refreshSources() {

    ViewEditor editor = getEditor();

    Collection<GraphNode> nodes = editor.getSelectedNodes();
    String name = editor.getPartName();
    String label = MessageFormat.format(
        "{0} [{1} selected nodes]", name, nodes.size());

    NodeListViewProvider<GraphNode> provider =
        new NodeListViewProvider<GraphNode>(label, nodes);
    provider.setProvider(NodeTreeProviders.GRAPH_NODE_PROVIDER);

    sources.setNvProvider(provider);
    sources.refresh();
  }
}
