/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class LayoutDialog<T extends LayoutPlan>
    extends Dialog {

  private final T layout;

  private final IProject project;

  private T result;

  /**
   * @param parentShell
   */
  protected LayoutDialog(
      Shell parentShell, T layout, IProject project) {
    super(parentShell);
    this.layout = layout;
    this.project = project;
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected void okPressed() {
    result = buildFilter();
    super.okPressed();
  }

  public T getResult() {
    return result;
  }

  protected T getLayoutPlan() {
    return layout;
  }

  protected IProject getProject() {
    return project;
  }

  protected abstract T buildFilter();

  /////////////////////////////////////
  // UX Setup

  /**
   * Add Save As... button on left side of each FilterEditor dialog.
   */
  @Override
  protected Control createButtonBar(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 2);
    result.setLayoutData(Widgets.buildHorzFillData());

    Composite saveAs = setUpSaveAs(result);
    saveAs.setLayoutData(Widgets.buildHorzFillData());

    // Parent's button bar on right
    super.createButtonBar(result);
    return result;
  }

  private Composite setUpSaveAs(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = Widgets.buildContainerLayout(1);
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
    layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
    layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
    result.setLayout(layout);

    Button saveAs = Widgets.buildCompactPushButton(result, "Save As...");
    setButtonLayoutData(saveAs);
    saveAs.addSelectionListener(new SelectionAdapter() {
      
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleSaveAs();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // SaveAs support

  /**
   * Provide a document to save,
   * based on the current state of the editor's controls.
   */
  protected LayoutPlanDocument<? extends LayoutPlan> buildSaveResource() {
    LayoutPlanDocument<? extends LayoutPlan>  result = null;
    return result;
  }

  private void handleSaveAs() {
    LayoutPlanDocument<? extends LayoutPlan> rsrc = buildSaveResource();
    //$ config.saveResource(rsrc, getShell(), getProject());
  }
}
