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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface Tool {

  /**
   * Return a name for this tool, will be used in tooltips in the Tool list for
   * example.
   *
   * @return a name for this tool.
   */
  public String getName();

  /**
   * Provides an image used for the button representing this tool.
   *
   * @return an image used for the button representing this tool.
   */
  public Image getIcon();

  /**
   * Standard way to create the GUI with options for this tool.
   * <b>MAY</b> return {@code null} if this tool doesn't have any options.
   * This method is called once on each tool.
   *
   * @param parent parent composite.
   * @return the composite created, or <code>null</code> if this tool has no
   *         options.
   */
  public Control setupComposite(Composite parent);

  /**
   * Set the editor that is currently selected.
   * With a selected editor, the tool operates on the editors content.
   * Display changes may occur separately via notifications or may be
   * initiated directly by the tool
   * <p>
   * This method is called only when a new or different
   * {@link ViewEditor} is selected. Can be called with a {@code null}
   * argument if an editor is closed.
   *
   * @param viewEditor new {@link ViewEditor}.
   */
  public void setEditor(ViewEditor viewEditor);

  /**
   * Notify the tool that the given {@link ViewEditor} has been closed. Tool
   * should release any pointer on it.
   *
   * @param viewEditor the closed {@link ViewEditor}.
   */
  public void editorClosed(ViewEditor viewEditor);

  /**
   * Notify the tool when it is selected or when another tool is selected.
   * When a tool is selected, only the previous selected tool receive a
   * callback with <code>isSelected</code> as <code>false</code>.
   *
   * @param isSelected true if this tool have been selected, false, if another
   * tool have been selected.
   */
  public void selected(boolean isSelected);
}
