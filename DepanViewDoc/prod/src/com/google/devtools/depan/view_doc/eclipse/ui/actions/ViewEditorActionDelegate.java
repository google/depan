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
package com.google.devtools.depan.view_doc.eclipse.ui.actions;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.OptionPreferences;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditorActionDelegate implements IEditorActionDelegate {

  // selections
  public static final String COLLAPSE_ID =
      "com.google.devtools.depan.eclipse.actions.view.Collapse";
  public static final String SELECT_ALL =
      "com.google.devtools.depan.eclipse.actions.view.SelectAll";

  // other
  public static final String SCREENSHOT_ID =
      "com.google.devtools.depan.eclipse.actions.view.Screenshot";

  /////////////////////////////////////
  // Member fields

  private ViewEditor targetEditor = null;

  /////////////////////////////////////
  // IEditorActionDelegate (& IActionDelegate) implementation

  @Override()
  public void setActiveEditor(IAction action, IEditorPart editor) {
    if (editor instanceof ViewEditor) {
      targetEditor = (ViewEditor) editor;
      updateState(action);
    } else {
      editor = null;
    }
  }

  @Override()
  public void run(IAction action) {
    if (targetEditor == null) {
      return;
    }
    String id = action.getId();

    // visualization options
    if (id.equals(OptionPreferences.STROKEHIGHLIGHT_ID)) {
      toggleOptions(action, OptionPreferences.STROKEHIGHLIGHT_ID);
    } else if (id.equals(OptionPreferences.SHAPE_ID)) {
      toggleOptions(action, OptionPreferences.SHAPE_ID);

    // others
    } else if (id.equals(SCREENSHOT_ID)) {
      targetEditor.takeScreenshot();
    } else if (id.equals(SELECT_ALL)) {
      targetEditor.selectAllNodes();
    }
  }

  @Override()
  public void selectionChanged(IAction action, ISelection selection) {
  }

  /////////////////////////////////////

  private void updateState(IAction action) {
    String optionId = action.getId();
    boolean checked = targetEditor.isOptionChecked(optionId);
    action.setChecked(checked);
  }

  private void toggleOptions(IAction action, String optionId) {
    boolean checked = targetEditor.isOptionChecked(optionId);
    targetEditor.setOption(optionId, OptionPreferences.notValue(checked));
    action.setChecked(!checked);
  }
}
