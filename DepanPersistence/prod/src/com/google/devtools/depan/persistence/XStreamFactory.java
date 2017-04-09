/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.persistence;

import com.google.devtools.depan.persistence.plugins.XStreamConfig;
import com.google.devtools.depan.persistence.plugins.XStreamConfigRegistry;
import com.google.devtools.depan.platform.plugin.PluginClassLoader;

import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import org.osgi.framework.Bundle;

import java.util.Collection;

/**
 * Generate {@code XStream} instances that are configured for DepAn.  The DepAn
 * configuration include support class loading from plugins, basic
 * {@code GraphModel}s and {@code GraphEdge}s, and all node and relations types
 * from all active plugins.
 * <p>
 * The {@link #build(boolean, XStreamConfig)} Factory method standardizes
 * the process to configure an {@link XStream} for a document type.
 * The supplied {@link XStreamConfig} parameter in that methods integrates
 * document and plugin contributions to the {@code XStream}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class XStreamFactory {

  private XStreamFactory() {
    // Prevent instantiation.
  }

  /**
   * Provide an XStream instance that is initialized to use the StAX XML
   * toolkit.  The returned instance will still need to be configured
   * for DepAn.
   * 
   * @return StAX configured XStream
   */
  public static XStream newStaxXStream() {
    XStream result = new XStream(new StaxDriver());
    return result;
  }

  /**
   * Provide a default XStream instance.  With XStream 3.1, this will normally
   * use the XPP XML toolkit.  The returned instance will still need to be
   * configured for DepAn.
   */
  public static XStream newBaseXStream() {
    XStream result = new XStream();
    return result;
  }

  public static XStream newXStream(boolean readable) {
    if (readable) {
      return newBaseXStream();
    }
    return newStaxXStream();
  }

  /**
   * Configure an XStream instance with all the specializations necessary
   * for plugin support (class loading), graphs (app-level) and all the
   * plugins graph elements.
   * 
   * @param xstream instance to configure
   */
  public static void configureXStream(XStream xstream) {
    XStreamConfigRegistry.config(xstream);
  }

  /**
   * Factory method to configure underlying {@link XStream} properly for
   * a desired document type.
   * 
   * This method integrates the document supplied {@link XStreamConfig}
   * with the plugin contributions from {@link XStreamConfigRegistry}.
   * The document supplied {@link XStreamConfig} values have priority,
   * and can override both the {@code XStream} options and class path from
   * the  {@link XStreamConfigRegistry}.
   * It is appropriate to use care with this mechanism.
   */
  public static ObjectXmlPersist build(
      boolean readable, XStreamConfig docConfig) {
    XStream xstream = XStreamFactory.newXStream(readable);
    XStreamFactory.configureXStream(xstream);
    docConfig.config(xstream);

    PluginClassLoader loader = buildLoader(docConfig);
    xstream.setClassLoader(loader);

    return new ObjectXmlPersist(xstream);
  }

  /**
   * In the returned class loader, bundles from the supplied
   * {@link XStreamConfig} parameter are searched ahead of the
   * plugins contributing to {@link XStreamConfigRegistry}.
   */
  private static PluginClassLoader buildLoader(XStreamConfig docConfig) {
    Collection<Bundle> loaders = Lists.newArrayList();
    loaders.addAll(docConfig.getDocumentBundles());
    loaders.addAll(XStreamConfigRegistry.getRegistryPluginBundles());
    return new PluginClassLoader(loaders);
  }
}
