/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.wizards;

import com.google.devtools.depan.eclipse.utils.WorkspaceProjectSelection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Wizard page for creation of a Relationship set file.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NewNamedRelationshipSetPage extends WizardPage {

  private Text filename;
  private Text container;
  private final ISelection selection;

  public NewNamedRelationshipSetPage(ISelection selection) {
    super("New Named set of relationships");
    setTitle("New Named set of relationships");
    setDescription("This wizard creates a new *.dpans "
        + "file, i.e a new set of named relationships");
    this.selection = selection;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IDialogPage
   *      #createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(3, false);
    composite.setLayout(layout);
    layout.verticalSpacing = 9;

    // create components
    new Label(composite, SWT.NULL).setText("&Container:");
    container = new Text(composite, SWT.BORDER | SWT.SINGLE);
    Button containerBrowse = new Button(composite, SWT.PUSH);
    new Label(composite, SWT.NULL).setText("&File name");
    filename = new Text(composite, SWT.BORDER | SWT.SINGLE);

    // text
    containerBrowse.setText("Browse");

    // actions
    ModifyListener listener = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    };
    container.addModifyListener(listener);
    filename.addModifyListener(listener);

    containerBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });

    // components layout
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    filename.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    composite.pack();
    composite.getParent().getParent().pack();

    // initialize values
    initialize();
    setPageComplete(false);
    setControl(composite);
  }

  /**
   * Check the data entered by the user: project must exist and be writable,
   * file name is a new filename and ends with .dpans.
   */
  private void dialogChanged() {
    IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(
        new Path(getContainerName()));
    String fileName = getFileName();

    if (getContainerName().length() == 0) {
      updateStatus("File container must be specified");
      return;
    }
    if (resource == null
        || (resource.getType()
            & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus("File container must exist");
      return;
    }
    if (!resource.isAccessible()) {
      updateStatus("Project must be writable");
      return;
    }
    if (fileName.length() == 0) {
      updateStatus("File name must be specified");
      return;
    }
    if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
      updateStatus("File name must be valid");
      return;
    }
    int dotLoc = fileName.lastIndexOf('.');
    if (dotLoc != -1) {
      String ext = fileName.substring(dotLoc + 1);
      if (!ext.equalsIgnoreCase("dpans")) {
        updateStatus("File extension must be \"dpans\"");
        return;
      }
    } else {
      updateStatus("File extension must be \"dpans\"");
      return;
    }

    updateStatus(null);
  }

  /**
   * Browse for a project.
   */
  private void handleBrowse() {
    container.setText(WorkspaceProjectSelection.selectProject(
        getShell(), container.getText()));
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getContainerName() {
    return container.getText();
  }

  public String getFileName() {
    return filename.getText();
  }

  /**
   * Tests if the current workbench selection is a suitable container to use.
   * Set the default filename.
   */
  private void initialize() {
    // setup the selection
    if ((null != selection) && (!selection.isEmpty())
        && (selection instanceof IStructuredSelection)) {
      IStructuredSelection ssel = (IStructuredSelection) selection;
      if (ssel.size() > 1) {
        return;
      }
      Object obj = ssel.getFirstElement();
      if (obj instanceof IResource) {
        IContainer containerResource;
        if (obj instanceof IContainer) {
          containerResource = (IContainer) obj;
        } else {
          containerResource = ((IResource) obj).getParent();
        }
        container.setText(containerResource.getFullPath().toString());
      }
    }
    // default filename
    filename.setText("relationships.dpans");
  }

}
