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
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.RelationDisplayTableControl;
import com.google.devtools.depan.view_doc.eclipse.ui.wizards.NewRelationDisplayDocWizard;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.model.RelationDisplayRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayDocumentXmlPersist;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.net.URI;
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
   * The <code>RelationSetEditorControl</code> that controls the UX.
   */
  private RelationDisplayTableControl propEditor;

  /////////////////////////////////////
  // RelationSet integration

  private RelationDisplayRepository propRepo;

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

    Composite saves = setupSaveButtons(result);
    saves.setLayoutData(Widgets.buildHorzFillData());
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Button saveProps = Widgets.buildGridPushButton(
        result, "Save selected as an Edge Display resource...");
    saveProps.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    Button loadProps = Widgets.buildGridPushButton(
        result, "Load properties from Edge Display resource...");
    loadProps.addSelectionListener(new SelectionAdapter() {
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

    RelationDisplayDocument saveInfo = buildSaveDocument();
    NewRelationDisplayDocWizard wizard =
        new NewRelationDisplayDocWizard(saveInfo);

    Shell shell = getSite().getWorkbenchWindow().getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  private RelationDisplayDocument buildSaveDocument() {
    Collection<Relation> relations = propEditor.getSelection();
    Map<Relation, EdgeDisplayProperty> props =
        buildEdgeDisplayProperties(relations);

    ViewEditor ed = getEditor();
    String name = ed.getBaseName();
    return new RelationDisplayDocument(name, props);
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

  private void loadSelection() {
    FileDialog dialog = buildFileDialog();
    String visFilename = dialog.open();
    if (null == visFilename) {
      return;
    }

    URI visURI = new File(visFilename).toURI();
    RelationDisplayDocumentXmlPersist loader =
        RelationDisplayDocumentXmlPersist.build(true);
    RelationDisplayDocument propInfo = loader.load(visURI);

    // TODO: Options might control overwriting or adding relations
    ViewEditor ed = getEditor();
    for (Map.Entry<Relation, EdgeDisplayProperty> entry :
        propInfo.getRelationProperties().entrySet()) {
      ed.setRelationProperty(entry.getKey(), entry.getValue());
    }
  }

  private FileDialog buildFileDialog() {
    Shell shell = getSite().getWorkbenchWindow().getShell();
    FileDialog result = new FileDialog(shell, SWT.OPEN);
    String[] names = new String [] {"Relation properties"};
    String[] filters = new String[] {
        buildExtensionFilter(RelationDisplayDocument.EXTENSION)};
    result.setFilterNames(names);
    result.setFilterExtensions(filters);
    return result;
  }

  private String buildExtensionFilter(String... exts) {
    if ((null == exts) || (0 == exts.length)) {
      return "";
    }
    StringBuilder result = new StringBuilder("*.");
    Joiner.on(";*.").appendTo(result, exts);
    return result.toString();
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();

    propRepo = new PartRelationDisplayRepo(editor);
    propEditor.setEdgeDisplayRepository(propRepo);

    // TODO: Should come from editor
    propEditor.setInput(RelationRegistry.getRegistryRelations());
    propEditor.update();
  }

  @Override
  protected void releaseResources() {
    propEditor.removeEdgeDisplayRepository(propRepo);
    propRepo = null;
  }
}
