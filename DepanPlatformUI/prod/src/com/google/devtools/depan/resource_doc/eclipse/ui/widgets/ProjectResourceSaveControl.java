/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.resource_doc.eclipse.ui.widgets;

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.FileDocumentReference;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.DrillDownComposite;

import java.text.MessageFormat;

/**
 * Provide a standard component for specifying the persistent
 * save location for a user defined resource.
 * 
 * Although this control uses a {@link DrillDownComposite} internally,
 * the results are taken the the user input text fields for the
 * container and file name.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ProjectResourceSaveControl<T extends PropertyDocument<?>>
    extends ProjectResourceControl<T> {

  /////////////////////////////////////
  // UX elements

  private Text containerText;

  private Text fileText;

  /////////////////////////////////////
  // Public methods

  /**
   * Provide the composite GUI element.
   */
  public ProjectResourceSaveControl(
      Composite parent, UpdateListener client,
      ResourceContainer container, IContainer rsrcContainer,
      String fileName, String requiredExt) {
    super(parent, client, container, rsrcContainer, fileName, requiredExt);
    setLayout(Widgets.buildContainerLayout(1));
 
    Composite contents = setupContents(this);
    contents.setLayoutData(Widgets.buildGrabFillData());
  }

  public IFile getResourceLocation() throws CoreException {
    String containerName = getContainerName();
    String inputName = getInputName();
    IResource resource =
        WorkspaceTools.buildWorkspaceResource(new Path(containerName));
    if (!(resource instanceof IContainer) || !resource.exists()) {
      String msg = MessageFormat.format(
          "Container \'{0}\' does not exist.", containerName);
      PlatformTools.throwCoreException(
          msg, "com.google.devtools.depan.platform.ui");
    }
    return PlatformTools.buildResourceFile(
        (IContainer) resource, inputName);
  }

  @Override
  public PropertyDocumentReference<T> getDocumentReference()
      throws CoreException {
    IFile location = getResourceLocation();
    return FileDocumentReference.buildFileReference(
        location, null);
  }

  @Override
  public String validateInputs() {
    String inputName = getInputName();

    String result = validateInputs(getContainerName(), inputName);
    if (null != result) {
      return result;
    }

    IPath filePath = PlatformTools.buildPath(inputName);
    result = validateExtension(filePath.getFileExtension());
    if (null != result) {
      return result;
    }
    return null;
  }

  private String getContainerName() {
    return containerText.getText();
  }

  private String getInputName() {
    return fileText.getText();
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected Composite setupContents(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Resource Location", 1);

    // Container by name - grid box text includes layout
    containerText = buildContainerText(result);

    // Container by tree
    Composite containerControl = setupContainerControl(result);
    containerControl.setLayoutData(Widgets.buildGrabFillData());
    addSelectionChangedListener(new ISelectionChangedListener () {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        handleSelectionChange(Selections.getFirstObject(selection));
      }});

    // File name selection
    Composite fileGrp = setupFilenameControl(result);
    fileGrp.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  protected Composite setupFilenameControl(Composite parent) {

    Composite result = Widgets.buildGridContainer(parent, 2);
    result.setLayoutData(Widgets.buildHorzFillData());
    Widgets.buildCompactLabel(result, "&File name:");

    fileText = Widgets.buildGridBoxedText(result);
    fileText.setText(getFileName());

    fileText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        fireClientOnUpdate();
      }
    });
    return result;
  }

  private void handleSelectionChange(Object selection) {
    if (selection instanceof IFile) {
      IFile file = (IFile) selection;
      String fileName = file.getName();
      IContainer rsrcContainer = file.getParent();

      containerText.setText(rsrcContainer.getFullPath().toString());
      fileText.setText(fileName);
    }
  }
}
