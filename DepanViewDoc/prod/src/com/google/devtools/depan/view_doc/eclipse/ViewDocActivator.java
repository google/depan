package com.google.devtools.depan.view_doc.eclipse;

import com.google.devtools.depan.eclipse.visualization.JoglLoader;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Install features needed by the View Document plugin.
 * 
 * Most importantly, install an Eclipse/OSGi-aware class loader for JOGL.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewDocActivator implements BundleActivator {

  @Override
  public void start(BundleContext context) throws Exception {
    JoglLoader.prepareJoglLoader();
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }
}
