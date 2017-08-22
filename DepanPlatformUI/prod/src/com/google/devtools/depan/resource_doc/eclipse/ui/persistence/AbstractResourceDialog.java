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

package com.google.devtools.depan.resource_doc.eclipse.ui.persistence;

import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.eclipse.PlatformUIActivator;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resource_doc.eclipse.ui.widgets.ProjectResourceControl;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Based on Eclipse's {@code SaveAsDialog}.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractResourceDialog<T extends PropertyDocument<?>>
    extends TitleAreaDialog {

  private ResourceContainer container;
  private IContainer rsrcRoot;
  private String fileName;
  private String fileExt;

  private PropertyDocumentReference<T> result;

  /////////////////////////////////////
  // UX Elements

  private ProjectResourceControl<T> rsrcLoc;

  public AbstractResourceDialog(Shell parentShell) {
    super(parentShell);
    setShellStyle(SWT.SHELL_TRIM | SWT.SHEET);
  }

  /**
   * Hook method to {@link #configureShell(Shell)}.
   */
  protected abstract void decorateShell(Shell shell);

  /**
   * Hook method to {@link #createContents(Composite)}.
   */
  protected abstract void decorateDialog();

  /**
   * Hook method to {@link #createContents(Composite)}.
   */
  protected abstract ProjectResourceControl<T> buildResourceControl(
      Composite parent);

  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected IDialogSettings getDialogBoundsSettings() {
    return PlatformUIActivator.getPlatformDialogSettings();
  }

  @Override
  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    decorateShell(shell);
  }

  @Override
  protected Control createContents(Composite parent) {
    Control result = super.createContents(parent);
    result.setLayoutData(Widgets.buildGrabFillData());

    decorateDialog();
    return result;
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite result = (Composite) super.createDialogArea(parent);

    rsrcLoc = buildResourceControl(result);
    rsrcLoc.setLayoutData(Widgets.buildGrabFillData());
    return result;
  }

  @Override
  protected void okPressed() {
    try {
      result = rsrcLoc.getDocumentReference();
    } catch (CoreException errCore) {
      PlatformLogger.LOG.error("Resource load error", errCore);
      result = null;
    }

    super.okPressed();
  }

  protected void onResourceUpdate() {
    String msg = rsrcLoc.validateInputs();
    setErrorMessage(msg);

    getButton(IDialogConstants.OK_ID).setEnabled(null == msg);
  }

  public PropertyDocumentReference<T> getResult() {
    return result;
  }

  /////////////////////////////////////
  // Accessors

  public ResourceContainer getContainer() {
    return container;
  }

  public IContainer getResourceRoot() {
    return rsrcRoot;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFileExt() {
    return fileExt;
  }

  public void setInput(
      ResourceContainer container,
      IContainer rsrcRoot,
      String fileName,
      String fileExt) {
    this.container = container;
    this.rsrcRoot = rsrcRoot;
    this.fileName = fileName;
    this.fileExt = fileExt;
  }

  public void setInput(ResourceContainer container, IFile input) {
    setInput(
        container, input.getParent(),
        input.getName(), input.getFileExtension());
  }
}
