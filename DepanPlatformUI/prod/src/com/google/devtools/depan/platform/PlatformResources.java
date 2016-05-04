/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.platform;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class PlatformResources {

  public static final String APP_ID = "com.google.devtools.depan";

  public static final String PLUGIN_ID = APP_ID + ".platform";

  // icons
  public static final Image IMAGE_HANDTOOL =
      PlatformResources.getImageFromPath("icons/hand.png");
  public static final Image IMAGE_PICKTOOL =
      PlatformResources.getImageFromPath("icons/arrow.png");
  public static final Image IMAGE_COLLAPSE =
      PlatformResources.getImageFromPath("icons/collapse.png");
  public static final Image IMAGE_EXPANDALL =
      PlatformResources.getImageFromPath("icons/expandall.png");
  public static final Image IMAGE_AND =
      PlatformResources.getImageFromPath("icons/and.png");
  public static final Image IMAGE_OR =
      PlatformResources.getImageFromPath("icons/or.png");
  public static final Image IMAGE_XOR =
      PlatformResources.getImageFromPath("icons/xor.png");
  public static final Image IMAGE_NOT =
      PlatformResources.getImageFromPath("icons/not.png");

  public static final Image IMAGE_ON =
      PlatformResources.getImageFromPath("icons/brkpi_obj.gif");
  public static final Image IMAGE_OFF =
      PlatformResources.getImageFromPath("icons/brkpd_obj.gif");

  public static final Image IMAGE_DEFAULT =
      PlatformResources.getImageFromPath("icons/sample.gif");
  public static final ImageDescriptor IMAGE_DESC_DEFAULT =
      ImageDescriptor.createFromImage(IMAGE_DEFAULT);

  // private constructor to prevent instantiation
  private PlatformResources() { }

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

  public static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(
          PLUGIN_ID, path);
    }

  public static Image getImageFromPath(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(
        PLUGIN_ID, path).createImage();
  }
}
