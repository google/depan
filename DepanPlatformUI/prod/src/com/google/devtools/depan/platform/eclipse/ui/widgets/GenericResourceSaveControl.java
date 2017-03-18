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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.devtools.depan.resources.PropertyDocument;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Control button for saving many types of resource documents.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class GenericResourceSaveControl<T extends PropertyDocument<?>>
    extends Composite {

  private final SaveLoadConfig<T> config;

  public GenericResourceSaveControl(Composite parent, SaveLoadConfig<T> config) {
    super(parent, SWT.NONE);
    this.config = config; 

    setLayout(new RowLayout());

    Button saveButton = new Button(this, SWT.PUSH);
    saveButton.setText(config.getSaveLabel());
    saveButton.setLayoutData(new RowData());

    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleSave();
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

  /////////////////////////////////////
  // Hook methods for button actions

  protected abstract IProject getProject();

  /**
   * Provide a document to save,
   * based on the current state of the editor's controls.
   */
  protected abstract T buildSaveResource();
}
