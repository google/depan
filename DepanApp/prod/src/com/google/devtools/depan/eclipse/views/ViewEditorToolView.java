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

package com.google.devtools.depan.eclipse.views;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.ListeningViewViewPart;

import org.eclipse.swt.widgets.Composite;

/**
 * A wrapper class that converts individual tools from the palette into
 * standalone Views.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ViewEditorToolView extends ListeningViewViewPart<ViewEditor> {

  /** Contained tool - all operations delegate to this instance */
  // TODO: Share this instances with the tool palette version.
  private final Tool tool;

  public ViewEditorToolView(Tool tool) {
    super(ViewEditor.class);
    this.tool = tool;
  }

  @Override
  public void createGui(Composite parent) {
    tool.setupComposite(parent);

    // After GUI creation, tell the tool which editor it should listen to
    tool.setEditor(getAcceptableEditor());
  }


  @Override
  protected void disposeGui() {
    tool.dispose();
  }

  @Override
  public void eClosed(ViewEditor part) {
    tool.editorClosed(part);
  }

  @Override
  protected boolean newEditorCallback(ViewEditor part) {
    tool.setEditor(part);
    return false;
  }
}
