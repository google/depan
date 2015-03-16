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
package com.google.devtools.depan.eclipse.actions;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds;
import com.google.devtools.depan.eclipse.preferences.PreferencesIds;
import com.google.devtools.depan.eclipse.visualization.layout.JungLayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.TreeLayoutGenerator;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class ViewEditorActionDelegate implements IEditorActionDelegate {

  // layouts
  public static final String FRLAYOUT_ID =
      "com.google.devtools.depan.eclipse.actions.view.SetFRLayout";
  public static final String SPRINGLAYOUT_ID =
      "com.google.devtools.depan.eclipse.actions.view.SetSpringLayout";
  public static final String TREELAYOUT_ID =
      "com.google.devtools.depan.eclipse.actions.view.SetTreeLayout";
  public static final String RADIALLAYOUT_ID =
      "com.google.devtools.depan.eclipse.actions.view.SetRadialLayout";

  // rendering options
  public static final String ROOTHIGHLIGHT_ID =
      "com.google.devtools.depan.eclipse.actions.view.RootHighlight";
  public static final String SHAPE_ID =
      "com.google.devtools.depan.eclipse.actions.view.Shape";
  public static final String STRETCHRATIO_ID =
      "com.google.devtools.depan.eclipse.actions.view.StretchRatio";
  public static final String SIZE_ID =
      "com.google.devtools.depan.eclipse.actions.view.Size";
  public static final String STROKEHIGHLIGHT_ID =
      "com.google.devtools.depan.eclipse.actions.view.StrokeHighlight";

  // selections
  public static final String SUBLAYOUT_ID =
      "com.google.devtools.depan.eclipse.actions.view.SubLayout";
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

    // layouts
    if (id.equals(FRLAYOUT_ID)) {
      applyLayout(JungLayoutGenerator.FRLayoutBuilder);
    } else if (id.equals(SPRINGLAYOUT_ID)) {
      applyLayout(JungLayoutGenerator.SpringLayoutBuilder);
    } else if (id.equals(TREELAYOUT_ID))  {
      applyLayout(TreeLayoutGenerator.NewTreeLayoutBuilder);
    } else if (id.equals(RADIALLAYOUT_ID)) {
      applyLayout(TreeLayoutGenerator.NewRadialLayoutBuilder);

    // visualization options
    } else if (id.equals(ROOTHIGHLIGHT_ID)) {
      action.setChecked(togglePref(NodePreferencesIds.NODE_ROOT_HIGHLIGHT_ON, false));
    } else if (id.equals(STRETCHRATIO_ID)) {
      action.setChecked(togglePref(NodePreferencesIds.NODE_RATIO_ON, false));
    } else if (id.equals(SIZE_ID)) {
      action.setChecked(togglePref(NodePreferencesIds.NODE_SIZE_ON, false));
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      action.setChecked(togglePref(NodePreferencesIds.NODE_STROKE_HIGHLIGHT_ON, true));
    } else if (id.equals(SHAPE_ID)) {
      action.setChecked(togglePref(NodePreferencesIds.NODE_SHAPE_ON, true));

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
    String id = action.getId();
    boolean checked = false;
    if (id.equals(ROOTHIGHLIGHT_ID)) {
      checked = isChecked(NodePreferencesIds.NODE_ROOT_HIGHLIGHT_ON, false);
    } else if (id.equals(STRETCHRATIO_ID)) {
      checked = isChecked(NodePreferencesIds.NODE_RATIO_ON, false);
    } else if (id.equals(SIZE_ID)) {
      checked = isChecked(NodePreferencesIds.NODE_SIZE_ON, false);
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      checked = isChecked(NodePreferencesIds.NODE_STROKE_HIGHLIGHT_ON, true);
    } else if (id.equals(SHAPE_ID)) {
      checked = isChecked(NodePreferencesIds.NODE_SHAPE_ON, true);
    }
    action.setChecked(checked);
  }

  private void applyLayout(LayoutGenerator layout) {
    targetEditor.applyLayout(layout);
  }

  private boolean isChecked(String prefId, boolean defaultValue) {
    IEclipsePreferences node = PreferencesIds.getInstanceNode();
    boolean result = node.getBoolean(prefId, defaultValue);
    return result;
  }

  private boolean togglePref(String prefId, boolean defaultValue) {
    IEclipsePreferences node = PreferencesIds.getInstanceNode();
    boolean result = !node.getBoolean(prefId, defaultValue);
    node.putBoolean(prefId, result);
    return result;
  }
}
