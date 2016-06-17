package com.google.devtools.depan.view_doc.eclipse;

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

  public static final Image IMAGE_VIEWDOC =
      getImage(IMAGE_DESC_VIEWDOC);

  private static final ImageDescriptor IMAGE_DESC_RELATIONPICKER =
      getImageDescriptor("icons/relpicker.png");

  public static final Image IMAGE_RELATIONPICKER =
      getImage(IMAGE_DESC_RELATIONPICKER);

  private static final ImageDescriptor IMAGE_DESC_NODEEDITOR =
      getImageDescriptor("icons/nodeeditor.png");

  public static final Image IMAGE_NODEEDITOR =
      getImage(IMAGE_DESC_NODEEDITOR);

  private static ImageDescriptor getImageDescriptor(String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  private static Image getImage(ImageDescriptor descriptor) {
    return descriptor.createImage();
  }
}
