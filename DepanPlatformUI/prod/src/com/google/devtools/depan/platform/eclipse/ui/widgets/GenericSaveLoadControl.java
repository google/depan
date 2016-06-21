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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.net.URI;

/**
 * Control for saving and loading many types of documents.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class GenericSaveLoadControl extends Composite {

  public GenericSaveLoadControl(
      Composite parent, String saveLabel, String loadLabel) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(2));

    Button saveButton = new Button(this, SWT.PUSH);
    saveButton.setText(saveLabel);
    saveButton.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    Button loadButton = new Button(this, SWT.PUSH);
    loadButton.setText(loadLabel);
    loadButton.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        loadSelection();
      }
    });
  }

  /**
   * Open a dialog to save the current selection under a new name.
   */
  private void saveSelection() {

    Wizard wizard = getSaveWizard();

    Shell shell = getShell();
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  private void loadSelection() {
    Shell shell = getShell();
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    prepareLoadDialog(dialog);
    String visFilename = dialog.open();
    if (null == visFilename) {
      return;
    }

    loadURI(new File(visFilename).toURI());
  }

  /////////////////////////////////////
  // Hook methods

  /**
   * Prepare the supplied {@link FileDialog} for the 
   * @param dialog
   */
  protected abstract void prepareLoadDialog(FileDialog dialog);

  /**
   * Provide a {@link Wizard} suitable for saving the document.
   * 
   * Typically, the document to save is obtained at via some reference
   * in the concrete derived types, and embedded in the 
   * returned {@link Wizard}.
   */
  protected abstract Wizard getSaveWizard();

  /**
   * Load the document identified by the supplied {@link URI}.
   * 
   * Typically, the document is saved via some reference in the concrete
   * derived type.
   */
  protected abstract void loadURI(URI uri);
}
