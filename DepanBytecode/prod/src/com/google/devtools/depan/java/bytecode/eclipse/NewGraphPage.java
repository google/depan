/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.java.bytecode.eclipse;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dpang).
 */

public class NewGraphPage extends WizardPage {
  private Text containerText;

  private Text fileText;

  private ISelection selection;
  private Text dirCP;
  private Text jarCP;
  private Button radioJar;
  private Button radioDir;
  private Button basePathButton;
  private Button baseJarButton;
  private Text packageFilter;
  private Text directoryFilter;
  private Button infer;
  private Button dirFilterButton;

  /**
   * @param selection
   */
  public NewGraphPage(ISelection selection) {
    super("New Graph 1/2");
    setTitle("New Graph Analysis");
    setDescription("This wizard creates a new *.dpang "
        + "file, i.e a new class dependencies' analysis.");
    this.selection = selection;
  }


  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.dialogs.IDialogPage
   *      #createControl(org.eclipse.swt.widgets.Composite)
   */
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 3;
    layout.verticalSpacing = 9;

    GridData dontFill = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    GridData fillGrid = new GridData(SWT.FILL, SWT.FILL, true, false);
    GridData span3Col = new GridData(SWT.FILL, SWT.FILL, true, false,
        3, 1);

    Label label = new Label(container, SWT.NULL);
    label.setText("&Container:");
    containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
    containerText.setLayoutData(dontFill);
    containerText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    Button button = new Button(container, SWT.PUSH);
    button.setText("Browse...");
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });


    label = new Label(container, SWT.NULL);
    label.setText("&File name:");
    fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
    fileText.setLayoutData(dontFill);
    fileText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    // fill is just a place holder, it is not modified.
    Label fill = new Label(container, SWT.NULL);

    // group for selecting jar file or Directory as input
    Group group = new Group(container, SWT.NONE);
    GridLayout grid = new GridLayout();
    group.setLayout(grid);
    grid.numColumns = 3;
    grid.verticalSpacing = 9;

    group.setText("Class Path");
    group.setLayoutData(span3Col);

    // jar file
    radioJar = new Button(group, SWT.RADIO);
    radioJar.setText("From a .jar file");
    radioJar.setLayoutData(span3Col);

    Label basePathLabel = new Label(group, SWT.NULL);
    basePathLabel.setText("&Jar File:");
    jarCP = new Text(group, SWT.BORDER | SWT.SINGLE);
    jarCP.setLayoutData(fillGrid);
    jarCP.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    baseJarButton = new Button(group, SWT.PUSH);
    baseJarButton.setText("Browse...");
    baseJarButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleFileBrowse(jarCP);
      }
    });

    // Directory
    radioDir = new Button(group, SWT.RADIO);
    radioDir.setText("From a directory");
    radioDir.setLayoutData(span3Col);

    Label baseDirPathLabel = new Label(group, SWT.NULL);
    baseDirPathLabel.setText("&Directory:");
    dirCP = new Text(group, SWT.BORDER | SWT.SINGLE);
    dirCP.setLayoutData(fillGrid);
    dirCP.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    basePathButton = new Button(group, SWT.PUSH);
    basePathButton.setText("Browse...");
    basePathButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(dirCP);
      }
    });

    // group for filters input
    Group groupFilters = new Group(container, SWT.NONE);
    GridLayout gridFilters = new GridLayout();
    groupFilters.setLayout(gridFilters);
    gridFilters.numColumns = 3;
    gridFilters.verticalSpacing = 9;

    groupFilters.setText("Filters");
    groupFilters.setLayoutData(span3Col);

    Label pkgLabel = new Label(groupFilters, SWT.NULL);
    pkgLabel.setText("&Package name:");
    packageFilter = new Text(groupFilters, SWT.BORDER | SWT.SINGLE);
    packageFilter.setLayoutData(fillGrid);
    packageFilter.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    new Label(groupFilters, SWT.NULL);

    Label cpLabel = new Label(groupFilters, SWT.NULL);
    cpLabel.setText("&Directory:");
    directoryFilter = new Text(groupFilters, SWT.BORDER | SWT.SINGLE);
    directoryFilter.setLayoutData(fillGrid);
    directoryFilter.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    dirFilterButton = new Button(groupFilters, SWT.PUSH);
    dirFilterButton.setText("Browse...");
    dirFilterButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(directoryFilter);
      }
    });

    infer = new Button(groupFilters, SWT.PUSH);
    infer.setText("infer");
    infer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        inferDirectoryFilter();
        dialogChanged();
      }
    });

    // listeners for radio buttons
    radioJar.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        enable(true);
        dialogChanged();
      }
    });
    radioDir.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        enable(false);
        dialogChanged();
      }
    });

    container.pack();
    container.getParent().getParent().pack();

    initialize();

    setPageComplete(false);
    setControl(container);
  }

  private void enable(boolean jarSelected) {
    // select Jar radio button, and enable his controls
    radioJar.setSelection(jarSelected);
    jarCP.setEnabled(jarSelected);
    baseJarButton.setEnabled(jarSelected);

    // unselect directory radio button, and disable his controls
    radioDir.setSelection(!jarSelected);
    dirCP.setEnabled(!jarSelected);
    basePathButton.setEnabled(!jarSelected);
    dirFilterButton.setEnabled(!jarSelected);
//    infer.setEnabled(!jarSelected);
  }

  protected void inferDirectoryFilter() {
    if (jarCP.isEnabled()) {
      directoryFilter.setText(packageFilter.getText().replace('.', '/'));
    } else {
      String f = getClassPath();
      String subdir = packageFilter.getText().replace('.', '/');
      if (!f.endsWith(subdir)) {
        directoryFilter.setText(f + "/" + subdir);
      }
    }
  }

  /**
   * Tests if the current workbench selection is a suitable container to use.
   * Set the default filename, and select as default Jar as input.
   */
  private void initialize() {
    if (selection != null && !selection.isEmpty()
        && selection instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) selection;
      if (ssel.size() > 1) {
        return;
      }
      Object obj = ssel.getFirstElement();
      if (obj instanceof IResource) {
        IContainer container;
        if (obj instanceof IContainer) {
          container = (IContainer) obj;
        } else {
          container = ((IResource) obj).getParent();
        }
        containerText.setText(container.getFullPath().toString());
      }
    }
    fileText.setText("Java.dpang");
    enable(true);
  }

  /**
   * Uses the standard container selection dialog to choose the new value for
   * the container field.
   */
  private void handleBrowse() {
    containerText.setText(WorkspaceProjectSelection.selectProject(
        getShell(), containerText.getText()));
  }


  /**
   * Open a directory and write the name in the given {@link Text} object.
   */
  private void handleBrowse(Text cell) {
    cell.setText(new DirectoryDialog(getShell()).open());
  }


  /**
   * Open a File and write the name in the given {@link Text} object.
   */
  private void handleFileBrowse(Text cell) {
    FileDialog dialog = new FileDialog(getShell());
    dialog.setFilterExtensions(new String[] {"*.jar", "*.zip", "*.*"});
    cell.setText(dialog.open());
  }

  /**
   * Ensures that both text fields are set.
   */
  private void dialogChanged() {
    IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(
        new Path(getContainerName()));
    String fileName = getFileName();

    if (getContainerName().length() == 0) {
      updateStatus("File container must be specified");
      return;
    }
    if (container == null
        || (container.getType()
            & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus("File container must exist");
      return;
    }
    if (!container.isAccessible()) {
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
      if (!ext.equalsIgnoreCase("dpang")) {
        updateStatus("File extension must be \"dpang\"");
        return;
      }
    }

    if (radioJar.getSelection()) {
      File f = new File(this.jarCP.getText());
      if (!f.exists()) {
        updateStatus("Jar file doesn't exists");
        return;
      }
      if (!f.canRead()) {
        updateStatus("Can't read Jar file directory");
        return;
      }
    } else {
      File f = new File(dirCP.getText());
      if (!f.exists()) {
        updateStatus("Base Path directory doesn't exists");
        return;
      }
      if (!f.canRead()) {
        updateStatus("Can't read Base Path directory");
        return;
      }
    }

    updateStatus(null);
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getContainerName() {
    return containerText.getText();
  }

  public String getFileName() {
    return fileText.getText();
  }

  public String getClassPath() {
    if (radioJar.getSelection()) {
      return jarCP.getText();
    } else {
      return dirCP.getText();
    }
  }

  public String getDirectoryFilter() {
    return directoryFilter.getText();
  }

  public String getPackageFilter() {
    return packageFilter.getText();
  }


}
