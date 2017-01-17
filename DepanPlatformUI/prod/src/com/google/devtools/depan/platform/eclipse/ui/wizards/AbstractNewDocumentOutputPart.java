/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.platform.eclipse.ui.wizards;

import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * Reusable output part that allows the user to specify the destination
 * output file for the wizard's result.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AbstractNewDocumentOutputPart implements NewDocumentOutputPart {

  /**
   * Listener to notify on edit changes. Updates occur via
   * {@link AbstractNewDocumentPage#updatePageStatus}.
   * Also provides shell for container selection dialogs via
   * {@link AbstractNewDocumentPage#getShell()}.
   */
  private final AbstractNewDocumentPage containingPage;

  /**
   * Label to use for grouped input widgets.
   */
  private final String groupName;

  /**
   * Extension to use for resource.
   */
  private final String requiredExt;

  /**
   * Container for output file.  Edited by user control {@code containerText}.
   */
  private IContainer outputContainer;

  // UI elements
  private Text containerText;
  private Text fileText;

  private String defaultFilename;

  /**
   * @param outputContainer
   * @param outputFilename
   * @param requiredExt 
   */
  public AbstractNewDocumentOutputPart(
      String groupName,
      AbstractNewDocumentPage containingPage,
      IContainer outputContainer,
      String requiredExt,
      String defaultFilename) {
    this.groupName = groupName;
    this.containingPage = containingPage;
    this.outputContainer = outputContainer;
    this.requiredExt = requiredExt;
    this.defaultFilename = defaultFilename;
  }

  @Override
  public Composite createPartControl(Composite container) {
    Group output = Widgets.buildGridGroup(container, groupName, 3);

    // Row 1) Container selection
    Widgets.buildCompactLabel(output, "&Container:");
    containerText = Widgets.buildGridBoxedText(output);
    if (null != outputContainer) {
      containerText.setText(outputContainer.getFullPath().toString());
    }
    Button browse = Widgets.buildCompactPushButton(output, "Browse...");

    // Row 2) File selection
    Widgets.buildCompactLabel(output, "&File name:");
    fileText = Widgets.buildGridBoxedText(output);
    fileText.setText(defaultFilename);

    // Install listeners after initial value assignments
    containerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    browse.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });
    fileText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    return output;
  }

  /**
   * Uses the standard container selection dialog to choose the new value for
   * the container field.
   */
  private void handleBrowse() {
    containerText.setText(WorkspaceTools.selectProject(
        containingPage.getShell(), getContainerName()));
  }

  private String getContainerName() {
    return containerText.getText();
  }


  /**
   * Have the container and filename been completed properly?
   * Part of the answer to {@link WizardPage#isPageComplete().}
   */
  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  @Override
  public String getErrorMsg() {

    // Container validation.
    String containerName = getContainerName();
    if (containerName.length() == 0) {
      return "File container must be specified";
    }
    IResource container =
        WorkspaceTools.buildWorkspaceResource(new Path(containerName));
    if (container == null
        || (container.getType()
            & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      return "File container must exist";
    }
    if (!container.isAccessible()) {
      return "Project must be writable";
    }

    String filename = getFilename();
    if (filename.length() == 0) {
      return "File name must be specified";
    }
    if (filename.replace('\\', '/').indexOf('/', 1) > 0) {
      return "File name cannot include path";
    }
    int dotLoc = filename.lastIndexOf('.');
    if (dotLoc != -1) {
      String ext = filename.substring(dotLoc + 1);
      if (!ext.equalsIgnoreCase(requiredExt)) {
        return "File extension must be \"." + requiredExt + "\"";
      }
    }
    return null;
  }

  @Override
  public String getFilename() {
    return fileText.getText();
  }

  @Override
  public IFile getOutputFile() throws CoreException {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(getContainerName()));
    if (!resource.exists() || !(resource instanceof IContainer)) {
      throwCoreException(
          "Container \"" + getContainerName() + "\" does not exist.");
    }

    IContainer container = (IContainer) resource;
    final IFile file = container.getFile(new Path(getFilename()));
    return file;
  }

  private void throwCoreException(String message) throws CoreException {
    IStatus status = new Status(IStatus.ERROR, "com.google.devtools.depan",
        IStatus.OK, message, null);
    throw new CoreException(status);
  }

  /////////////////////////////////////
  // UX utilities

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }
}
