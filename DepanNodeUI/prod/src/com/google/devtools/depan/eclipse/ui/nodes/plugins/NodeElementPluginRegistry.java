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

package com.google.devtools.depan.eclipse.ui.nodes.plugins;

import com.google.devtools.depan.eclipse.ui.nodes.NodesUILogger;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Map;

/**
 * Registry of known NodeElement definitions.  NodeElement contributions are
 * provided by plugins and supply the basic display properties for their
 * GraphNodes (Elements).  The data from all contributions is loaded for
 * convenient accesses.
 * 
 * <p>It is also a static interface to transform {@link Element}s into useful
 * values such as colors, shapes, etc. The algorithm for this task is simple.
 * The plugin registry first look at the class of the given {@link Element}.
 * Then it selects the plugin providing the class, and call the right function
 * to that plugin.
 *
 * @author Yohann Coppel
 */
public class NodeElementPluginRegistry
    extends ContributionRegistry<NodeElementPlugin> {

  /** Extension point name for node element behaviors. */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.eclipse.ui.nodes.node_element";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static NodeElementPluginRegistry INSTANCE = null;

  /////////////////////////////////////
  // Handle configuration elements for contribution

  static class Entry extends ContributionEntry<NodeElementPlugin>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected NodeElementPlugin createInstance() throws CoreException {
      return (NodeElementPlugin) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * A map to find the right {@link NodeElementTransformers} given a element's
   * class.
   */
  private final Map<Class<? extends Element>, NodeElementTransformers>
      transformers = Maps.newHashMap();

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private NodeElementPluginRegistry() {
    super();
  }


  @Override
  protected ContributionEntry<NodeElementPlugin> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    NodesUILogger.LOG.error(
        "NodeElement registry load failure for {}", entryId, err);
  }

  /////////////////////////////////////
  // Hook method implementations

  @Override
  protected void installContribution(String entryId, NodeElementPlugin plugin) {
    NodeElementTransformers xform = new NodeElementTransformers(
        entryId,
        plugin.getElementImageProvider(),
        plugin.getElementImageDescriptorProvider(),
        plugin.getElementColorProvider(),
        plugin.getElementCategoryProvider(),
        plugin.getElementSorter());
    for (Class<? extends Element> type : plugin.getElementClasses()) {
      transformers.put(type, xform);
    }
  }

  /////////////////////////////////////
  // Static NodeElement Methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  private static synchronized NodeElementPluginRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new NodeElementPluginRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  /////////////////////////////////////
  // Static Transformer Methods

  /**
   * Get the correct transformer for this element.  This method ensures correct
   * allocation of the singleton instance.
   */
  private static NodeElementTransformers getTransformers(Element element) {
    NodeElementTransformers t = getInstance().transformers.get(element.getClass());
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
    NodeElementTransformers t = getTransformers(element);
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
    NodeElementTransformers t = getTransformers(element);
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
    NodeElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.color.transform(element);
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
    NodeElementTransformers t1 = getTransformers(e1);
    NodeElementTransformers t2 = getTransformers(e2);

    // If either one is empty, that element goes at the end.
    if (null == t1) {
      return -1;
    }
    if (null == t2) {
      return 1;
    }

    // both elements are from the same plugin, we can easily compare them
    if (t1 == t2) {
      return t1.sorter.compare(e1, e2);
    }

    // Otherwise order by underlying plugin
    return t1.pluginId.compareTo(t2.pluginId);
  }

  public static Integer getCategory(Element element) {
    NodeElementTransformers t = getTransformers(element);
    if (null != t) {
      return t.category.transform(element);
    }
    return null;
  }
}
