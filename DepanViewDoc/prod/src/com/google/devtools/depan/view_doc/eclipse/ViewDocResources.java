package com.google.devtools.depan.view_doc.eclipse;

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

  private static final ImageDescriptor IMAGE_DESC_RELATIONPICKER =
      getImageDescriptor("icons/relpicker.png");

  public static Image IMAGE_RELATIONPICKER =
      getImage(IMAGE_DESC_RELATIONPICKER);

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
