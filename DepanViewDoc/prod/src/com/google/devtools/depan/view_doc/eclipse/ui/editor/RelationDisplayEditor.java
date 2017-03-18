/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.RelationDisplayTableControl;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.model.RelationDisplayRepository;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayDocXmlPersist;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;

import java.util.Map;

/**
 * Editor for {@link RelationDisplayDocument} resources.
 * 
 * Based on the [Jun 2106] legacy version of {@code RelationSetDescriptorEditor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationDisplayEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.editor.EdgeDisplayEditor";

  /**
   * {@link RelationDisplayDocument} that is being edited.
   */
  private RelationDisplayDocument propInfo;

  /**
   * The file this editor is editing.
   */
  private IFile file;

  /**
   * Dirty state.
   */
  private boolean isDirty = true;

  /////////////////////////////////////
  // UX Elements

  /**
   * Name of matcher in the editor.
   */
  private Text relSetName;

  private RelationDisplayTableControl propEditor;

  private RelationDisplayDocumentRepo propRepo;

  /////////////////////////////////////
  // Public methods

  @Override
  public void doSave(IProgressMonitor monitor) {

    handleDocumentChange();
    persistDocument(monitor);
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    saveas.setOriginalFile(file);
    saveas.setOriginalName(propInfo.getName());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }

    // get the file relatively to the workspace.
    IFile saveFile = WorkspaceTools.calcViewFile(
        saveas.getResult(), RelationSetResources.EXTENSION);
    // TODO: set up a progress monitor
    file = saveFile;
    handleDocumentChange();
    persistDocument(null);
  }

  /**
   * Save the current {@link RelationDisplayDocument} at the supplied
   * location.
   */
  private void persistDocument(IProgressMonitor monitor) {
    RelationDisplayDocXmlPersist persist =
        RelationDisplayDocXmlPersist.build(false);
    persist.saveDocument(file, propInfo, monitor);

    setDirtyState(false);
  }

  private String buildPartName() {
    StringBuilder result = new StringBuilder();

    // Format matcher name section
    if (null != propInfo) {
      result.append(propInfo.getName());
    } else {
      result.append("Unnamed");
    }

    // Format file name section
    if (null != file) {
      String filename = file.getName();
      int truncAt = filename.length() - file.getFileExtension().length() - 1;
      String briefname = filename.substring(0, truncAt);
      if (result.length() >0) {
        result.append(" ");
      }
      result.append("[");
      result.append(briefname);
      result.append("]");
    } else {
      if (result.length() >0) {
        result.append(" ");
      }
      result.append("[-unsaved-]");
    }

    return result.toString();
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    // only accept a file as input.
    if (input instanceof IFileEditorInput) {
      // get the URI
      IFileEditorInput fileInput = (IFileEditorInput) input;
      file = fileInput.getFile();

      RelationDisplayDocXmlPersist persist =
          RelationDisplayDocXmlPersist.build(true);
      propInfo = persist.load(file.getRawLocationURI());

      propRepo = new RelationDisplayDocumentRepo(
          RelationRegistry.getRegistryRelations());
      propRepo.setEdgeDisplayProperties(propInfo.getInfo());

      setPartName(buildPartName());
      setDirtyState(false);
      return;
    }

    // Something unexpected
    throw new PartInitException(
        "Input for editor is not suitable for the RelationSetDescriptorEditor");
  }

  /**
   * Set the dirtyState for <code>this</code> editor.
   * 
   * @param dirty
   */
  public void setDirtyState(boolean dirty) {
    if (dirty != isDirty()) {
      this.isDirty = dirty;
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }

  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  @Override
  public void setFocus() {
    if (relSetName.getText().isEmpty()) {
      relSetName.setFocus();
    }
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = Widgets.buildContainerLayout(1);
    layout.horizontalSpacing = 9;
    container.setLayout(layout);

    // Name for RelationSet descriptor ..
    Composite props =  setupProperties(container);
    props.setLayoutData(Widgets.buildHorzFillData());

    propEditor = new RelationDisplayTableControl(container);
    propEditor.setLayoutData(Widgets.buildGrabFillData());
    propRepo.addChangeListener(new RelationDisplayRepository.ChangeListener() {
      @Override
      public void edgeDisplayChanged(
          Relation relation, EdgeDisplayProperty props) {
        setDirtyState(true);
      }
    });

    if (null != propInfo) {
      setInput(propInfo);
    }
  }

  /**
   * In a future world, this might provide access to a complete set
   * of {@code ResourceDocument} properties.
   */
  @SuppressWarnings("unused")
  private Composite setupProperties(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);

    Label label = Widgets.buildCompactLabel(result, "&Name:");

    relSetName = new Text(result, SWT.BORDER | SWT.SINGLE);
    relSetName.setLayoutData(Widgets.buildHorzFillData());
    relSetName.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (propInfo.getName().equals(relSetName.getText())) {
          return;
        }
        setDirtyState(true);
        handleDocumentChange();
      }
    });
    return result;
  }

  private void setInput(RelationDisplayDocument propInfo) {
    relSetName.setText(propInfo.getName());

    propEditor.setEdgeDisplayRepository(propRepo);
    propEditor.setInput(RelationRegistry.getRegistryRelations());
  }

  /**
   * Use the information in the editor to create new document content,
   * for external use (e.g. persistence).
   * @return 
   */
  private RelationDisplayDocument buildRelationDisplayDocument() {

    DependencyModel model = propInfo.getModel();
    Map<Relation, EdgeDisplayProperty> props = propInfo.getInfo();
    RelationDisplayDocument result =
        new RelationDisplayDocument(relSetName.getText(), model, props);
    return result;
  }

  private void handleDocumentChange() {
    propInfo = buildRelationDisplayDocument();
    propRepo.setEdgeDisplayProperties(propInfo.getInfo());
    setPartName(buildPartName());
  }
}
