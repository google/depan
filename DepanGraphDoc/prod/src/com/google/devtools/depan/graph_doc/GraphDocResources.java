package com.google.devtools.depan.graph_doc;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class GraphDocResources {

  /** Must match definition in MANIFEST.MF */
  public static final String PLUGIN_ID = "com.google.devtools.depan.graph_doc";

  public static final Bundle BUNDLE = Platform.getBundle(PLUGIN_ID);

  GraphDocResources() {
    // Prevent instantiation.
  }
}
