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

package com.google.devtools.depan.maven.eclipse;


import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewWizardOptionPart;

import com.google.common.base.Strings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.io.File;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dgi).
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NewGraphMLOptionPart implements NewWizardOptionPart {

  private final AbstractNewDocumentPage containingPage;

  private static final String[] POM_FILTER = new String[] { "*.graphml" };

  // UI elements for embedded analysis source part
  private Text pathEntry;
  private Button pathBrowse;
  private Combo processCombo;

  /////////////////////////////////////
  // Public API

  public NewGraphMLOptionPart(
      AbstractNewDocumentPage containingPage) {
    this.containingPage = containingPage;
  }

  public String getPathText() {
    return pathEntry.getText();
  }

  public File getPathFile() {
    return new File(getPathText());
  }

  public String getTreePrefix() {
    File path = getPathFile();

    return path.getPath();
  }

  public GraphMLProcessing getProcessing() {
    int select = processCombo.getSelectionIndex();
    return GraphMLProcessing.values()[select];
  }

  @Override
  public boolean isComplete() {
    return (null == getErrorMsg());
  }

  /**
   * Determine if the page has been filled in correctly.
   */
  @Override
  public String getErrorMsg() {
    String pomPath = pathEntry.getText();
    if (Strings.isNullOrEmpty(pomPath)) {
      return "GraphML file cannot be empty";
    }
    File pathFile = new File(pomPath);
    if (!pathFile.exists()) {
      return "GraphML file doesn't exist";
    }

    // No problems.
    return null;
  }

  /**
   * Provide a group for selecting jar file or Directory as input.
   */
  @Override
  public Composite createPartControl(Composite container) {
    Group source = Widgets.buildGridGroup(container, "Graph ML", 3);

    // First row: directory path selector
    @SuppressWarnings("unused")
    Label pathLabel = Widgets.buildCompactLabel(source, "&GraphML:");
    pathEntry = Widgets.buildGridBoxedText(source);
    pathEntry.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    pathBrowse = Widgets.buildCompactPushButton(source, "Browse...");
    pathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });

    // Second row: compute or use
    @SuppressWarnings("unused")
    Label procLabel =  Widgets.buildCompactLabel(source, "Processing:");
    processCombo = createProcessCombo(source);
    GridData processData = Widgets.buildHorzFillData();
    processData.horizontalSpan = 2;
    processData.grabExcessHorizontalSpace = false;
    processCombo.setLayoutData(processData);

    return source;
  }

  private Combo createProcessCombo(Composite container) {
    Combo result = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);

    for (GraphMLProcessing item : GraphMLProcessing.values()) {
      result.add(item.label);
    }
    result.select(0);
    return result;
  }

  /**
   * Open a directory and write the name in the given {@link Text} object.
   */
  private void handleBrowse() {
    FileDialog dialog = new FileDialog(containingPage.getShell(), SWT.OPEN);
    dialog.setFilterExtensions(POM_FILTER);
    pathEntry.setText(dialog.open());
  }

  /////////////////////////////////////
  // UX utilities

  private void dialogChanged() {
    containingPage.updatePageStatus();
  }
}
