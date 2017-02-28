/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.resource_doc.eclipse.ui.wizards;

import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provide a standard component for specifying the output
 * of an analysis page.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ResourceOutputPart {

  /**
   * Provides the shell for dialog and dynamic input validation.
   * 
   * Integration with WizardPage depends on {@code updateStatus}
   * method introduced by {@link AbstractResouceWizardPage}.
   */
  private final AbstractResouceWizardPage containingPage;

  /**
   * Label to use for grouped input widgets.
   */
  private final String groupName;

  /**
   * Initial content for user control {@code fileText}.
   */
  private final String defaultFilename;

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
  private Group output;

  public ResourceOutputPart(
      AbstractResouceWizardPage containingPage, String groupName,
      IContainer outputContainer,
      String defaultFilename, String requiredExt) {
    this.containingPage = containingPage;
    this.groupName = groupName;
    this.outputContainer = outputContainer;
    this.defaultFilename = defaultFilename;
    this.requiredExt = requiredExt;
  }

  /**
   * Provide a GUI element to collect the output destination for a resource.
   * 
   * The returned control is a {@link Group}, with the text supplied
   * by {@code groupName}.
   * 
   * @param container window context for the UI
   */
  public Composite createControl(Composite container) {
    output = new Group(container, SWT.NONE);
    output.setText(groupName);

    GridLayout grid = new GridLayout();
    grid.numColumns = 3;
    grid.verticalSpacing = 9;
    output.setLayout(grid);

    GridData fillHorz = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

    // Row 1) Container selection
    Label label = new Label(output, SWT.NULL);
    label.setText("&Container:");

    containerText = new Text(output, SWT.BORDER | SWT.SINGLE);
    containerText.setLayoutData(fillHorz);
    if (null != outputContainer) {
      containerText.setText(outputContainer.getFullPath().toString());
    }

    Button button = new Button(output, SWT.PUSH);
    button.setText("Browse...");

    // Row 2) File selection
    label = new Label(output, SWT.NULL);
    label.setText("&File name:");
    fileText = new Text(output, SWT.BORDER | SWT.SINGLE);
    fileText.setLayoutData(fillHorz);
    fileText.setText(defaultFilename);

    // Install listeners after initial value assignments
    containerText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    button.addSelectionListener(new SelectionAdapter() {

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

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }

  /////////////////////////////////////
  // Error management methods

  public String getErrorMsg() {
    IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(
        new Path(getContainerName()));
    String filename = getFilename();
    
    if (getContainerName().length() == 0) {
      return "File container must be specified";
    }
    if (container == null
        || (container.getType()
            & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      return "File container must exist";
    }
    if (!container.isAccessible()) {
      return "Project must be writable";
    }
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

  /**
   * Have the container and filename been completed properly?
   * Part of the answer to {@link WizardPage#isPageComplete().}
   */
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  /////////////////////////////////////
  // Public API for container and file location

  public String getContainerName() {
    return containerText.getText();
  }

  public String getFilename() {
    return fileText.getText();
  }


  public String getResourceName() {
    return new Path(fileText.getText())
        .removeFileExtension().toPortableString();
  }

  /**
   * @param containerName
   * @param fileName
   * @return
   * @throws CoreException
   */
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
}
