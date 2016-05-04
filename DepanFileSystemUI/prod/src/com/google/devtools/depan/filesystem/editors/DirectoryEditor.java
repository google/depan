/*
 * Copyright 2008 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.filesystem.editors;

import com.google.devtools.depan.filesystem.eclipse.FileSystemActivator;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * An {@link ElementEditor} for <code>DirectoryElement</code>s. This editor is
 * intended to be used by the Refactoring Tool.
 *
 * @author Yohann Coppel
 */
public class DirectoryEditor extends ElementEditor {
  protected Label icon;
  protected Text path;

  /**
   * Creates a new <code>DirectoryEditor</code>.
   *
   * @param parent parent Container.
   * @param style SWT style for the container.
   * @param swtTextStyle SWT style for textboxes. Useful to set them
   *        SWT.READ_ONLY for instance.
   */
  public DirectoryEditor(
      Composite parent, Integer style, Integer swtTextStyle) {
    super(parent, style, swtTextStyle);
    // widgets
    icon = new Label(this, SWT.NONE);
    Label labelName = new Label(this, SWT.NONE);
    path = new Text(this, swtTextStyle);

    // layout
    Layout layout = new GridLayout(3, false);
    this.setLayout(layout);

    GridData iconGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
    iconGridData.minimumHeight = 16;
    iconGridData.minimumWidth = 16;
    icon.setLayoutData(iconGridData);
    labelName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    path.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    // content
    icon.setImage(FileSystemActivator.IMAGE_DIRECTORY);
    labelName.setText("Path");
  }
}
