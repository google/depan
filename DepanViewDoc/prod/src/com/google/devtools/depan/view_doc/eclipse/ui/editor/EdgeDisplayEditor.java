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
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.EdgeDisplayTableControl;
import com.google.devtools.depan.view_doc.model.EdgeDisplayDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.model.EdgeDisplayRepository;
import com.google.devtools.depan.view_doc.persistence.EdgeDisplayDocumentXmlPersist;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
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
 * Editor for {@link EdgeDisplayDocument} resources.
 * 
 * Based on the [Jun 2106] legacy version of {@code RelationSetDescriptorEditor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class EdgeDisplayEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.editor.EdgeDisplayEditor";

  /**
   * {@link EdgeDisplayDocument} that is being edited.
   */
  private EdgeDisplayDocument propInfo;

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

  private EdgeDisplayTableControl propEditor;

  private EdgeDisplayDocumentRepo propRepo;

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
        saveas.getResult(), RelationSetDescriptor.EXTENSION);
    // TODO: set up a progress monitor
    file = saveFile;
    handleDocumentChange();
    persistDocument(null);
  }

  /**
   * Use the information in the editor to create new document content,
   * for external use (e.g. persistence).
   */
  private void updateDocument() {

    Map<Relation, EdgeDisplayProperty> props =
        propInfo.getRelationProperties();
    EdgeDisplayDocument result =
        new EdgeDisplayDocument(relSetName.getText(), props);
    propInfo = result;
    propRepo.setEdgeDisplayProperties(propInfo.getRelationProperties());
  }

  /**
   * Save the current {@link GraphEdgeMatcherDescriptor} at the supplied
   * location.
   * 
   * @param location
   * @param monitor
   */
  private void persistDocument(IProgressMonitor monitor) {
    EdgeDisplayDocumentXmlPersist persist =
        EdgeDisplayDocumentXmlPersist.build(false);
    WorkspaceTools.saveDocument(file, propInfo, persist, monitor);

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

      EdgeDisplayDocumentXmlPersist persist =
          EdgeDisplayDocumentXmlPersist.build(true);
      propInfo = persist.load(file.getRawLocationURI());

      propRepo = new EdgeDisplayDocumentRepo(
          RelationRegistry.getRegistryRelations());
      propRepo.setEdgeDisplayProperties(propInfo.getRelationProperties());

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
  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.horizontalSpacing = 9;
    container.setLayout(layout);

    // Name for RelationSet descriptor ..
    Composite props = new Composite(container, SWT.NONE);
    props.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    props.setLayout(new GridLayout(2, false));

    Label label = new Label(props, SWT.NULL);
    label.setText("&Name:");

    relSetName = new Text(props, SWT.BORDER | SWT.SINGLE);
    relSetName.setLayoutData(
        new GridData(SWT.FILL, SWT.BEGINNING, true, false));
    if (null != propInfo) {
      relSetName.setText(propInfo.getName());
    }
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

    propEditor = new EdgeDisplayTableControl(container);
    propEditor.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    propEditor.setEdgeDisplayRepository(propRepo);
    propEditor.setInput(RelationRegistry.getRegistryRelations());

    propRepo.addChangeListener(new EdgeDisplayRepository.ChangeListener() {
      @Override
      public void edgeDisplayChanged(
          Relation relation, EdgeDisplayProperty props) {
        setDirtyState(true);
      }
    });
  }

  private void handleDocumentChange() {
    updateDocument();
    setPartName(buildPartName());
  }

  @Override
  public void setFocus() {
  }
}
