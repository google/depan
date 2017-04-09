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
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Control button for loading many types of resource documents.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class GenericResourceLoadControl<T extends PropertyDocument<?>>
    extends Composite {

  private final SaveLoadConfig<T> config;

  public GenericResourceLoadControl(Composite parent, SaveLoadConfig<T> config) {
    super(parent, SWT.NONE);
    this.config = config;

    setLayout(new RowLayout());

    Button loadButton = new Button(this, SWT.PUSH);
    loadButton.setText(config.getLoadLabel());
    loadButton.setLayoutData(new RowData());

    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleLoad();
      }
    });
  }

  /**
   * Open a dialog to load a resource.
   */
  private void handleLoad() {
    IProject project = getProject();
    if (null == project) {
      return;
    }
    PropertyDocumentReference<T> rsrc =
       config.loadResource(getShell(), project);
    if (null != rsrc) {
        installLoadResource(rsrc);
    }
  }

  /////////////////////////////////////
  // Hook methods for button actions

  protected abstract IProject getProject();

  /**
   * Configure the editor's controls, based on the supplied document.
   */
  protected abstract void installLoadResource(
      PropertyDocumentReference<T> ref);
}
