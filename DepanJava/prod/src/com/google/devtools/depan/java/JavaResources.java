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

package com.google.devtools.depan.java;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public abstract class JavaResources {

  public static final String PLUGIN_ID = "com.google.devtools.depan.java";

  private static Bundle bundle = Platform.getBundle(PLUGIN_ID);

  // icons
  public static final ImageDescriptor IMAGE_DESC_FIELD =
      getImageDescriptor("icons/eclipse/field_public_obj.png");
  public static final ImageDescriptor IMAGE_DESC_INTERFACE =
      getImageDescriptor("icons/eclipse/int_obj.png");
  public static final ImageDescriptor IMAGE_DESC_METHOD =
      getImageDescriptor("icons/eclipse/methpub_obj.png");
  public static final ImageDescriptor IMAGE_DESC_PACKAGE =
      getImageDescriptor("icons/eclipse/package_obj.png");
  public static final ImageDescriptor IMAGE_DESC_TYPE =
      getImageDescriptor("icons/eclipse/class_obj.png");

  public static final Image IMAGE_FIELD =
      getImage(IMAGE_DESC_FIELD);
  public static final Image IMAGE_INTERFACE =
      getImage(IMAGE_DESC_INTERFACE);
  public static final Image IMAGE_METHOD =
      getImage(IMAGE_DESC_METHOD);
  public static final Image IMAGE_PACKAGE =
      getImage(IMAGE_DESC_PACKAGE);
  public static final Image IMAGE_TYPE =
      getImage(IMAGE_DESC_TYPE);

  private static ImageDescriptor getImageDescriptor(String path) {
    try {
      URL imageURL = bundle.getResource(path);
      return ImageDescriptor.createFromURL(imageURL);
    } catch (Exception e) {
      return com.google.devtools.depan.eclipse.utils.Resources
          .IMAGE_DESC_DEFAULT;
    }
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
