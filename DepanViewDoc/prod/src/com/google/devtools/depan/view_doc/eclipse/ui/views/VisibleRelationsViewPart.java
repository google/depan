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
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetSaveLoadControl;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class VisibleRelationsViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Visible Relations";

  /////////////////////////////////////
  // UX Elements

  /**
   * The {@code RelationSetEditorControl} that controls the UX.
   */
  private RelationSetEditorControl relationSetEditor;

  /////////////////////////////////////
  // RelationSet integration

  private PartRelationRepo vizRepo;

  private static class PartRelationRepo
      implements RelationSetRepository,
          RelationSetRepository.ProvidesUniverse {

    private final ViewEditor editor;

    private PartPrefsListener prefsListener;

    public PartRelationRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public boolean isRelationIncluded(Relation relation) {
      return editor.isVisibleRelation(relation);
    }

    @Override
    public void setRelationChecked(Relation relation, boolean isChecked) {
      editor.setVisibleRelation(relation, isChecked);
    }

    @Override
    public void addChangeListener(
        RelationSetRepository.ChangeListener listener) {
      prefsListener = new PartPrefsListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(
        RelationSetRepository.ChangeListener listener) {
      editor.removeViewPrefsListener(prefsListener);
    }

    @Override
    public Collection<Relation> getUniverse() {
      return editor.getDisplayRelations();
    }
  }

  private static class PartPrefsListener extends ViewPrefsListener.Simple {

    private RelationSetRepository.ChangeListener listener;

    public PartPrefsListener(RelationSetRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void relationVisibleChanged(Relation relation, boolean visible) {
      listener.includedRelationChanged(relation, visible);
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

    relationSetEditor = new RelationSetEditorControl(result);
    relationSetEditor.setLayoutData(Widgets.buildGrabFillData());

    Composite saves = setupSaveButtons(result);
    saves.setLayoutData(Widgets.buildHorzFillData());
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  private Composite setupSaveButtons(Composite parent) {
    RelationSetSaveLoadControl result =
        new RelationSetSaveLoadControl(parent) {

          @Override
          protected IProject getProject() {
            return VisibleRelationsViewPart.this.getProject();
          }

          @Override
          protected RelationSetDescriptor buildSaveResource() {
            return VisibleRelationsViewPart.this.buildSaveResource();
          }

          @Override
          protected void installLoadResource(
              PropertyDocumentReference<RelationSetDescriptor> ref) {
            if (null != ref) {
              RelationSetDescriptor doc = ref.getDocument();
              VisibleRelationsViewPart.this.installLoadResource(doc);
            }
          }
      
    };
    return result;
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();

    vizRepo = new PartRelationRepo(editor);
    relationSetEditor.setRelationSetRepository(vizRepo);

    relationSetEditor.setRelationSetSelectorInput(
        editor.getDefaultRelationSet(), editor.getResourceProject());
  }

  @Override
  protected void releaseResources() {
    relationSetEditor.removeRelationSetRepository(vizRepo);
    vizRepo = null;
  }

  private IProject getProject() {
    return vizRepo.editor.getResourceProject();
  }

  private RelationSetDescriptor buildSaveResource() {
    ViewEditor editor = getEditor();
    RelationSet vizRelSet = editor.getVisibleRelationSet();

    String name = editor.getBaseName();
    DependencyModel model = editor.getDependencyModel();
    return new RelationSetDescriptor(name, model, vizRelSet);
  }

  private void installLoadResource(RelationSetDescriptor doc) {
    if (null == doc) {
      return;
    }

    ViewEditor ed = getEditor();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      ed.setVisibleRelation(relation, doc.getInfo().contains(relation));
    }
  }
}
