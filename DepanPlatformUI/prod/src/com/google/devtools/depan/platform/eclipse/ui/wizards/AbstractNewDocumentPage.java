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

package com.google.devtools.depan.platform.eclipse.ui.wizards;

import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.Lists;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * A standard page for accepting user input in new document wizard.
 * The top portion allows the user to specify the file name for
 * the analysis output.
 */
public abstract class AbstractNewDocumentPage extends WizardPage {

  private final ISelection selection;

  private NewDocumentOutputPart outputPart;

  private List<NewWizardOptionPart> wizardParts = Lists.newArrayList();

  public AbstractNewDocumentPage(
      ISelection selection, String pageLabel, String pageDescription) {
    super(pageLabel);
    this.selection = selection;

    setTitle(pageLabel);
    setDescription(pageDescription);
  }

  @Override
  public boolean isPageComplete() {
    for (NewWizardOptionPart part : wizardParts) {
      boolean result = part.isComplete();
      // Early exit fail is this part is not complete.
      if (!result) {
        return result;
      }
    }
    return true;
  }

  @Override
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    // The output part is required and comes first.
    // ??? Should it come last ??? for better defaults/inferred ???
    outputPart = createOutputPart();
    if (null != outputPart) {
      addOptionPart(container, outputPart);
    }

    createOptionsParts(container);

    updateStatus(getPageErrorMsg());
    setControl(container);
  }

  /**
   * Utility method for derived classes to adding input part for the wizard.
   */
  protected void addOptionPart(
      Composite container, NewWizardOptionPart part) {
    wizardParts.add(part);
    Composite widget = part.createPartControl(container);
    widget.setLayoutData(Widgets.buildHorzFillData());
  }

  /////////////////////////////////////
  // Error management methods

  /**
   * Derived classes should extend this with any tests required for their
   * document included in the overriding implementation.
   */
  private String getPageErrorMsg() {
    for (NewWizardOptionPart part : wizardParts) {
      String result = part.getErrorMsg();
      if (null != result) {
        return result;
      }
    }
    return null;
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  /**
   * Callback for child parts to update page status.
   * Could be listener driven.
   */
  public void updatePageStatus() {
    updateStatus(getPageErrorMsg());
  }

  /////////////////////////////////////
  // Utility methods for derived types

  public IContainer guessContainer() {
    return WorkspaceTools.guessContainer(selection);
  }

  /////////////////////////////////////
  // Public API for container and file location

  public IFile getOutputFile() throws CoreException {
    return outputPart.getOutputFile();
  }

  public String getOutputFilename () {
    return outputPart.getFilename();
  }

  /////////////////////////////////////
  // Hook methods

  /**
  * Hook method for defining the output part.
  * 
  * Most resource pages should {@code @Override} this method to define
  * permissible extensions and containers.
  * 
  * @param containingPage Access to page attributes,
  *     such as the shell for dialog inputs.
  */
  protected NewDocumentOutputPart createOutputPart() {
    return null;
  }

  /**
   * Hook method for defining additional document options.
   */
  protected void createOptionsParts(Composite container) {
  }
}
