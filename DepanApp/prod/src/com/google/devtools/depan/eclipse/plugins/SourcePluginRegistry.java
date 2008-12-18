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

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.XmlPersistentObject;

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
 * <p>
 * It is also a static interface to transform {@link Element}s into useful
 * values such as colors, shapes, etc. The algorithm for this task is simple.
 * The plugin registry first look at the class of the given {@link Element}.
 * Then it selects the plugin providing the class, and call the right function
 * to that plugin.
 *
 * @author Yohann Coppel
 *
 */
public class SourcePluginRegistry {
  /**
   * Extension point name for sourceplugins
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.eclipse.sourceplugin";

  /**
   * Static instance. This class is a singleton.
   */
  private final static SourcePluginRegistry INSTANCE =
      new SourcePluginRegistry();

  /**
   * A list of all registered plugins entries. The key is the plugin id.
   */
  private Map<String, SourcePluginEntry> entries = null;

  /**
   * List of all registered plugins instances.
   */
  private List<SourcePlugin> pluginList = null;

  /**
   * A map to find the right {@link ElementTransformers} given a element's
   * class.
   */
  private Map<Class<? extends Element>, ElementTransformers> transformers;

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
   * @return a list of {@link SourcePluginEntry}es containing informations on
   * the registered plugins.
   */
  public static Collection<SourcePluginEntry> getEntries() {
    if (null == INSTANCE.entries) {
      INSTANCE.load();
    }
    return INSTANCE.entries.values();
  }

  /**
   * Get the SourcePluginEntry with the given ID.
   * @param id a SourcePluginEntry ID.
   * @return the corresponding SourcePluginEntry, or <code>null</code> if no
   * plugins has the given ID.
   */
  public static SourcePluginEntry getEntry(String id) {
    if (null == INSTANCE.entries) {
      INSTANCE.load();
    }
    if (INSTANCE.entries.containsKey(id)) {
      return INSTANCE.entries.get(id);
    }
    return null;
  }

  /**
   * @return a collection of SourcePlugin instances.
   */
  public static Collection<SourcePlugin> getInstances() {
    if (null == INSTANCE.pluginList) {
      INSTANCE.load();
    }
    return INSTANCE.pluginList;
  }

  /**
   * Load the plugins from the extension point, and fill the lists of entries.
   */
  private void load() {
    entries = Maps.newHashMap();
    pluginList = Lists.newArrayList();
    transformers = Maps.newHashMap();
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
          pluginList.add(p);
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
   * Get an {@link Image} for the corresponding {@link Element}. The image is
   * an icon for this type of elements, and is provided by the plugin handling
   * the given element.
   * @param element
   * @return an image, or <code>null</code> if no plugin handle this kind of
   * element.
   */
  public static Image getImage(Element element) {
    ElementTransformers t = INSTANCE.transformers.get(element.getClass());
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
    ElementTransformers t = INSTANCE.transformers.get(element.getClass());
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
    ElementTransformers t = INSTANCE.transformers.get(element.getClass());
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
    ElementTransformers t = INSTANCE.transformers.get(element.getClass());
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
    ElementTransformers t = INSTANCE.transformers.get(element.getClass());
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
    ElementTransformers t1 = INSTANCE.transformers.get(e1.getClass());
    ElementTransformers t2 = INSTANCE.transformers.get(e2.getClass());
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
    return INSTANCE.transformers.values();
  }

  /**
   * helper method to configure {@link XmlPersistentObject} with plugin
   * informations.
   */
  public static void setupXMLConfig() {
    // TODO Ideally, DepanCore should accept some of this responsibility
    // - perhaps an Extension point to receive serialization advisors.
    for (SourcePlugin p : SourcePluginRegistry.getInstances()) {
      XmlPersistentObject.addConfig(p.getXMLConfig());
    }
    if (null == INSTANCE.pluginClassLoader) {
      INSTANCE.pluginClassLoader =
          new PluginClassLoader(INSTANCE.entries.values());
    }
    XmlPersistentObject.addConfig(INSTANCE.pluginClassLoader);
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

  /**
   * Create the list of known Element kinds.  Ideally, this should use
   * ElementKindDescriptors obtained directly from the plugins.
   * 
   * @return collection of all element descriptors from all plugins
   */
  public static Collection<PrimitiveElementKindDescriptor>
      getElementKindDescriptors() {
    Collection<PrimitiveElementKindDescriptor> elementKinds = Lists.newArrayList();
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (Class<? extends Element> elementClass 
          : plugin.getElementClasses()) {
        elementKinds.add(
            new PrimitiveElementKindDescriptor(elementClass, plugin));
      }
    }
    return elementKinds;
  }

}
