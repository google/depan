/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.awt.Color;

/**
 * this class is a "namespace" class with only static methods.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public abstract class Tools {

  /**
   * private constructor to prevent instantiation.
   */
  private Tools() { }

  public static String[] toString(Object[] objs, boolean lowercase) {
    String[] s = new String[objs.length];
    int i = 0;
    for (Object o : objs) {
      s[i++] = lowercase ? o.toString().toLowerCase() : o.toString();
    }
    return s;
  }

  public static String getRgb(Color color) {
    return "" + color.getRed() + "," + color.getGreen() + "," + color.getBlue();
  }

  /**
   * Convert the given string to a color. The string must have the form "R,G,B".
   * If the conversion fails, return a red color.
   *
   * @param key
   * @return the Color corresponding to the given string R,G,B
   */
  public static Color getRgb(String key) {
    RGB rgb = StringConverter.asRGB(key, new RGB(255, 0, 0));
    return new Color(rgb.red, rgb.green, rgb.blue);
  }

  public static void setBackgroundRecursively(
      org.eclipse.swt.graphics.Color color, Control control) {
    control.setBackground(color);
    if (control instanceof Composite) {
      for (Control child : ((Composite) control).getChildren()) {
        setBackgroundRecursively(color, child);
      }
    }
  }

  /**
   * Return an {@link Image} provided by the plugin handling the given node.
   * @param node
   * @return an {@link Image} for the node, or <code>null</code> if no image
   * was found.
   */
  public static Image getIcon(GraphNode node) {
    return SourcePluginRegistry.getImage(node);
  }
}
