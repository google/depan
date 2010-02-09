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

import com.google.devtools.depan.eclipse.wizards.AbstractAnalysisPage;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dgi).
 */
public class NewGraphPage extends AbstractAnalysisPage {

  private static final String PAGE_LABEL = "New Java Analysis";

  private Text dirPath;
  private Text jarPath;
  private Button jarRadio;
  private Button dirRadio;
  private Button dirBrowse;
  private Button jarBrowse;
  private Text packageFilter;
  private Text directoryFilter;
  private Button infer;
  private Button dirFilterButton;

  private String errorMsg;

  /**
   * @param selection
   */
  public NewGraphPage(ISelection selection) {
    super(selection, PAGE_LABEL,
        "This wizard creates a new dependency graph"
        + " from an analysis of Java .class files.",
        createFilename("Java"));
  }

  @Override
  public String getAnalysisSourceErrorMsg() {
    return errorMsg;
  }

  @Override
  protected Composite createSourceControl(Composite parent) {

    // Outer composite for both the class path group and the
    // filter definition group
    Composite result = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    result.setLayout(layout);

    Composite classpathGroup = createClasspathGroup(result);
    classpathGroup.setLayoutData(createHorzFillData());

    Composite filterGroup = createFilterGroup(result);
    filterGroup.setLayoutData(createHorzFillData());

    enable(true);
    errorMsg = validateInputs();

    // install listeners after full configuration of all controls
    // this minimizes the "chatter" during initialization
    installListeners();

    return result;
  }

  private Composite createClasspathGroup(Composite parent) {
    // group for selecting jar file or Directory as input
    Group result = new Group(parent, SWT.NONE);
    result.setText("Class Path");

    GridLayout grid = new GridLayout(3, false);
    grid.verticalSpacing = 9;
    result.setLayout(grid);

    // jar file as source
    jarRadio = new Button(result, SWT.RADIO);
    jarRadio.setText("From a .jar file");
    jarRadio.setLayoutData(createColSpanData(3));

    createSimpleLabel(result, "&Jar File:");
    jarPath = new Text(result, SWT.BORDER | SWT.SINGLE);
    jarPath.setLayoutData(createHorzFillData());

    jarBrowse = new Button(result, SWT.PUSH);
    jarBrowse.setText("Browse...");

    // directory as source
    dirRadio = new Button(result, SWT.RADIO);
    dirRadio.setText("From a directory");
    dirRadio.setLayoutData(createColSpanData(3));

    createSimpleLabel(result, "&Directory:");
    dirPath = new Text(result, SWT.BORDER | SWT.SINGLE);
    dirPath.setLayoutData(createHorzFillData());

    dirBrowse = new Button(result, SWT.PUSH);
    dirBrowse.setText("Browse...");

    return result;
  }

  private Composite createFilterGroup(Composite parent) {

    Group result = new Group(parent, SWT.NONE);
    result.setText("Filters");

    GridLayout gridFilters = new GridLayout(3, false);
    gridFilters.verticalSpacing = 9;
    result.setLayout(gridFilters);

    // Filter by package name
    createSimpleLabel(result, "&Package name:");
    packageFilter = new Text(result, SWT.BORDER | SWT.SINGLE);
    packageFilter.setLayoutData(createHorzFillData());

    createPlaceholder(result);

    // Filter by directory name
    createSimpleLabel(result, "&Directory:");
    directoryFilter = new Text(result, SWT.BORDER | SWT.SINGLE);
    directoryFilter.setLayoutData(createHorzFillData());

    dirFilterButton = new Button(result, SWT.PUSH);
    dirFilterButton.setText("Browse...");

    infer = new Button(result, SWT.PUSH);
    infer.setText("infer");

    return result;
  }

  private void enable(boolean jarSelected) {
    // select Jar radio button, and enable his controls
    jarRadio.setSelection(jarSelected);
    jarPath.setEnabled(jarSelected);
    jarBrowse.setEnabled(jarSelected);

    // unselect directory radio button, and disable his controls
    dirRadio.setSelection(!jarSelected);
    dirPath.setEnabled(!jarSelected);
    dirBrowse.setEnabled(!jarSelected);
    dirFilterButton.setEnabled(!jarSelected);
  }

  /**
   * Install the listeners that tie together the controls and provide
   * this wizards functionality.  Depressingly, installing these listeners
   * during controls generation leads to many minor state changes when
   * the initial {@code enable(true)} step is performed.
   */
  private void installListeners() {
    // Listeners for jar selection controls
    jarRadio.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        enable(true);
        dialogChanged();
      }
    });
    jarPath.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    jarBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleFileBrowse(jarPath);
      }
    });

    // Listeners for directory selection controls
    dirRadio.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        enable(false);
        dialogChanged();
      }
    });
    dirPath.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    dirBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(dirPath);
      }
    });

    // Listeners for Filters controls.
    packageFilter.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    directoryFilter.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    dirFilterButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse(directoryFilter);
      }
    });
    infer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        inferDirectoryFilter();
        dialogChanged();
      }
    });
  }

  protected void inferDirectoryFilter() {
    if (jarPath.isEnabled()) {
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
    errorMsg = validateInputs();
    updateStatus(errorMsg);
  }

  /**
   * Determine if the current set of analysis inputs are valid.
   * If not, provide an error message that describes the problem.
   * @return {@code null} if error free, or a non-null error String if
   *     there are problems.
   */
  private String validateInputs() {
    if (jarRadio.getSelection()) {
      File f = new File(jarPath.getText());
      if (!f.exists()) {
        return "Jar file doesn't exists";
      }
      if (!f.canRead()) {
        return "Can't read Jar file directory";
      }
    } else {
      File f = new File(dirPath.getText());
      if (!f.exists()) {
        return "Base Path directory doesn't exists";
      }
      if (!f.canRead()) {
        return "Can't read Base Path directory";
      }
    }

    return null;
  }

  public String getClassPath() {
    if (jarRadio.getSelection()) {
      return jarPath.getText();
    } else {
      return dirPath.getText();
    }
  }

  public String getDirectoryFilter() {
    return directoryFilter.getText();
  }

  public String getPackageFilter() {
    return packageFilter.getText();
  }
}
