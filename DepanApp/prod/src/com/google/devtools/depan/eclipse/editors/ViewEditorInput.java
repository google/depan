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

import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.view.ViewModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewEditorInput implements IEditorInput {

  private final ViewModel view;
  private final Layouts layout;
  private final IFile parentFile;

  public ViewEditorInput(ViewModel view, Layouts layout, IFile parentFile) {
    this.view = view;
    this.layout = layout;
    this.parentFile = parentFile;
  }

  public ViewModel getView() {
    return view;
  }

  public Layouts getLayout() {
    return layout;
  }

  public IFile getParentFile() {
    return parentFile;
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
    return view.getName();
  }

  @Override
  public IPersistableElement getPersistable() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getToolTipText() {
    return view.getName();
  }

  @Override
  // warning suppressed because Class should be parameterized. however the
  // implemented method doesn't use parameter here.
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter) {
    return null;
  }
}
