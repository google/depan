/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filesystem.eclipse;

import com.google.common.collect.Lists;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import java.io.File;
import java.util.List;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dpang).
 */

public class NewFileSystemPage extends AbstractAnalysisPage {

  public static final String PAGE_LABEL = "New File System Analysis";

  // UI elements for embedded analysis source part
  private Text pathEntry;
  private Button pathBrowse;
  private Spinner prefixEntry;

  private String errorMsg;

  /**
   * @param selection
   */
  public NewFileSystemPage(ISelection selection) {
    super(selection, PAGE_LABEL,
        "This wizard creates a new *.dpang"
            + " file from an analysis of a file system tree.",
        "Tree.dpang");
  }

  @Override
  public String getAnalysisSourceErrorMsg() {
    return errorMsg;
  }

  /**
   * @param container
   */
  @Override
  protected Composite createSourceControl(Composite container) {

    // group for selecting jar file or Directory as input
    Group source = new Group(container, SWT.NONE);
    source.setText("File System Tree");

    GridLayout grid = new GridLayout();
    grid.numColumns = 3;
    grid.verticalSpacing = 9;
    source.setLayout(grid);

    // First row: directory path selector
    Label pathLabel = new Label(source, SWT.NONE);
    pathLabel.setText("&Directory:");
    pathEntry = new Text(source, SWT.BORDER | SWT.SINGLE);
    pathEntry.setLayoutData(createHorzFillData());
    pathEntry.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    pathBrowse = new Button(source, SWT.PUSH);
    pathBrowse.setText("Browse...");
    pathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });

    // Second row: prefix depth
    Label prefixLabel = new Label(source, SWT.NULL);
    prefixLabel.setText("&Ignore Depth:");
    prefixEntry = new Spinner(source, SWT.NONE);
    prefixEntry.setMinimum(0);
    configurePrefix(0);

    errorMsg = validateInputs();

    return source;
  }

  /**
   * Ensures that all inputs are valid.
   */
  private void dialogChanged() {
    errorMsg = validateInputs();
    updateStatus(errorMsg);
  }

  /**
   * 
   */
  private String validateInputs() {
    String pathText = getPathText();

    if (pathText.length() == 0) {
      prefixEntry.setMaximum(0);
      return "File system tree must be specified";
    }

    File pathFile = getPathFile();
    configurePrefix(getPathLevels(pathFile));
    if (!pathFile.isDirectory()) {
      return "File system tree is not a directory";
    }

    return null;
  }

  private static int getPathLevels(File path) {
    int result = 0;
    while (null != path) {
      result++;
      path = path.getParentFile();
    }

    // Don't count the root
    return Math.max(0, result - 1);
  }

  private void configurePrefix(int maxLevels) {
    if (maxLevels > 0) {
      prefixEntry.setEnabled(true);
      prefixEntry.setMaximum(maxLevels);
      return;
    }

    prefixEntry.setEnabled(false);
  }

  /**
   * Open a directory and write the name in the given {@link Text} object.
   */
  private void handleBrowse() {
    pathEntry.setText(new DirectoryDialog(getShell()).open());
  }

  private int getPrefixCount() {
    try {
      return Integer.parseInt(prefixEntry.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private String getPathText() {
    return pathEntry.getText();
  }

  public File getPathFile() {
    return new File(getPathText());
  }

  public String getTreePrefix() {
    int prefixCount = getPrefixCount();
    if (prefixCount == 0) {
      return "";
    }

    File path = getPathFile();
    List<File> parts = Lists.newArrayList();

    while (null != path) {
      parts.add(path);
      path = path.getParentFile();
    }

    return parts.get(parts.size() - prefixCount - 1).getPath();
  }
}
