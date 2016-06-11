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

package com.google.devtools.depan.remap_doc.eclipse.ui.widgets;

import com.google.devtools.depan.remap_doc.plugins.ElementEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An Element editor with two {@link ElementEditor} stacked, the first one is
 * the source, the second one is the target. Useful to edit Elements in
 * refactoring, to see "before" and "after".
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> the type of ElementEditor this {@link DoubleElementEditor}
 *        contains.
 */
public class DoubleElementEditor<E extends ElementEditor>
    extends ElementEditor {
  protected ElementEditor sourceEditor;
  protected ElementEditor targetEditor;

  /**
   * @param parent
   * @param style
   */
  public DoubleElementEditor(
      Composite parent, int style, Class<? extends ElementEditor> c) {
    super(parent, style, SWT.NONE);

    try {
      Constructor<? extends ElementEditor> constructor =
          c.getConstructor(Composite.class, Integer.class, Integer.class);

      Label labelSource = new Label(this, SWT.NONE);
      sourceEditor =
        constructor.newInstance(this, style, SWT.READ_ONLY | SWT.BORDER);
      Label labelTarget = new Label(this, SWT.NONE);
      targetEditor =
        constructor.newInstance(this, style, SWT.NONE | SWT.BORDER);

      this.setLayout(new GridLayout(1, false));

      sourceEditor.setLayoutData(
          new GridData(SWT.FILL, SWT.FILL, true, false));
      targetEditor.setLayoutData(
          new GridData(SWT.FILL, SWT.FILL, true, false));

      labelSource.setText("Source");
      labelTarget.setText("Target");

    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
