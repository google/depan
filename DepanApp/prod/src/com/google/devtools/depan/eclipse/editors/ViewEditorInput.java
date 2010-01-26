/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditorInput implements IEditorInput {

  private final ViewDocument viewInfo;

  // TODO(leeca):  In the future, this could evolve into a bundle of
  // transient display options.
  private final boolean skipLayout;

  public ViewEditorInput(ViewDocument viewInfo, boolean skipLayout) {
    this.viewInfo = viewInfo;
    this.skipLayout = skipLayout;
  }

  public ViewDocument getViewDocument() {
    return viewInfo;
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
    return null;
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
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter) {
    return null;
  }

  /**
   * @return whether to skip node layout computation on initial render.
   */
  public boolean skipLayout() {
    return skipLayout;
  }
}
