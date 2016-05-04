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

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A standard page for accepting user input in an analysis wizard.
 * The top portion allows the user to specify the file name for
 * the analysis output.
 * 
 * Extending classes should implement the
 * {@link AbstractResouceWizardPage.createSourceControl(Composite)} method to
 * define user's content to analyze.
 */
public abstract class AbstractResouceWizardPage extends WizardPage {

  private final ISelection selection;

  private String outputGroupText;

  private final String defaultFilename;

  private ResourceOutputPart outputPart;

  private ResourceOptionWizard options;

  /**
   * @param selection
   */
  public AbstractResouceWizardPage(
      ISelection selection, String pageLabel, String pageDescription,
      String outputGroupText, String defaultFilename,
      ResourceOptionWizard options) {
    super(pageLabel);
    this.selection = selection;
    this.defaultFilename =  defaultFilename;
    this.outputGroupText = outputGroupText;
    this.options = options;

    setTitle(pageLabel);
    setDescription(pageDescription);
  }

  /**
   * Add the graph document's file extension to the filename.
   */
  protected static String createFilename(String filename) {
    return filename + '.' + GraphDocument.EXTENSION;
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    IContainer outputContainer = WorkspaceTools.guessContainer(selection);
    String outputFilename = WorkspaceTools.guessNewFilename(
        outputContainer, defaultFilename, 1, 10);
    outputPart = new ResourceOutputPart(this, outputContainer, outputFilename);
    Composite outputGroup = outputPart.createControl(
        container, outputGroupText);
    outputGroup.setLayoutData(createHorzFillData());

    // Many document wizard pages only need container and name.
    Composite sourceGroup = options.createOptionsControl(container);
    if (null != sourceGroup) {
      sourceGroup.setLayoutData(createHorzFillData());
    }

    updateStatus(getPageErrorMsg());
    setControl(container);
  }

  /**
   * Provide a GridData instance that should expand to fill any available
   * horizontal space.
   */
  protected GridData createHorzFillData() {
    return new GridData(SWT.FILL, SWT.FILL, true, false);
  }

  /**
   * Provide a GridData instance that should span multiple columns.
   * @return
   */
  protected GridData createColSpanData(int columns) {
    return new GridData(SWT.FILL, SWT.FILL, true, false, columns, 1);
  }

  protected Label createSimpleLabel(Composite parent, String text) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(text);
    return result;
  }

  protected Label createPlaceholder(Composite parent) {
    return new Label(parent, SWT.NONE);
  }

  public IFile getOutputFile() throws CoreException {
    return outputPart.getOutputFile();
  }

  public String getOutputFileName () {
    return outputPart.getFileName();
  }

  @Override
  public boolean isPageComplete() {
    return outputPart.isComplete() && options.isComplete();
  }

  protected String getPageErrorMsg() {
    String result = outputPart.getErrorMsg();
    if (null != result) {
      return result;
    }
    return options.getErrorMsg();
  }

  protected void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  /**
   * @param container
   */
  // protected abstract Composite createSourceControl(Composite container);

  // protected abstract String getAnalysisSourceErrorMsg();

  // protected boolean hasCompleteAnalysisSource() {
    // return (null == getAnalysisSourceErrorMsg());
  // }
}
