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
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetSaveLoadControl;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;
import com.google.devtools.depan.resources.DirectDocumentReference;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.OptionPreferences;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

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

  private Button onlySelected;

  /**
   * The {@code RelationSetEditorControl} that controls the UX.
   */
  private RelationSetEditorControl relationSetEditor;

  /////////////////////////////////////
  // RelationSet integration

  private PartRelationRepo vizRepo;

  private class ControlSaveLoadControl extends RelationSetSaveLoadControl {
    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

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
  }

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
      prefsListener = new PartPrefsListener(
          listener, editor.getDisplayRelations());
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

    private final Collection<Relation> displayRelations;
    private RelationSetRepository.ChangeListener listener;

    public PartPrefsListener(
        RelationSetRepository.ChangeListener listener,
        Collection<Relation> displayRelations) {
      this.listener = listener;
      this.displayRelations = displayRelations;
    }

    @Override
    public void relationSetVisibleChanged(
        PropertyDocumentReference<RelationSetDescriptor> visRelSet) {
      RelationSet doc = visRelSet.getDocument().getInfo();
      for(Relation relation : displayRelations) {
        boolean visible = doc.contains(relation);
        listener.includedRelationChanged(relation, visible);
      }
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

    Composite visibleControls = createControls(result);
    visibleControls.setLayoutData(Widgets.buildHorzFillData());

    relationSetEditor = new RelationSetEditorControl(result);
    relationSetEditor.setLayoutData(Widgets.buildGrabFillData());

    RelationSetSaveLoadControl saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());
  }

  /**
   * @param result
   * @return
   */
  private Composite createControls(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Filters", 1);

    onlySelected = Widgets.buildCompactCheckButton(result, "Only selected nodes");
    onlySelected.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleOnlySelected();
      }
    });

    return result;
  }

  private void handleOnlySelected() {
    ViewEditor editor = getEditor();
    if (editor != null) {
      boolean value = onlySelected.getSelection();
      editor.setBooleanOption(
          OptionPreferences.ONLY_SELECTED_NODE_EDGES_ID, value);
    }
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

    vizRepo = new PartRelationRepo(editor);
    relationSetEditor.setRelationSetRepository(vizRepo);

    relationSetEditor.setRelationSetSelectorInput(
        editor.getVisibleRelationSet(), editor.getResourceProject());

    onlySelected.setSelection(
        editor.isOptionChecked(OptionPreferences.ONLY_SELECTED_NODE_EDGES_ID));
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
    PropertyDocumentReference<RelationSetDescriptor> vizRelSet =
        editor.getVisibleRelationSet();

    String name = editor.getBaseName();
    DependencyModel model = editor.getDependencyModel();
    return new RelationSetDescriptor(
        name, model, vizRelSet.getDocument().getInfo());
  }

  private void installLoadResource(RelationSetDescriptor doc) {
    if (null == doc) {
      return;
    }

    ViewEditor ed = getEditor();
    DirectDocumentReference<RelationSetDescriptor> rsrcRef =
        DirectDocumentReference.buildDirectReference(doc);
    ed.setVisibleRelationSet(rsrcRef);
  }
}
