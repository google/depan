/*
 * Copyright 2008 Yohann R. Coppel
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

package com.google.devtools.depan.eclipse.plugins;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;

import com.thoughtworks.xstream.XStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
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
public class SourcePluginRegistry {
  /**
   * Extension point name for sourceplugins
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.eclipse.sourceplugin";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static SourcePluginRegistry INSTANCE = null;

  /**
   * A list of all registered plugins entries. The key is the plugin id.
   */
  private final Map<String, SourcePluginEntry> entries = Maps.newHashMap();

  /**
   * List of all registered plugins instances.
   */
  private final Map<SourcePlugin, String> pluginToId = Maps.newHashMap();

  /**
   * A map to find the right {@link ElementTransformers} given a element's
   * class.
   */
  private final Map<Class<? extends Element>, ElementTransformers> transformers =
      Maps.newHashMap();

  /**
   * A PluginClassLoader that can load classes provided by a plugin.
   */
  private PluginClassLoader pluginClassLoader = null;

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private SourcePluginRegistry() {
  }

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized SourcePluginRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new SourcePluginRegistry();
      INSTANCE.load();
    }
    return INSTANCE;
  }

  /**
   * @return a list of {@link SourcePluginEntry}es containing informations on
   * the registered plugins.
   */
  public static Collection<SourcePluginEntry> getEntries() {
    return getInstance().entries.values();
  }

  /**
   * Get the SourcePluginEntry with the given ID from the singleton.
   * @param id a SourcePluginEntry ID.
   * @return the corresponding SourcePluginEntry, or <code>null</code> if no
   * plugins has the given ID.
   */
  public static SourcePluginEntry getEntry(String id) {
    return getInstance().getSourcePluginEntry(id);
  }

  /**
   * Get the SourcePluginEntry with the given ID.
   * @param id a SourcePluginEntry ID.
   * @return the corresponding SourcePluginEntry, or <code>null</code> if no
   * plugins has the given ID.
   */
  public SourcePluginEntry getSourcePluginEntry(String id) {
    if (entries.containsKey(id)) {
      return entries.get(id);
    }
    return null;
  }

  /**
   * Provide the {@link SourcePlugin} allocated for the plugin id.
   * 
   * @param pluginId key for locating {@code SourcePlugin} instance
   * @return requested {@code SourcePlugin} instance
   * @throws IllegalArgumentException for unknown {@code pluginId}s
   */
  public static SourcePlugin getSourcePlugin(String pluginId) {
    try {
      return getEntry(pluginId).getInstance();
    } catch (CoreException errCore) {
      throw new IllegalArgumentException(
          "Unrecognized plugin id " + pluginId, errCore);
    }
  }

  /**
   * Convert an array of analyzer plugin ids into a list of the actual
   * plugins.
   */
  public static List<SourcePlugin> buildPluginList(String... pluginIds) {
    List<SourcePlugin> result =
        Lists.newArrayListWithExpectedSize(pluginIds.length);
    for (String pluginId : pluginIds) {
      result.add(getSourcePlugin(pluginId));
    }
    return result;
  }

  public String getPluginId(SourcePlugin plugin) {
    return pluginToId.get(plugin);
  }

  /**
   * @return a collection of SourcePlugin instances.
   */
  public static Collection<SourcePlugin> getInstances() {
    return getInstance().pluginToId.keySet();
  }

  /**
   * Load the plugins from the extension point, and fill the lists of entries.
   */
  private void load() {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint point = registry.getExtensionPoint(EXTENTION_POINT);
    // for each extension
    for (IExtension extension: point.getExtensions()) {
      // ... and for each elements
      for (IConfigurationElement element :
          extension.getConfigurationElements()) {
        // obtain an object on the entry
        SourcePluginEntry entry = new SourcePluginEntry(element);
        entries.put(entry.getId(), entry);
        try {
          // try to instantiate the plugin
          SourcePlugin p = entry.getInstance();
          // if the instantiation succeed, add the plugin to the list
          pluginToId.put(p, entry.getId());
          // create the ElementTransformer for this plugin
          ElementTransformers t = new ElementTransformers(entry,
              p.getElementImageProvider(),
              p.getElementImageDescriptorProvider(),
              p.getElementColorProvider(),
              p.getElementShapeProvider(),
              p.getElementEditorProvider(),
              p.getElementSorter());
          // associate each possible class of elements to the element
          // transformer
          for (Class<? extends Element> c : p.getElementClasses()) {
            transformers.put(c, t);
          }
        } catch (CoreException e) {
          // plugin loading (instantiating) failed
          e.printStackTrace();
        }
      }
    }
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
   * Get an {@link Image} for the corresponding {@link Element}. The image is
   * an icon for this type of elements, and is provided by the plugin handling
   * the given element.
   * @param element
   * @return an image, or <code>null</code> if no plugin handle this kind of
   * element.
   */
  public static Image getImage(Element element) {
    ElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.image.transform(element);
    }
    return null;
  }

  /**
   * As {@link #getImage(Element)}, but return an {@link ImageDescriptor}.
   * @param element
   * @return an {@link ImageDescriptor}, or <code>null</code> if no plugin can
   * handle the given element.
   */
  public static ImageDescriptor getImageDescriptor(Element element) {
    ElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.imageDescriptor.transform(element);
    }
    return null;
  }

  /**
   * Return a {@link Color} for the given {@link Element}. The {@link Color} is
   * given by the plugin providing the type of element.
   * @param element
   * @return a {@link Color}, or <code>null</code> if no plugin can handle the
   * given element.
   */
  public static Color getColor(Element element) {
    ElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.color.transform(element);
    }
    return null;
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

  /**
   * Return a class of {@link ElementEditor} corresponding to the given
   * {@link Element}.
   * @param element
   * @return a class of {@link ElementEditor}, or <code>null</code> if no plugin
   * can handle the given element.
   */
  public static Class<? extends ElementEditor> getEditor(Element element) {
    ElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.editor.transform(element.getClass());
    }
    return null;
  }

  /**
   * Try to compare two elements. If the elements are provided by the same
   * plugin, use the result of the plugin's compare method. Otherwise, compare
   * alphabetically the two plugins' ids (so that elements given by a same
   * plugin are together in a list). If one element has no associated plugin,
   * it is sorted at the end.
   *
   * @param e1 first element
   * @param e2 second element
   * @return a number greater than 0 if e1 > e2, less than 0 if e1 < e2,
   * or 0 if they are equal.
   */
  public static int compare(Element e1, Element e2) {
    ElementTransformers t1 = getTransformers(e1);
    ElementTransformers t2 = getTransformers(e2);
    if ((null != t1) && (null != t2)) {
      if (t1 == t2) {
        // both elements are from the same plugin, we can easily compare them:
        return t1.sorter.compare(e1, e2);
      } else {
        return t1.plugin.getId().compareTo(t2.plugin.getId());
      }
    }
    // if one of the two elements have no plugin, sort it at the end.
    return t1 == null ? -1 : 1;
  }

  /**
   * @return a collection of {@link ElementTransformers} registered by this
   * plugin.
   */
  public static Collection<ElementTransformers> getTransformers() {
    return getInstance().transformers.values();
  }

  private static synchronized void ensurePluginClassLoader() {
    if (null == getInstance().pluginClassLoader) {
      INSTANCE.pluginClassLoader =
          new PluginClassLoader(INSTANCE.entries.values());
    }
  }

  /**
   * Configure an {@code XStream} instance with all the plug specific details.
   * 
   * @param xstream instance to configure
   */
  public static void configXmlPersist(XStream xstream) {
    for (SourcePlugin p : SourcePluginRegistry.getInstances()) {
      p.getXMLConfig().config(xstream);
    }

    ensurePluginClassLoader();
    getInstance().pluginClassLoader.config(xstream);
  }

  /**
   * @return a list of existing relations in all registered plugins.
   */
  public static Collection<? extends Relation> getRelations() {
    Collection<Relation> relations = Lists.newArrayList();
    for (SourcePlugin p : SourcePluginRegistry.getInstances()) {
      relations.addAll(p.getRelations());
    }
    return relations;
  }
}
