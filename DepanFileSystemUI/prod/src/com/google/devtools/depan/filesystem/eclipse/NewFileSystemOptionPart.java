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

package com.google.devtools.depan.filesystem.eclipse;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewWizardOptionPart;

import com.google.common.collect.Lists;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
 * OR with the extension that matches the expected one (dgi).
 */

public class NewFileSystemOptionPart implements NewWizardOptionPart {

  private final AbstractNewDocumentPage containingPage;

  // UI elements for embedded analysis source part
  private Text pathEntry;
  private Button pathBrowse;
  private Spinner prefixEntry;

  /////////////////////////////////////
  // Public API

  public NewFileSystemOptionPart(
      AbstractNewDocumentPage containingPage) {
    this.containingPage = containingPage;
  }

  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  public String getPathText() {
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

  @Override
  public Composite createPartControl(Composite container) {

    // group for selecting jar file or Directory as input
    Group result = Widgets.buildGridGroup(container, "File System Tree", 3);

    // First row: directory path selector
    @SuppressWarnings("unused")
    Label pathLabel = Widgets.buildCompactLabel(result, "&Directory:");
    pathEntry = Widgets.buildGridBoxedText(result);
    pathEntry.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    pathBrowse = Widgets.buildCompactPushButton(result, "Browse...");
    pathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });

    // Second row: prefix depth
    Label prefixLabel = new Label(result, SWT.NULL);
    prefixLabel.setText("&Ignore Depth:");
    prefixEntry = new Spinner(result, SWT.NONE);
    prefixEntry.setMinimum(0);
    configurePrefix(0);

    return result;
  }

  @Override
  // TODO: Verify that all files exist.
  public String getErrorMsg() {
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
    pathEntry.setText(new DirectoryDialog(containingPage.getShell()).open());
  }

  private int getPrefixCount() {
    try {
      return Integer.parseInt(prefixEntry.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /////////////////////////////////////
  // UX utilities

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }
}
