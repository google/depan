package com.google.devtools.depan.view_doc.eclipse;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ViewDocResources {

  public static final String PLUGIN_ID = "com.google.devtools.depan.view_doc";

  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  ViewDocResources() {
    // Prevent instantiation.
  }

}
