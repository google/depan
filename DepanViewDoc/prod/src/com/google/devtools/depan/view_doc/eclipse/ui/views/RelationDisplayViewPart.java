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

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.RelationDisplaySaveLoadControl;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.RelationDisplayTableControl;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.model.RelationDisplayRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;
import java.util.Map;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationDisplayViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Relations Properties";

  /////////////////////////////////////
  // UX Elements

  /**
   * The {@code RelationDisplayTableControl} that controls the UX.
   */
  private RelationDisplayTableControl propEditor;

  /////////////////////////////////////
  // RelationSet integration

  private RelationDisplayRepository propRepo;

  private class ControlSaveLoadControl extends RelationDisplaySaveLoadControl {
    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected IProject getProject() {
      return RelationDisplayViewPart.this.getProject();
    }

    @Override
    protected RelationDisplayDocument buildSaveResource() {
      return RelationDisplayViewPart.this.buildSaveResource();
    }

    @Override
    protected void installLoadResource(
        PropertyDocumentReference<RelationDisplayDocument> ref) {
      if (null != ref) {
        RelationDisplayDocument doc = ref.getDocument();
        RelationDisplayViewPart.this.installLoadResource(doc);
      }
    }
  }

  private static class PartRelationDisplayRepo
      implements RelationDisplayRepository {

    private final ViewEditor editor;

    private PartPrefsListener prefsListener;

    private Map<Relation, EdgeDisplayProperty> tempRows;

    public PartRelationDisplayRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public EdgeDisplayProperty getDisplayProperty(Relation relation) {
      EdgeDisplayProperty result = editor.getRelationProperty(relation);
      if (null != result) {
        return result;
      }
      return getTempRow(relation);
    }

    private EdgeDisplayProperty getTempRow(Relation relation) {
      if (null == tempRows) {
        tempRows = Maps.newHashMap();
      }
      EdgeDisplayProperty result = tempRows.get(relation);
      if (null != result) {
        return result;
      }
      result = new EdgeDisplayProperty();
      tempRows.put(relation, result);
      return result;
    }

    @Override
    public void setDisplayProperty(Relation relation, EdgeDisplayProperty props) {
      editor.setRelationProperty(relation, props);
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

    private RelationDisplayRepository.ChangeListener listener;

    public PartPrefsListener(RelationDisplayRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void relationPropertyChanged(Relation relation,
        EdgeDisplayProperty newProperty) {
      listener.edgeDisplayChanged(relation, newProperty);
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

    propEditor = new RelationDisplayTableControl(result);
    propEditor.setLayoutData(Widgets.buildGrabFillData());

    RelationDisplaySaveLoadControl saves = new ControlSaveLoadControl(result);
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

    propRepo = new PartRelationDisplayRepo(editor);
    propEditor.setEdgeDisplayRepository(propRepo);

    propEditor.setInput(editor.getDisplayRelations());
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

  private RelationDisplayDocument buildSaveResource() {
    Collection<Relation> relations = propEditor.getSelection();
    Map<Relation, EdgeDisplayProperty> props =
        buildEdgeDisplayProperties(relations);

    ViewEditor ed = getEditor();
    String name = ed.getBaseName();
    DependencyModel model = ed.getDependencyModel();
    return new RelationDisplayDocument(name, model, props);
  }

  private void installLoadResource(RelationDisplayDocument doc) {
    if (null == doc) {
      return;
    }

    ViewEditor ed = getEditor();
    Map<Relation, EdgeDisplayProperty> info = doc.getInfo();
    for (Map.Entry<Relation, EdgeDisplayProperty> entry : info.entrySet()) {
      ed.setRelationProperty(entry.getKey(), entry.getValue());
    }
  }

  private Map<Relation, EdgeDisplayProperty>
      buildEdgeDisplayProperties(Collection<Relation> relations) {
    Map<Relation, EdgeDisplayProperty> result =
        Maps.newHashMapWithExpectedSize(relations.size());

    for (Relation relation : relations) {
      EdgeDisplayProperty prop = propRepo.getDisplayProperty(relation);
      if (null == prop) {
        continue;
      }
      result.put(relation, prop);
    }
    return result;
  }
}
