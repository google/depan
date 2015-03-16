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

package com.google.devtools.depan.eclipse.wizards;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
import org.eclipse.ui.dialogs.SaveAsDialog;

/**
 * A wizard page to create a new named relationship set.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NewRelationshipSetPage extends WizardPage {

  /**
   * The file where we want to add the new relationship set (can be a new file)
   */
  private Text file = null;

  /**
   * The new name for the set.
   */
  private Text setName = null;

  /**
   * Construct the new Wizard page.
   */
  protected NewRelationshipSetPage() {
    super("New Named set of relationships");
    setTitle("New Named set of relationships");
    setDescription("Add a new set of relationships to a already existing "
        + ".dpans file, or create a new .dpans file with a new set.");
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IDialogPage
   *      #createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(4, false);
    composite.setLayout(layout);
    layout.verticalSpacing = 9;

    // create components
    new Label(composite, SWT.NULL).setText("&File");
    file = new Text(composite, SWT.BORDER | SWT.SINGLE);
    Button containerBrowse = new Button(composite, SWT.PUSH);
    Button fileBrowse = new Button(composite, SWT.PUSH);
    new Label(composite, SWT.NULL).setText("&Set Name");
    setName = new Text(composite, SWT.BORDER | SWT.SINGLE);

    // actions
    ModifyListener listener = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    };
    file.addModifyListener(listener);
    setName.addModifyListener(listener);

    containerBrowse.setText("New File");
    containerBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        containerBrowse();
      }
    });

    fileBrowse.setText("Existing File");
    fileBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        fileBrowse();
      }
    });

    // layout
    file.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    setName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    composite.pack();
    composite.getParent().getParent().pack();

    setPageComplete(false);
    setControl(composite);
  }

  /**
   * Check data entered. File name and set name must be specified.
   */
  private void dialogChanged() {
    String fileName = getFilename();
    String set = getSetname();

    if ((null == fileName) || (fileName.length() < 1)) {
      updateStatus("File name must be specified");
      return;
    }

    if ((null == set) || (set.length() < 1)) {
      updateStatus("Set name must be specified");
      return;
    }

    updateStatus(null);
  }

  /**
   * Set the error message. If <code>message</code> is <code>null</code>,
   * the dialog is complete, and ready to submit.
   *
   * @param message error message, or <code>null</code> if the dialog is
   *        complete
   */
  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  /**
   * Open a browser to select a container for a new file.
   */
  private void containerBrowse() {
    Shell shell = new Shell(Display.getDefault());
    SaveAsDialog saveas = new SaveAsDialog(shell);
    if (saveas.open() == SaveAsDialog.OK) {
      IPath result = saveas.getResult();
      file.setText(result.toOSString());
    }
  }

  /**
   * Open a browser to select an existing resource. By default, set a filter
   * to "*.dpans".
   */
  private void fileBrowse() {
    Shell shell = new Shell(Display.getDefault());
    FilteredResourcesSelectionDialog dialog =
      new FilteredResourcesSelectionDialog(
        shell, false, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
    dialog.setInitialPattern("*.dpans");
    if (dialog.open() == ResourceSelectionDialog.OK) {
      Object[] result = dialog.getResult();
      if (result.length == 1) {
        System.out.println(result[0].getClass());
        if (result[0] instanceof File) {
          File resource = (File) result[0];
          file.setText(resource.getFullPath().toOSString());
        }
      }
    }
  }

  /**
   * @return the filename chosen by the user.
   */
  public String getFilename() {
    return file.getText();
  }

  /**
   * @return the set name chosen by the user.
   */
  public String getSetname() {
    return setName.getText();
  }

}
