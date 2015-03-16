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

package com.google.devtools.depan.eclipse.utils;

import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.model.Element;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.Map;

/**
 * A widget simplifying the process of switching from one DoubleEditor to
 * another when selecting a different type of Element.
 *
 * Maintains a list of {@link DoubleElementEditor} for each type of Element to
 * edit in a stackLayout (only one editor is visible at a time). The
 * {@link #setEditorFor(JavaElement)} methods then choose the correct one to
 * show. Also have a "NoEditor" panel, so that we can hide any editor when we
 * want.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class DoubleElementEditorChooser extends Composite {

  private StackLayout stackLayout;

  /**
   * A map from the class of Elements, to the associated
   * {@link DoubleElementEditor}.
   */
  private Map<Class<? extends ElementEditor>, DoubleElementEditor<?>> editors =
      Maps.newHashMap();

  /**
   * An empty Composite shown when we don't want any editor to be visible.
   */
  private Composite noEditor;

  /**
   * @param parent
   * @param style
   */
  @SuppressWarnings("unchecked")
  public DoubleElementEditorChooser(Composite parent, int style) {
    super(parent, style);
    stackLayout = new StackLayout();

    // Install editors for every Element type known to every plugin
    for (SourcePlugin p : SourcePluginRegistry.getInstances()) {
      // get the transformer for an Element to an ElementEditor
      ElementClassTransformer<Class<? extends ElementEditor>> transformer =
          p.getElementEditorProvider();

      // Install the editor for every known (and valid) class from the plugin
      for (Class<? extends Element> e : p.getElementClasses()) {
        // get the associated editor class
        Class<? extends ElementEditor> editorClass = transformer.transform(e);

        // Ignore classes that have no editor
        if (null != editorClass) {
          // instantiate the editor
          DoubleElementEditor editor =
              new DoubleElementEditor(this, SWT.NONE, editorClass);
          editors.put(editorClass, editor);
        }
      }
    }

    noEditor = new Composite(this, SWT.NONE);
    stackLayout.topControl = noEditor;

    this.setLayout(stackLayout);
  }

  /**
   * Choose the correct editor for the given element type.
   *
   * @param element the element we want to edit.
   */
  public void setEditorFor(Element element) {
    if (null == element) {
      setNoEditor();
      return;
    }
    Class<? extends ElementEditor> klass =
        SourcePluginRegistry.getEditor(element);
    stackLayout.topControl = editors.get(klass);
    this.layout();
  }

  /**
   * Remove any editor and display an empty Composite instead.
   */
  public void setNoEditor() {
    stackLayout.topControl = noEditor;
    this.layout();
  }
}
