/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filesystem;

import com.google.devtools.depan.eclipse.utils.Resources;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * Provides the resources for this plug-in such as images.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public abstract class FileSystemResources {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID = "com.google.devtools.depan.filesystem";

  /**
   * Bundle that is responsible for storing the resources for this plug-in.
   */
  private static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  /**
   * {@link ImageDescriptor} for <code>DirectoryElement</code>s.
   */
  public static final ImageDescriptor IMAGE_DESC_DIRECTORY =
      getImageDescriptor("icons/eclipse/folder_obj.png");

  /**
   * {@link ImageDescriptor} for <code>FileElement</code>s.
   */
  public static final ImageDescriptor IMAGE_DESC_FILE =
      getImageDescriptor("icons/eclipse/file_obj.png");

  /**
   * {@link Image} object for <code>DirectoryElement</code>s.
   */
  public static final Image IMAGE_DIRECTORY = getImage(IMAGE_DESC_DIRECTORY);

  /**
   * {@link Image} object for <code>FileElement</code>s.
   */
  public static final Image IMAGE_FILE = getImage(IMAGE_DESC_FILE);

  private static ImageDescriptor getImageDescriptor(String path) {
    return Resources.getImageDescriptor(BUNDLE, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
