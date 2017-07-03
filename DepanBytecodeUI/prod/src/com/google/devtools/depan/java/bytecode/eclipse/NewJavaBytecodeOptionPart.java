/*
 * Copyright 2007 The Depan Project Authors
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

import com.google.devtools.depan.java.bytecode.eclipse.ui.widgets.AsmLevelControl;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewWizardOptionPart;

import com.google.common.base.Strings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
 * OR with the extension that matches the expected one (dgi).
 */
public class NewJavaBytecodeOptionPart implements NewWizardOptionPart {

  private final AbstractNewDocumentPage containingPage;

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
  private AsmLevelControl asmLevelOpt;

  /////////////////////////////////////
  // Public API

  public NewJavaBytecodeOptionPart(
      AbstractNewDocumentPage containingPage) {
    this.containingPage = containingPage;
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

  public AsmFactory getAsmFactory() {
    return asmLevelOpt.getChoice();
  }

  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  /**
   * Determine if the current set of analysis inputs are valid.
   * If not, provide an error message that describes the problem.
   * @return {@code null} if error free, or a non-null error String if
   *     there are problems.
   */
  @Override
  public String getErrorMsg() {
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

  @Override
  public Composite createPartControl(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    Composite classpathGroup = createClasspathGroup(result);
    classpathGroup.setLayoutData(Widgets.buildHorzFillData());

    Composite filterGroup = createFilterGroup(result);
    filterGroup.setLayoutData(Widgets.buildHorzFillData());

    enable(true);

    // install listeners after full configuration of all controls
    // this minimizes the "chatter" during initialization
    installListeners();

    return result;
  }

  /**
   * Provide group for selecting jar file or Directory as input.
   */
  private Composite createClasspathGroup(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Class Path", 3);

    // jar file as source
    jarRadio = Widgets.buildCompactRadio(result, "From a .jar file");
    jarRadio.setLayoutData(Widgets.buildHorzSpanData(3));

    Widgets.buildCompactLabel(result, "&Jar File:");
    jarPath = Widgets.buildGridBoxedText(result);

    jarBrowse = Widgets.buildCompactPushButton(result, "Browse...");

    // directory as source
    dirRadio = Widgets.buildCompactRadio(result, "From a directory");
    dirRadio.setLayoutData(Widgets.buildHorzSpanData(3));

    Widgets.buildCompactLabel(result, "&Directory:");
    dirPath = Widgets.buildGridBoxedText(result);

    dirBrowse = Widgets.buildCompactPushButton(result, "Browse...");

    Widgets.buildCompactLabel(result, "Bytecode Level");
    asmLevelOpt = new AsmLevelControl(result);

    return result;
  }

  private Label createPlaceholder(Composite parent) {
    return new Label(parent, SWT.NONE);
  }

  private Composite createFilterGroup(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Filters", 3);

    // Filter by package name
    Widgets.buildCompactLabel(result, "&Package name:");
    packageFilter = Widgets.buildGridBoxedText(result);

    createPlaceholder(result);

    // Filter by directory name
    Widgets.buildCompactLabel(result, "&Directory:");
    directoryFilter = Widgets.buildGridBoxedText(result);
    dirFilterButton = Widgets.buildCompactPushButton(result, "Browse...");
    infer = Widgets.buildCompactPushButton(result, "infer");

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

      @Override
      public void handleEvent(Event event) {
        enable(true);
        dialogChanged();
      }
    });
    jarPath.addModifyListener(new ModifyListener() {

      @Override
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
      @Override
      public void handleEvent(Event event) {
        enable(false);
        dialogChanged();
      }
    });
    dirPath.addModifyListener(new ModifyListener() {

      @Override
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

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    directoryFilter.addModifyListener(new ModifyListener() {

      @Override
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
    String dirname = new DirectoryDialog(containingPage.getShell()).open();
    if (!Strings.isNullOrEmpty(dirname)) {
      cell.setText(dirname);
      dialogChanged();
    }
  }


  /**
   * Open a File and write the name in the given {@link Text} object.
   */
  private void handleFileBrowse(Text cell) {
    FileDialog dialog = new FileDialog(containingPage.getShell());
    dialog.setFilterExtensions(new String[] {"*.jar", "*.zip", "*.*"});
    String filename = dialog.open();
    if (!Strings.isNullOrEmpty(filename)) {
      cell.setText(filename);
      dialogChanged();
    }
  }

  /////////////////////////////////////
  // UX utilities

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }
}
