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

import org.eclipse.swt.widgets.Composite;

/**
 * An {@code ElementEditor} for {@code FileElement}s. This editor is
 * intended to be used by the Refactoring Tool.
 *
 * @author Yohann Coppel
 */
public class FileEditor extends DirectoryEditor {
  /**
   * Creates a new {@code FileEditor}.
   *
   * @param parent parent Container.
   * @param style SWT style for the container.
   * @param swtTextStyle SWT style for textboxes. Useful to set them
   *        SWT.READ_ONLY for instance.
   */
  public FileEditor(Composite parent, Integer style, Integer swtTextStyle) {
    super(parent, style, swtTextStyle);
    icon.setImage(FileSystemActivator.IMAGE_FILE);
  }
}
