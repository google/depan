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

package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.model.ViewDocument;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditorInput implements IEditorInput {

  private final ViewDocument viewInfo;

  private final String baseName;

  private LayoutGenerator initialLayout;

  public ViewEditorInput(ViewDocument viewInfo, String baseName) {
    this.viewInfo = viewInfo;
    this.baseName = baseName;
  }

  public String getBaseName() {
    return baseName;
  }

  public ViewDocument getViewDocument() {
    return viewInfo;
  }

  public void setInitialLayout(LayoutGenerator initialLayout) {
    this.initialLayout = initialLayout;
  }

  public LayoutGenerator getInitialLayout() {
    return initialLayout;
  }

  @Override
  public boolean equals(Object compare) {
    // ViewEditorInputs only match themselves, never other entities,
    // even if the content is the same.
    return this == compare;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return ViewDocResources.IMAGE_DESC_VIEWDOC;
  }

  @Override
  public String getName() {
    return viewInfo.getGraphModelLocation().getName();
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return viewInfo.getGraphModelLocation().getFullPath().toString();
  }

  @Override
  // warning suppressed because Class should be parameterized. however the
  // implemented method doesn't use parameter here.
  @SuppressWarnings("rawtypes")
  public Object getAdapter(Class adapter) {
    return null;
  }
}
