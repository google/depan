/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.views;

import com.google.devtools.depan.platform.eclipse.ui.views.EditorBoundViewPart;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractViewDocViewPart
    extends EditorBoundViewPart<ViewEditor> {

  public AbstractViewDocViewPart() {
    super(ViewEditor.class);
  }

  @Override
  public void createPartControl(Composite parentComposite) {
    super.createPartControl(parentComposite);
    if (hasEditor()) {
      acquireResources();
    }
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected boolean newEditorCallback(ViewEditor editor) {
    if (hasEditor()) {
      releaseResources();
    }
    return true;
  }

  @Override
  protected boolean closeEditorCallback(ViewEditor editor) {
    if (hasEditor()) {
      releaseResources();
    }
    return true;
  }

  @Override
  public void eOpened(ViewEditor editor) {
    super.eOpened(editor);
    if (hasEditor()) {
      acquireResources();
    }
  }

  @Override
  public void eActivated(ViewEditor editor) {
    super.eActivated(editor);
    if (hasEditor()) {
      acquireResources();
    }
  }

  @Override
  public void eBroughtToTop(ViewEditor editor) {
    super.eBroughtToTop(editor);
    if (hasEditor()) {
      acquireResources();
    }
  }

  @Override
  public void eDeactivated(ViewEditor editor) {
    super.eDeactivated(editor);
  }

  protected abstract void acquireResources();

  protected abstract void releaseResources();
}
