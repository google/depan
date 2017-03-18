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

import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Control for saving and loading many types of documents.
 * 
 * Provides a space-filling pair of buttons, one for save and one for load.
 * The {@link SaveLoadConfig} instance provides type-specific UX elements
 * and serialization capabilities.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class GenericSaveLoadControl<T extends PropertyDocument<?>>
    extends Composite {

  private SaveLoadConfig<T> config;

  public GenericSaveLoadControl(Composite parent, SaveLoadConfig<T> config) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(2));

    this.config = config; 

    Button saveButton = Widgets.buildGridPushButton(
        this, config.getSaveLabel());

    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleSave();
      }
    });

    Button loadButton = Widgets.buildGridPushButton(
        this, config.getLoadLabel());

    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleLoad();
      }
    });
  }

  /**
   * Open a dialog to save a resource.
   */
  private void handleSave() {

    T rsrc = buildSaveResource();
    config.saveResource(rsrc, getShell(), getProject());
  }

  /**
   * Open a dialog to load a resource.
   */
  private void handleLoad() {
    PropertyDocumentReference<T> ref =
        config.loadResource(getShell(), getProject());
    installLoadResource(ref);
  }

  /////////////////////////////////////
  // Hook methods for button actions

  protected abstract IProject getProject();

  /**
   * Provide a document to save,
   * based on the current state of the editor's controls.
   */
  protected abstract T buildSaveResource();

  /**
   * Configure the editor's controls, based on the supplied document.
   */
  protected abstract void installLoadResource(
      PropertyDocumentReference<T> ref);
}
