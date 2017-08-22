/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.Map;

/**
 * A registry for all the sourceplugins connected to DepAn App. Source plugins
 * are plugin that provides new sources of dependencies. For instance, one
 * source plugin could provide Java dependencies, another one C/C++
 * dependencies, etc.
 * 
 * <p>It is also a static interface to transform {@link Element}s into useful
 * values such as colors, shapes, etc. The algorithm for this task is simple.
 * The plugin registry first look at the class of the given {@link Element}.
 * Then it selects the plugin providing the class, and call the right function
 * to that plugin.
 *
 * @author Yohann Coppel
 */
public class JoglPluginRegistry
    extends ContributionRegistry<JoglPlugin> {

  /**
   * Extension point name for sourceplugins
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.view_doc.eclipse.ui.joglplugin";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static JoglPluginRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<JoglPlugin>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected JoglPlugin createInstance() throws CoreException {
      return (JoglPlugin) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * List of all registered plugins instances.
   */
  private final Map<JoglPlugin, String> pluginToId = Maps.newHashMap();

  /**
   * A map to find the right {@link ElementTransformers} given a element's
   * class.
   */
  private final Map<Class<? extends Element>, ElementTransformers> transformers =
      Maps.newHashMap();

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private JoglPluginRegistry() {
  }

  @Override
  protected ContributionEntry<JoglPlugin> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    ViewDocLogger.LOG.error(
        "View OGL registry load failure for {}", entryId, err);
  }

  /////////////////////////////////////
  // Hook method implementations

  @Override
  protected void installContribution(String entryId, JoglPlugin plugin) {
    // if the instantiation succeed, add the plugin to the list
    pluginToId.put(plugin, entryId);
    // create the ElementTransformer for this plugin
    ElementTransformers t = new ElementTransformers(
        plugin.getElementShapeProvider());
    // associate each possible class of elements to the element
    // transformer
    for (Class<? extends Element> c : plugin.getElementClasses()) {
      transformers.put(c, t);
    }
  }

  /////////////////////////////////////
  // Static NodeElement Methods

  /**
   * Provide the {@code JoglPluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized JoglPluginRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new JoglPluginRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  /**
   * Get the correct transformer for this element.  This method ensure correct
   * allocation of the singleton instance.
   */
  private static ElementTransformers getTransformers(Element element) {
    ElementTransformers t = getInstance().transformers.get(element.getClass());
    return t;
  }

  /**
   * Return a {@link GLEntity} associated to the given {@link Element}. The
   * result is provided by the right plugin.
   * @param element
   * @return a {@link GLEntity}, or <code>null</code> if no plugin can handle
   * the given element.
   */
  public static GLEntity getShape(Element element) {
    ElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.shape.transform(element);
    }
    return null;
  }
}
