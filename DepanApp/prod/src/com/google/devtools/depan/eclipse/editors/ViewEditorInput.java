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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import java.net.URI;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewEditorInput implements IEditorInput {

  private final ViewModel view;
  private final Layouts layout;
  private final URI parentUri;

  public ViewEditorInput(ViewModel view, Layouts layout, URI parentUri) {
    this.view = view;
    this.layout = layout;
    this.parentUri = parentUri;
  }

  public ViewModel getView() {
    return view;
  }

  public Layouts getLayout() {
    return layout;
  }

  public URI getParentUri() {
    return parentUri;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#exists()
   */
  public boolean exists() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
   */
  public ImageDescriptor getImageDescriptor() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getName()
   */
  public String getName() {
    return view.getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getPersistable()
   */
  public IPersistableElement getPersistable() {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorInput#getToolTipText()
   */
  public String getToolTipText() {
    return view.getName();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
   */
  // warning suppressed because Class should be parameterized. however the
  // implemented method doesn't use parameter here.
  @SuppressWarnings("unchecked")
  public Object getAdapter(Class adapter) {
    return null;
  }
}
