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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class Resources {

  public static final String PLUGIN_ID = "com.google.devtools.depan";

  // icons
  public static final Image IMAGE_NODEEDITOR =
      Tools.getImageFromPath("icons/nodeeditor.png");
  public static final Image IMAGE_EDGEEDITOR =
      Tools.getImageFromPath("icons/edgeeditor.png");
  public static final Image IMAGE_SELECTIONEDITOR =
      Tools.getImageFromPath("icons/selectioneditor.png");
  public static final Image IMAGE_RELATIONPICKER =
      Tools.getImageFromPath("icons/relpicker.png");
  public static final Image IMAGE_HANDTOOL =
      Tools.getImageFromPath("icons/hand.png");
  public static final Image IMAGE_PICKTOOL =
    Tools.getImageFromPath("icons/arrow.png");
  public static final Image IMAGE_REFACTORING =
      Tools.getImageFromPath("icons/refactoring.png");
  public static final Image IMAGE_LAYOUT =
      Tools.getImageFromPath("icons/layout.png");
  public static final Image IMAGE_SUBLAYOUT =
      Tools.getImageFromPath("icons/sublayout.png");
  public static final Image IMAGE_ZOOM =
      Tools.getImageFromPath("icons/zoom.png");
  public static final Image IMAGE_INFOS =
      Tools.getImageFromPath("icons/infos.png");
  public static final Image IMAGE_COLLAPSE =
    Tools.getImageFromPath("icons/collapse.png");
  public static final Image IMAGE_EXPANDALL =
    Tools.getImageFromPath("icons/expandall.png");

  // editor names
  public static final String NAME_NODEEDITOR = "Node editor";
  public static final String NAME_EDGEEDITOR = "Edge editor";
  public static final String NAME_SELECTIONEDITOR = "Selection editor";
  public static final String NAME_RELATIONPICKERTOOL = "Relation picker";
  public static final String NAME_HANDTOOL = "Hand tool";
  public static final String NAME_PICKTOOL = "Picking tool";
  public static final String NAME_REFACTORING = "Refactoring tool";
  public static final String NAME_SUBLAYOUT = "Layouts / Sub-layouts";
  public static final String NAME_ZOOM = "Zoom / Stretch";
  public static final String NAME_INFORMATIONS = "Informations";
  public static final String NAME_COLLAPSE = "Collapse operations";

  public static final Image IMAGE_ON =
      Tools.getImageFromPath("icons/brkpi_obj.gif");
  public static final Image IMAGE_OFF =
      Tools.getImageFromPath("icons/brkpd_obj.gif");

  public static final Image IMAGE_DEFAULT =
    Tools.getImageFromPath("icons/sample.gif");
  public static final ImageDescriptor IMAGE_DESC_DEFAULT =
      ImageDescriptor.createFromImage(IMAGE_DEFAULT);

  // private constructor to prevent instantiation
  private Resources() { }

  public static Image getOnOff(boolean on) {
    return on ? IMAGE_ON : IMAGE_OFF;
  }

  public static ImageDescriptor getImageDescriptor(
      Bundle bundle, String path) {
    try {
      URL imageURL = bundle.getResource(path);
      return ImageDescriptor.createFromURL(imageURL);
    } catch (Exception e) {
      return IMAGE_DESC_DEFAULT;
    }
  }
}
