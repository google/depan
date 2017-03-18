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

package com.google.devtools.depan.relations.eclipse.ui.editors;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.models.RelationSetDescrRepo;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetRepository;
import com.google.devtools.depan.relations.persistence.RelationSetDescriptorXmlPersist;
import com.google.devtools.depan.relations.persistence.RelationSetResources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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

/**
 * Based on a legacy version of {@code NamedRelationshipEditor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationSetDescriptorEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.relations.eclipse.ui.editors.RelationSetDescriptorEditor";

  /**
   * RelationSetDescriptor document that is being edited.
   */
  private RelationSetDescriptor relSetInfo;

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

  private RelationSetEditorControl relationSetEditor;

  private RelationSetDescrRepo relRepo;

  @Override
  public void doSave(IProgressMonitor monitor) {

    handleDocumentChange();
    persistDocument(monitor);
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    saveas.setOriginalFile(file);
    saveas.setOriginalName(relSetInfo.getName());
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
   * Use the information in the editor to create new document content,
   * for external use (e.g. persistence).
   */
  private void updateDocument() {

    RelationSet relSet = relRepo.getRelationSet();
    RelationSetDescriptor result = new RelationSetDescriptor(
        relSetName.getText(), relSetInfo.getModel(), relSet);
    relSetInfo = result;
    relRepo.setRelationSet(relSetInfo.getInfo());
  }

  /**
   * Save the current {@link GraphEdgeMatcherDescriptor} at the supplied
   * location.
   * 
   * @param location
   * @param monitor
   */
  private void persistDocument(IProgressMonitor monitor) {
    RelationSetDescriptorXmlPersist persist =
        RelationSetDescriptorXmlPersist.build(false);
    persist.saveDocument(file, relSetInfo, monitor);

    setDirtyState(false);
  }

  private String buildPartName() {
    StringBuilder result = new StringBuilder();

    // Format matcher name section
    if (null != relSetInfo) {
      result.append(relSetInfo.getName());
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

      RelationSetDescriptorXmlPersist persist =
          RelationSetDescriptorXmlPersist.build(true);
      relSetInfo = persist.load(file.getRawLocationURI());

      relRepo = new RelationSetDescrRepo(relSetInfo.getModel().getRelations());
      relRepo.setRelationSet(relSetInfo.getInfo());

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
    Composite container = Widgets.buildGridContainer(parent, 1);

    // Name for RelationSet descriptor ..
    Composite props = setupProperties(container);
    props.setLayoutData(Widgets.buildHorzFillData());

    relationSetEditor = new RelationSetEditorControl(container);
    relationSetEditor.setLayoutData(Widgets.buildGrabFillData());

    relRepo.addChangeListener(new RelationSetRepository.ChangeListener() {
      @Override
      public void includedRelationChanged(Relation relation, boolean visible) {
        setDirtyState(true);
      }

      @Override
      public void relationsChanged() {
        setDirtyState(true);
      }
    });
    if (null != relSetInfo) {
      setInput(relSetInfo);
    }
  }

  /**
   * @param relSetInfo2
   */
  private void setInput(RelationSetDescriptor relSetInfo) {
    relSetName.setText(relSetInfo.getName());
    relationSetEditor.setRelationSetRepository(relRepo);
  }

  /**
   * In a future world, this might provide access to a complete set
   * of {@code ResourceDocument} properties.
   * @return 
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
        if (relSetInfo.getName().equals(relSetName.getText())) {
          return;
        }
        setDirtyState(true);
        handleDocumentChange();
      }
    });
    return result;
  }

  private void handleDocumentChange() {
    updateDocument();
    setPartName(buildPartName());
  }

  @Override
  public void setFocus() {
  }
}
