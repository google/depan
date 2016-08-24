/*
 * Copyright 2016 The Depan Project Authors
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

import com.google.devtools.depan.platform.PlatformResources;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class FileSystemActivator extends AbstractUIPlugin {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID =
      "com.google.devtools.depan.filesystem.ui";

  /**
   * Prefix to use for preferences.
   */
  // public static final String FS_PREF_PREFIX = PreferencesIds.PREFIX + "fs_";
  public static final String FS_PREF_PREFIX = "fs_";

  // The shared instance
  private static FileSystemActivator plugin;

  /**
   * The constructor
   */
  public FileSystemActivator() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static FileSystemActivator getDefault() {
    return plugin;
  }

  /////////////////////////////////////
  // Plugin Images 

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
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return PlatformResources.getImage(descriptor);
  }
}
