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

package com.google.devtools.depan.eclipse.wizards;

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
 * {@link AbstractAnalysisPage.createSourceControl(Composite)} method to
 * define user's content to analyze.
 */
public abstract class AbstractAnalysisPage extends WizardPage {

  private final String defaultFilename;

  private ISelection selection;

  private AnalysisOutputPart outputPart;

  /**
   * @param selection
   */
  public AbstractAnalysisPage(
      ISelection selection, String pageLabel, String pageDescription,
      String defaultFilename) {
    super(pageLabel);
    setTitle(pageLabel);
    setDescription(pageDescription);
    this.selection = selection;
    this.defaultFilename =  defaultFilename;
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    outputPart = new AnalysisOutputPart(this, selection, defaultFilename);
    Composite outputGroup = outputPart.createControl(container);
    outputGroup.setLayoutData(createHorzFillData());

    Composite sourceGroup = createSourceControl(container);
    sourceGroup.setLayoutData(createHorzFillData());

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
    return outputPart.isComplete()
        && hasCompleteAnalysisSource();
  }

  protected String getPageErrorMsg() {
    String result = outputPart.getErrorMsg();
    if (null != result) {
      return result;
    }
    return getAnalysisSourceErrorMsg();
  }

  protected void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  /**
   * @param container
   */
  protected abstract Composite createSourceControl(Composite container);

  protected abstract String getAnalysisSourceErrorMsg();

  protected boolean hasCompleteAnalysisSource() {
    return (null == getAnalysisSourceErrorMsg());
  }
}
