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

import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.resources.ResourceContainer;

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

  private ResourceOutputPart outputPart;

  private ResourceOptionWizard optionPart;

  /**
   * @param selection
   */
  public AbstractResouceWizardPage(
      ISelection selection, String pageLabel, String pageDescription) {
    super(pageLabel);
    this.selection = selection;

    setTitle(pageLabel);
    setDescription(pageDescription);
  }

  @Override
  public boolean isPageComplete() {
    return outputPart.isComplete() && optionPart.isComplete();
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    outputPart = createOutputPart(this);
    if (null != outputPart) {
      Composite outputGroup = outputPart.createControl(container);
      outputGroup.setLayoutData(createHorzFillData());
    }

    // Many resource document wizard pages only need container and name.
    optionPart = createOptionPart(this);
    if (null != optionPart) {
      Composite sourceGroup = optionPart.createOptionsControl(container);
      sourceGroup.setLayoutData(createHorzFillData());
    }

    updateStatus(getPageErrorMsg());
    setControl(container);
  }

  /**
   * Hook method for defining the output part.
   * 
   * Most resource pages should {@code @Override} this method to define
   * permissible extensions and containers.
   * 
   * @param containingPage Access to page attributes,
   *     such as the shell for dialog inputs.
   */
  protected ResourceOutputPart createOutputPart(
      AbstractResouceWizardPage containingPage) {
    return null;
  }

  /**
   * Hook method for derived types to add an option dialog.
   * 
   * Most resource pages should {@code @Override} this method.
   * 
   * @param containingPage Access to page attributes, such as shell for
   *     dialog inputs.
   */
  protected ResourceOptionWizard createOptionPart(
      AbstractResouceWizardPage containingPage) {
    return null;
  }

  /////////////////////////////////////
  // Error management methods

  private String getPageErrorMsg() {
    if (null != outputPart) {
      String result = outputPart.getErrorMsg();
      if (null != result) {
        return result;
      }
    }
    if (null != optionPart) {
      return optionPart.getErrorMsg();
    }
    return null;
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  protected void updatePageStatus() {
    updateStatus(getPageErrorMsg());
  }

  /////////////////////////////////////
  // Utility methods for derived types

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

  protected IContainer getResourceContainer(
      ResourceContainer container) {
    IContainer resources = WorkspaceTools.guessContainer(selection);
    if (resources != null) {
      return resources.getFolder(container.getPath());
    }
    return null;
  }

  /////////////////////////////////////
  // Public API for container and file location

  public IFile getOutputFile() throws CoreException {
    return outputPart.getOutputFile();
  }

  public String getOutputFilename() {
    return outputPart.getFilename();
  }

  public String getResourceName() {
    return outputPart.getResourceName();
  }
}
