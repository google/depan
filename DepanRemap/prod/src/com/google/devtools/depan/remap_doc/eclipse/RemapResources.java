package com.google.devtools.depan.remap_doc.eclipse;

import com.google.devtools.depan.platform.PlatformResources;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class RemapResources {

  public static final String PLUGIN_ID = "com.google.devtools.depan.view_doc";

  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  RemapResources() {
    // Prevent instantiation.
  }

  public static final String NAME_REFACTORING = "Refactoring";

  public static final ImageDescriptor IMAGE_DESC_REFACTORING = 
      getImageDescriptor("icons/refactoring.png");

  public static final Image IMAGE_REFACTORING = 
      getImage(IMAGE_DESC_REFACTORING);

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return PlatformResources.getImage(descriptor);
  }
}
