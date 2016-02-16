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

package com.google.devtools.depan.java.eclipse;

import com.google.devtools.depan.eclipse.preferences.PreferencesIds;
import com.google.devtools.depan.eclipse.utils.Resources;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class JavaActivator extends AbstractUIPlugin {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID = "com.google.devtools.depan.java";

  /**
   * Bundle that is responsible for storing the resources for this plug-in.
   */
  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  /**
   * Prefix to use for preferences.
   */
  public static final String JAVA_PREF_PREFIX = PreferencesIds.PREFIX + "java_";

  // The shared instance
  private static JavaActivator plugin;

  /**
   * The constructor
   */
  public JavaActivator() {
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
  public static JavaActivator getDefault() {
    return plugin;
  }

  /////////////////////////////////////
  // Plugin Images 
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

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }

  private static ImageDescriptor getImageDescriptor(String path) {
    return Resources.getImageDescriptor(BUNDLE, path);
  }
}
