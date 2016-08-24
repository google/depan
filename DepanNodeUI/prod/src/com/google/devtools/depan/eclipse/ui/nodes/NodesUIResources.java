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

package com.google.devtools.depan.eclipse.ui.nodes;

import com.google.devtools.depan.platform.PlatformResources;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public final class NodesUIResources {

  public static final String PLUGIN_ID = "com.google.devtools.depan.nodes.ui";

  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  public static final ImageDescriptor IMAGE_DESC_EXPANDALL =
      getImageDescriptor("icons/expandall.png");

  public static final Image IMAGE_EXPANDALL =
      getImage(IMAGE_DESC_EXPANDALL);

  private NodesUIResources() {
    // Prevent instantiation.
  }

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return PlatformResources.getImage(descriptor);
  }
}
