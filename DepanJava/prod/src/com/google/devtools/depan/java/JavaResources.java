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

import com.google.devtools.depan.eclipse.utils.Resources;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class JavaResources {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID = "com.google.devtools.depan.java";

  /**
   * Bundle that is responsible for storing the resources for this plug-in.
   */
  private static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

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
    return Resources.getImageDescriptor(BUNDLE, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
