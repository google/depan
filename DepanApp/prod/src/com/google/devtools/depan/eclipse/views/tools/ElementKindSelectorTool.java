/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.ElementKindPicker;
import com.google.devtools.depan.filters.NodeKindMatcher;
import com.google.devtools.depan.filters.PathMatcher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Allow the user to select nodes based on the type of the node.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementKindSelectorTool extends Composite {

  /** Control for selecting Element Kinds. */
  private ElementKindPicker elementKindPicker;

  /**
   * Construct the UI for element kind selection.
   * 
   * @param parent container windows
   * @param style standard window style
   */
  public ElementKindSelectorTool(Composite parent, int style) {
    super(parent, style);

    setLayout(new GridLayout());

    // Top: element kind selection
    elementKindPicker = new ElementKindPicker(this, SWT.NONE);
    elementKindPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
  }

  /**
   * Update all the UI values to the settings for the current 
   * {@code ViewEditor}.
   * 
   * @param editor source of settings for UI configuration
   */
  public void updateControls(ViewEditor editor) {
    elementKindPicker.setInput(editor.getElementKinds());
  }

  @Override
  public void dispose() {
    elementKindPicker.dispose();
    super.dispose();
  }

  /**
   * Adaptor class that provides a UI "Part" which contains a 
   * {@code ElementKindSelectorTool}.  This allows simple integration with
   * the generic {@code SelectionEditorTool}.
   */
  public static class SelectorPart
      implements SelectionEditorTool.NodeSelectorPart {

    private ElementKindSelectorTool selectorTool;

    @Override
    public Composite createControl(
        Composite parent, int style, ViewEditor viewEditor) {
      selectorTool = new ElementKindSelectorTool(parent, style);
      return selectorTool;
    }

    @Override
    public PathMatcher getNodeSelector() {
      return new NodeKindMatcher(
          selectorTool.elementKindPicker.getSelectedElementKindSet());
    }

    @Override
    public void updateControl(ViewEditor viewEditor) {
      selectorTool.updateControls(viewEditor);
    }
  }
}
