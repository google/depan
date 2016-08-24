/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse;

import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditorInput;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class ViewDocResources {

  public static final String PLUGIN_ID = "com.google.devtools.depan.view_doc";

  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  ViewDocResources() {
    // Prevent instantiation.
  }

  /** Public for {@link ViewEditorInput} compliance.*/
  public static final ImageDescriptor IMAGE_DESC_VIEWDOC =
      getImageDescriptor("icons/view-editor.gif");

  private static final ImageDescriptor IMAGE_DESC_RELATIONPICKER =
      getImageDescriptor("icons/relpicker.png");

  private static final ImageDescriptor IMAGE_DESC_NODEEDITOR =
      getImageDescriptor("icons/nodeeditor.png");

  private static final ImageDescriptor IMAGE_DESC_SELECTIONEDITOR =
      getImageDescriptor("icons/selectioneditor.png");

  public static final Image IMAGE_SELECTIONEDITOR =
      getImage(IMAGE_DESC_SELECTIONEDITOR);

  public static final Image IMAGE_VIEWDOC =
      getImage(IMAGE_DESC_VIEWDOC);

  public static final Image IMAGE_RELATIONPICKER =
      getImage(IMAGE_DESC_RELATIONPICKER);

  public static final Image IMAGE_NODEEDITOR =
      getImage(IMAGE_DESC_NODEEDITOR);

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return PlatformResources.getImage(descriptor);
  }
}
