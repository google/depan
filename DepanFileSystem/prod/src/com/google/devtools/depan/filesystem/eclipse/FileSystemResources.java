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

package com.google.devtools.depan.filesystem.eclipse;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * Provides the resources for this plug-in such as images.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public abstract class FileSystemResources {
  /**
   * Plug-in ID used to identify this plug-in.
   */
  public static final String PLUGIN_ID = "com.google.devtools.depan.filesystem";

  private static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  /**
   * {@link ImageDescriptor} for <code>DirectoryElement</code>s.
   */
  public static final ImageDescriptor IMAGE_DESC_DIRECTORY =
      getImageDescriptor("icons/eclipse/packagefolder_obj.png");

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

  /**
   * Returns the <code>ImageDescriptor</code> from the file on the given path.
   *
   * @param path Path of the file to be used to create the
   * <code>ImageDescriptor</code>.
   * @return <code>ImageDescriptor</code> at the specified path; or default
   * <code>ImageDescriptor</code> if an <code>ImageDescriptor</code> cannot be
   * created from the file with the given path.
   */
  protected static ImageDescriptor getImageDescriptor(String path) {
    try {
      URL imageURL = getResource(path);
      return ImageDescriptor.createFromURL(imageURL);
    } catch (Exception e) {
      return com.google.devtools.depan.eclipse.utils.Resources
          .IMAGE_DESC_DEFAULT;
    }
  }

  /**
   * Returns the URL for the given path.
   *
   * @param path Path of the resource.
   * @return URL for the given path.
   */
  protected static URL getResource(String path) {
    return BUNDLE.getResource(path);
  }

  /**
   * Creates and returns the <code>Image</code> from the given
   * <code>ImageDescriptor</code>.
   *
   * @param descriptor <code>ImageDescriptor</code> which is required to create
   * the <code>Image</code>.
   * @return <code>Image</code> created from the given
   * <code>ImageDescriptor</code>.
   */
  protected static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
