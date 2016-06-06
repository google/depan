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
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.eclipse.ui.wizards.NewRelationSetWizard;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;
import com.google.devtools.depan.relations.persistence.RelationSetDescriptorXmlPersist;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.net.URI;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class VisibleRelationsViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Relation Visibility";

  /**
   * The <code>RelationSetEditorControl</code> that controls the UX.
   */
  private RelationSetEditorControl relationSetEditor;

  private ToolRelationRepo vizRepo;

  private static class ToolRelationRepo
      implements RelationSetRepository {

    private final ViewEditor editor;

    private VisibleListener prefsListener;

    public ToolRelationRepo(ViewEditor editor) {
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
      prefsListener = new VisibleListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(
        RelationSetRepository.ChangeListener listener) {
      editor.removeViewPrefsListener(prefsListener);
    }
  }

  private static class VisibleListener extends ViewPrefsListener.Simple {

    private RelationSetRepository.ChangeListener listener;

    public VisibleListener(RelationSetRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void relationVisibleChanged(Relation relation, boolean visible) {
      listener.includedRelationChanged(relation, visible);
    }

  }

  @Override
  public Image getTitleImage() {
    return ViewDocResources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  @Override
  protected void createGui(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout());

    relationSetEditor = new RelationSetEditorControl(result);
    relationSetEditor.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite saves = setupSaveButtons(result);
    saves.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Button saveRels = new Button(result, SWT.PUSH);
    saveRels.setText("Save visible as RelationSet...");
    saveRels.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveRels.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    Button saveProps = new Button(result, SWT.PUSH);
    saveProps.setText("Load visible from RelationSet...");
    saveProps.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveProps.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        loadSelection();
      }
    });

    return result;
  }

  /**
   * Open a dialog to save the current selection under a new name.
   */
  private void saveSelection() {

    NewRelationSetWizard wizard =
        new NewRelationSetWizard(getEditor().getVisibleRelationSet());

    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  private void loadSelection() {
    Shell shell = getSite().getWorkbenchWindow().getShell();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(new String[] {RelationSetDescriptor.EXTENSION});
    String visFilename = dialog.open();
    if (null == visFilename) {
      return;
    }

    URI visURI = new File(visFilename).toURI();
    RelationSetDescriptorXmlPersist loader =
        RelationSetDescriptorXmlPersist.build(true);
    RelationSetDescriptor visRels = loader.load(visURI);

    ViewEditor ed = getEditor();
    for (Relation relation : RelationRegistry.getRegistryRelations()) {
      ed.setVisibleRelation(relation, visRels.contains(relation));
    }
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();
    // relationSetEditor.updateTable(editor.getBuiltinAnalysisPlugins());
    relationSetEditor.selectRelations(editor.getDisplayRelations());

    vizRepo = new ToolRelationRepo(editor);
    relationSetEditor.setRelationSetRepository(vizRepo);

    // TODO: Should come from editor
    relationSetEditor.setInput(RelationRegistry.getRegistryRelations());
    relationSetEditor.update();
  }

  @Override
  protected void releaseResources() {
    relationSetEditor.removeRelationSetRepository(vizRepo);
    vizRepo = null;
  }
}
