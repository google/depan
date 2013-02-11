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

package com.google.devtools.depan.eclipse.actions;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.JungLayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.TreeLayoutGenerator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ViewEditorActionDelegate implements IEditorActionDelegate {

  private ViewEditor targetEditor = null;

  // layouts
  public static final String FRLAYOUT_ID =
    "com.google.devtools.depan.eclipse.actions.view.SetFRLayout";
  public static final String SPRINGLAYOUT_ID =
    "com.google.devtools.depan.eclipse.actions.view.SetSpringLayout";
  public static final String TREELAYOUT_ID =
    "com.google.devtools.depan.eclipse.actions.view.SetTreeLayout";
  public static final String RADIALLAYOUT_ID =
    "com.google.devtools.depan.eclipse.actions.view.SetRadialLayout";

  // renderers
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

  @Override()
  public void setActiveEditor(IAction action, IEditorPart editor) {
    if (editor instanceof ViewEditor) {
      this.targetEditor = (ViewEditor) editor;
      updateState(action);
    } else {
      editor = null;
    }
  }

  private void updateState(IAction action) {
    String id = action.getId();
    View renderer = targetEditor.getRenderer();
    boolean checked = false;
    if (id.equals(ROOTHIGHLIGHT_ID)) {
      checked = renderer.getNodeColor().getSeedColoring();
    } else if (id.equals(STRETCHRATIO_ID)) {
      checked = renderer.getNodeSize().getRatio();
    } else if (id.equals(SIZE_ID)) {
      checked = renderer.getNodeSize().getResize();
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      checked = renderer.getNodeStroke().isActivated();
    } else if (id.equals(SHAPE_ID)) {
      checked = renderer.getNodeShape().getShapes();
    }
    action.setChecked(checked);
  }

  @Override()
  public void run(IAction action) {
    if (targetEditor == null) {
      return;
    }
    String id = action.getId();
    View view = targetEditor.getRenderer();

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
      action.setChecked(view.getNodeColor().toggleSeedColoring());
    } else if (id.equals(STRETCHRATIO_ID)) {
      action.setChecked(view.getNodeSize().toggleRatio());
    } else if (id.equals(SIZE_ID)) {
      action.setChecked(view.getNodeSize().toggleResize());
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      action.setChecked(view.getNodeStroke().toggle());
    } else if (id.equals(SHAPE_ID)) {
      action.setChecked(view.getNodeShape().toggleShapes());

    // others
    } else if (id.equals(SCREENSHOT_ID)) {
      targetEditor.takeScreenshot();
    } else if (id.equals(SELECT_ALL)) {
      targetEditor.selectAllNodes();
    }
  }

  private void applyLayout(LayoutGenerator layout) {
    targetEditor.applyLayout(layout);
  }

  @Override()
  public void selectionChanged(IAction action, ISelection selection) {
  }
}
