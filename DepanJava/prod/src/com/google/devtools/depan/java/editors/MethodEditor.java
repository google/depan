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

package com.google.devtools.depan.java.editors;

import com.google.devtools.depan.java.eclipse.JavaActivator;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * An editor for MethodElements.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MethodEditor extends ElementEditor {
  private Label icon;
  private Text name;
  private Text signature;
  private TypeEditor container;

  public MethodEditor(Composite parent, Integer style, Integer swtTextStyle) {
    super(parent, style, swtTextStyle);
    icon = new Label(this, SWT.NONE);
    name = new Text(this, swtTextStyle);
    Label labelSignature = new Label(this, SWT.NONE);
    signature = new Text(this, swtTextStyle);
    Label labelContainer = new Label(this, SWT.NONE);
    container = new TypeEditor(this, SWT.NONE, swtTextStyle);

    Layout layout = new GridLayout(2, false);
    this.setLayout(layout);

    GridData iconGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
    iconGridData.minimumHeight = 16;
    iconGridData.minimumWidth = 16;
    icon.setLayoutData(iconGridData);
    name.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    labelSignature.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));
    signature.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    labelContainer.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    icon.setImage(JavaActivator.IMAGE_METHOD);
    labelSignature.setText("Signature");
    labelContainer.setText("Class");
  }
}
