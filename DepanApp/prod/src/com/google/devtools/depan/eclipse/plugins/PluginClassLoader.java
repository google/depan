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
import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;

import com.thoughtworks.xstream.XStream;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.List;

/**
 * A ClassLoader that can load classes provided by source plugin.
 * It is used by XStream to find the classes when deserializing objects.
 *
 * @author Yohann Coppel
 *
 */
public class PluginClassLoader extends ClassLoader implements Config {

  /**
   * List of bundles for each plugin. Used to load the classes.
   */
  List<Bundle> pluginBundles;

  /**
   * Construct the class loader. Get and save the {@link Bundle}s for each
   * plugin.
   *
   * @param collection
   */
  public PluginClassLoader(Collection<SourcePluginEntry> collection) {
    pluginBundles = Lists.newArrayList();
    for (SourcePluginEntry p : collection) {
      pluginBundles.add(Platform.getBundle(p.getId()));
    }
  }

  /**
   * {@inheritDoc}
   * Set the classloader of the given xstream to <code>this</code> object.
   */
  @Override
  public void config(XStream xstream) {
    xstream.setClassLoader(this);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    for (Bundle bundle: pluginBundles) {
      // try to load the class one plugin after the other. stop on the first
      // plugin providing the corresponding class.
      try {
        return bundle.loadClass(name);
      } catch (ClassNotFoundException e) {
      }
    }
    return super.loadClass(name);
  }
}
