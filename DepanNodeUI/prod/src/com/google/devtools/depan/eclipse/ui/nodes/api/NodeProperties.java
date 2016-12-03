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

package com.google.devtools.depan.eclipse.ui.nodes.api;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPluginRegistry;
import com.google.devtools.depan.model.Element;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;

/**
 * Provide access to common node properties without exposing other packages
 * in the plugin.  Properties are supplied by delegation to hidden classes.
 *
 * @author Yohann Coppel
 */
public class NodeProperties {
  /////////////////////////////////////
  // Static Transformer Methods

  /**
   * Get an {@link Image} for the corresponding {@link Element}. The image is
   * an icon for this type of elements, and is provided by the plugin handling
   * the given element.
   * @param element
   * @return an image, or <code>null</code> if no plugin handle this kind of
   * element.
   */
  public static Image getImage(Element element) {
    return NodeElementPluginRegistry.getImage(element);
  }

  /**
   * As {@link #getImage(Element)}, but return an {@link ImageDescriptor}.
   * @param element
   * @return an {@link ImageDescriptor}, or <code>null</code> if no plugin can
   * handle the given element.
   */
  public static ImageDescriptor getImageDescriptor(Element element) {
    return NodeElementPluginRegistry.getImageDescriptor(element);
  }

  /**
   * Return a {@link Color} for the given {@link Element}. The {@link Color} is
   * given by the plugin providing the type of element.
   * @param element
   * @return a {@link Color}, or <code>null</code> if no plugin can handle the
   * given element.
   */
  public static Color getColor(Element element) {
    return NodeElementPluginRegistry.getColor(element);
  }

  /**
   * Try to compare two elements. If the elements are provided by the same
   * plugin, use the result of the plugin's compare method. Otherwise, compare
   * alphabetically the two plugins' ids (so that elements given by a same
   * plugin are together in a list). If one element has no associated plugin,
   * it is sorted at the end.
   *
   * @param e1 first element
   * @param e2 second element
   * @return a number greater than 0 if e1 > e2, less than 0 if e1 < e2,
   * or 0 if they are equal.
   */
  public static int compare(Element e1, Element e2) {
    return NodeElementPluginRegistry.compare(e1, e2);
  }

  public static Integer getCategory(Element element) {
    return NodeElementPluginRegistry.getCategory(element);
  }
}
