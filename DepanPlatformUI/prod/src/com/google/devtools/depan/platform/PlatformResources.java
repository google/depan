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

import java.text.MessageFormat;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class PlatformResources {

  public static final String APP_ID = "com.google.devtools.depan";

  public static final String PLUGIN_ID = "com.google.devtools.depan.platform.ui";

  // icons

  public static final ImageDescriptor IMAGE_DESC_AND =
      getImageDescriptor("icons/and.png");
  public static final ImageDescriptor IMAGE_DESC_OR =
      getImageDescriptor("icons/or.png");
  public static final ImageDescriptor IMAGE_DESC_XOR =
      getImageDescriptor("icons/xor.png");
  public static final ImageDescriptor IMAGE_DESC_NOT =
      getImageDescriptor("icons/not.png");
  public static final ImageDescriptor IMAGE_DESC_DEFAULT =
      getImageDescriptor("icons/sample.gif");
  public static final ImageDescriptor IMAGE_DESC_LIBRARY_OBJ =
      getImageDescriptor("icons/library_obj.png");

  public static final Image IMAGE_HANDTOOL =
      getImage(getImageDescriptor("icons/hand.png"));
  public static final Image IMAGE_PICKTOOL =
      getImage(getImageDescriptor("icons/arrow.png"));
  public static final Image IMAGE_COLLAPSE =
      getImage(getImageDescriptor("icons/collapse.png"));
  public static final Image IMAGE_EXPANDALL =
      getImage(getImageDescriptor("icons/expandall.png"));
  public static final Image IMAGE_AND =
      getImage(IMAGE_DESC_AND);
  public static final Image IMAGE_OR =
      getImage(IMAGE_DESC_OR);
  public static final Image IMAGE_XOR =
      getImage(IMAGE_DESC_XOR);
  public static final Image IMAGE_NOT =
      getImage(IMAGE_DESC_NOT);

  public static final Image IMAGE_ON =
      getImage(getImageDescriptor("icons/brkpi_obj.gif"));
  public static final Image IMAGE_OFF =
      getImage(getImageDescriptor("icons/brkpd_obj.gif"));

  public static final Image IMAGE_DEFAULT =
      getImage(IMAGE_DESC_DEFAULT);

  // private constructor to prevent instantiation
  private PlatformResources() { }

  public static Image getOnOff(boolean on) {
    return on ? IMAGE_ON : IMAGE_OFF;
  }

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(
          PLUGIN_ID, path);
    }

  public static Image getImage(ImageDescriptor descriptor) {
    try {
      return descriptor.createImage();
    } catch (RuntimeException errAny) {
      String msg = MessageFormat.format(
          "Unable to open image from descriptor {0}", descriptor);
      PlatformLogger.LOG.error(msg , errAny);
    }

    return null;
  }
}
