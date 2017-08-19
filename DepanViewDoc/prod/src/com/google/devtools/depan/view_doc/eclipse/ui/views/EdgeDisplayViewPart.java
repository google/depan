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

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.EdgeDisplaySaveLoadControl;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.EdgeDisplayTableControl;
import com.google.devtools.depan.view_doc.model.EdgeDisplayDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Tool for setting display properties for individual edges.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class EdgeDisplayViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Edge Properties";

  /////////////////////////////////////
  // UX Elements

  private EdgeDisplayTableControl propEditor;

  /////////////////////////////////////
  // EdgeDisplayProperty integration

  private EdgeDisplayRepository propRepo;

  private class ControlSaveLoadControl extends EdgeDisplaySaveLoadControl {
    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected IProject getProject() {
      return EdgeDisplayViewPart.this.getProject();
    }

    @Override
    protected EdgeDisplayDocument buildSaveResource() {
      return EdgeDisplayViewPart.this.buildSaveResource();
    }

    @Override
    protected void installLoadResource(
        PropertyDocumentReference<EdgeDisplayDocument> ref) {
      if (null != ref) {
        EdgeDisplayDocument doc = ref.getDocument();
        EdgeDisplayViewPart.this.installLoadResource(doc);
      }
    }
  }

  private static class PartEdgeDisplayRepo
      implements EdgeDisplayRepository {

    private final ViewEditor editor;

    private PartPrefsListener prefsListener;

    public PartEdgeDisplayRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public EdgeDisplayProperty getDisplayProperty(GraphEdge edge) {
      return editor.getEdgeProperty(edge);
    }

    @Override
    public void setDisplayProperty(GraphEdge edge, EdgeDisplayProperty props) {
      editor.setEdgeProperty(edge, props);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
      prefsListener = new PartPrefsListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
      // TODO: if multiple ChangeListener,
      // add Map<ChangeListener, EdgeDisplayListener>
      editor.removeViewPrefsListener(prefsListener);
    }
  }

  private static class PartPrefsListener extends ViewPrefsListener.Simple {

    private EdgeDisplayRepository.ChangeListener listener;

    public PartPrefsListener(EdgeDisplayRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void edgePropertyChanged(
        GraphEdge edge, EdgeDisplayProperty newProperty) {
      listener.edgeDisplayChanged(edge, newProperty);
    }
  }

  /////////////////////////////////////
  // Public methods

  @Override
  public Image getTitleImage() {
    return ViewDocResources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected void createGui(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    propEditor = new EdgeDisplayTableControl(result);
    propEditor.setLayoutData(Widgets.buildGrabFillData());

    EdgeDisplaySaveLoadControl saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();

    propRepo = new PartEdgeDisplayRepo(editor);
    propEditor.setEdgeDisplayRepository(propRepo);

    Collection<GraphEdge> edges = editor.getExposedGraph().getEdges();
    propEditor.setInput(edges);
    propEditor.update();
  }

  @Override
  protected void releaseResources() {
    propEditor.removeEdgeDisplayRepository(propRepo);
    propRepo = null;
  }

  private IProject getProject() {
    return getEditor().getResourceProject();
  }

  protected EdgeDisplayDocument buildSaveResource() {
    Collection<GraphEdge> relations = propEditor.getSelection();
    Map<GraphEdge, EdgeDisplayProperty> props =
        buildEdgeDisplayProperties(relations);

    ViewEditor ed = getEditor();
    String name = ed.getBaseName();
    DependencyModel model = ed.getDependencyModel();
    return new EdgeDisplayDocument(name, model, props);
  }

  protected void installLoadResource(EdgeDisplayDocument doc) {
    if (null == doc) {
      return;
    }

    Map<GraphEdge, EdgeDisplayProperty> info = doc.getInfo();
    for (Entry<GraphEdge, EdgeDisplayProperty> entry : info.entrySet()) {
      propRepo.setDisplayProperty(entry.getKey(), entry.getValue());
    }
  }

  private Map<GraphEdge, EdgeDisplayProperty> buildEdgeDisplayProperties(
      Collection<GraphEdge> edges) {

    Map<GraphEdge, EdgeDisplayProperty> result = Maps.newHashMap();
    for (GraphEdge edge : edges) {
      result.put(edge, propRepo.getDisplayProperty(edge));
    }
    return result;
  }
}
