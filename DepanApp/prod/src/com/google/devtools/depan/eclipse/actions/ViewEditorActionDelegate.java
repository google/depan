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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.visualization.View;
import com.google.devtools.depan.eclipse.visualization.layout.Layouts;
import com.google.devtools.depan.eclipse.visualization.ogl.RenderingPipe;

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

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IEditorActionDelegate
   *      #setActiveEditor(org.eclipse.jface.action.IAction,
   *      org.eclipse.ui.IEditorPart)
   */
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
    RenderingPipe t = targetEditor.getView().getRenderingPipe();
    boolean checked = false;
    if (id.equals(ROOTHIGHLIGHT_ID)) {
      checked = t.getNodeColors().getSeedColoring();
    } else if (id.equals(STRETCHRATIO_ID)) {
      checked = t.getNodeSize().getRatio();
    } else if (id.equals(SIZE_ID)) {
      checked = t.getNodeSize().getResize();
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      checked = t.getNodeStroke().isActivated();
    } else if (id.equals(SHAPE_ID)) {
      checked = t.getNodeShape().getShapes();
    }
    action.setChecked(checked);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run(IAction action) {
    if (targetEditor == null) {
      return;
    }
    String id = action.getId();
    View view = targetEditor.getView();
    RenderingPipe transformers = view.getRenderingPipe();

    // layouts
    if (id.equals(FRLAYOUT_ID)) {
      applyLayout(Layouts.FRLayout);
    } else if (id.equals(SPRINGLAYOUT_ID)) {
      applyLayout(Layouts.SpringLayout);
    } else if (id.equals(TREELAYOUT_ID))  {
      applyLayout(Layouts.UniversalTreeLayout);
    } else if (id.equals(RADIALLAYOUT_ID)) {
      applyLayout(Layouts.UniversalRadialLayout);

    // visualization options
    } else if (id.equals(ROOTHIGHLIGHT_ID)) {
      action.setChecked(transformers.getNodeColors().toggleSeedColoring());
    } else if (id.equals(STRETCHRATIO_ID)) {
      action.setChecked(transformers.getNodeSize().toggleRatio());
    } else if (id.equals(SIZE_ID)) {
      action.setChecked(transformers.getNodeSize().toggleResize());
    } else if (id.equals(STROKEHIGHLIGHT_ID)) {
      action.setChecked(transformers.getNodeStroke().toggle());
    } else if (id.equals(SHAPE_ID)) {
      action.setChecked(transformers.getNodeShape().toggleShapes());

    // ohters
    } else if (id.equals(SCREENSHOT_ID)) {
      takeScreenshot(view);
    } else if (id.equals(SELECT_ALL)) {
      selectAll(view);
    }
  }

  /**
   * Sets the selection state of all nodes in this view to on.
   *
   * @param view The view which is currently active.
   */
  private void selectAll(View view) {
    view.selectAll();
  }

  /**
   * Take a screenshot of the given view. Ask the user a filename, and use
   * this filename to determine which type of file format has to be used.
   * PNG format is used as default.
   *
   * @param view the view to capture.
   */
  private void takeScreenshot(View view) {
    // make the screenshot first, so that the overlapping file selection window
    // does not Interfere with the process of taking the screenshot
    // (apparently, otherwise, it does)
    BufferedImage screenshot = view.getGLPanel().takeScreenshot();

    // ask the user a filename where to save the screenshot
    FileDialog fd = new FileDialog(
        targetEditor.getSite().getShell(), SWT.SAVE);
    fd.setText("Save Screenshot:");
    String[] filterExt = { "*.png", "*.jpg", "*.gif", "*.bmp", "*.*" };
    fd.setFilterExtensions(filterExt);
    String selected = fd.open();

    IActionBars bars = targetEditor.getEditorSite().getActionBars();

    // user canceled operation. Print a message in the status line, and return.
    if (null == selected) {
      bars.getStatusLineManager().setErrorMessage(
          "To take a screenshot, you must specify a filename.");
      return;
    }

    // check if the file has an extension. otherwise, use .png as default
    // extension.
    if (selected.lastIndexOf('.') == -1) {
      selected = selected+".png";
    }

    try {
      // finally, write the image on a file.
      ImageIO.write(screenshot, selected.substring(
          selected.lastIndexOf('.')+1), new File(selected));
      bars.getStatusLineManager().setMessage("Image saved to "+selected);
    } catch (IOException e) {
      e.printStackTrace();
      bars.getStatusLineManager().setErrorMessage(
          "Error while saving screenshot");
    }
  }

  private void applyLayout(Layouts layout) {
    targetEditor.getView().applyLayout(layout);
    targetEditor.setDirtyState(true);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IActionDelegate
   *      #selectionChanged(org.eclipse.jface.action.IAction,
   *      org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IAction action, ISelection selection) {
  }

}

