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

import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.EdgeDisplayTableControl;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

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

  private static class ToolEdgeDisplayRepo
      implements EdgeDisplayRepository {

    private final ViewEditor editor;

    private EdgeDisplayListener prefsListener;

    public ToolEdgeDisplayRepo(ViewEditor editor) {
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
      prefsListener = new EdgeDisplayListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
      // TODO: if multiple ChangeListener,
      // add Map<ChangeListener, EdgeDisplayListener>
      editor.removeViewPrefsListener(prefsListener);
    }
  }

  private static class EdgeDisplayListener extends ViewPrefsListener.Simple {

    private EdgeDisplayRepository.ChangeListener listener;

    public EdgeDisplayListener(EdgeDisplayRepository.ChangeListener listener) {
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

    propRepo = new ToolEdgeDisplayRepo(editor);
    propEditor.setEdgeDisplayRepository(propRepo);

    // TODO: Should come from editor
    Collection<GraphEdge> edges = editor.getExposedGraph().getEdges();
    propEditor.setInput(edges);
    propEditor.update();
  }

  @Override
  protected void releaseResources() {
    propEditor.removeEdgeDisplayRepository(propRepo);
    propRepo = null;
  }
}
