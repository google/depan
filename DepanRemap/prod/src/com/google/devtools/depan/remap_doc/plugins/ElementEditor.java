/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.remap_doc.plugins;

import org.eclipse.swt.widgets.Composite;

/**
 * An interface for all ElementEditors.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public abstract class ElementEditor extends Composite {

  /**
   * Constructor. Require a parent Composite and a style. Requires Integers, and
   * not simple int, since we use reflexion to find the constructor in
   * EditorChooser.
   *
   * @param parent parent Composite.
   * @param style SWT style.
   * @param swtTextStyle SWT style used for text fields. Allow to set a
   *        SWT.READ_ONLY for example.
   */
  public ElementEditor(Composite parent, Integer style, Integer swtTextStyle) {
    super(parent, style);
  }
}
