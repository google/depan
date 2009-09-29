/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.eclipse.persist;

import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Generate {@code XStream} instances that are configured for DepAn.  The DepAn
 * configuration include support class loading from plugins, basic
 * {@code GraphModel}s and {@code GraphEdge}s, and all node and relations types
 * from all active plugins.
 * <p>
 * If you need custom configuration of an {@code XStream} instance, you can
 * construct it yourself and then configure it with
 * {@link #configureXStream(XStream)}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class XStreamFactory {

  /**
   * Sharable {@code XStream}, suitable for persistence of {@code GraphModel}s.
   */
  private static XStream sharedGraphXStream;

  /**
   * Sharable {@code XStream}, suitable for persistence with references to
   * {@code GraphModel} elements.
   */
  private static XStream sharedRefXStream;

  /**
   * Prevent instantiation of this namespace class
   */
  private XStreamFactory() {
  }

  /**
   * Interface for objects that need to configure properties of the
   * {@code XStream} instances.
   */
  public interface Config {
    void config(XStream xstream);
  }

  /**
   * Provide a shared XStream instance that is initialized to use the StAX
   * XML parser and to persist {@code GraphModel}s.  This can be used to avoid
   * expensive configuration processes of separate XStreams, but no added
   * configuration changes should be made.
   * 
   * @return XStream configured for persistence of {@code GraphModel}s.
   */
  public static synchronized XStream getSharedGraphXStream() {
    if (null == sharedGraphXStream) {
      sharedGraphXStream = newStaxXStream();
      configureGraphXStream(sharedGraphXStream);
    }
    return sharedGraphXStream;
  }

  /**
   * Provide a shared XStream instance that is initialized to use the StAX
   * XML parser and to persist references to {@code GraphModel} elements..
   * This can be used to avoid expensive configuration processes of separate
   * XStreams, but no added configuration changes should be made.
   * 
   * @return XStream configured for references to {@code GraphModel} elements.
   */
  public static synchronized XStream getSharedRefXStream() {
    if (null == sharedRefXStream) {
      sharedRefXStream = newStaxXStream();
      configureRefXStream(sharedRefXStream);
    }
    return sharedRefXStream;
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
  public static void configureGraphXStream(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    GraphElements.GRAPH_XML_PERSIST.config(xstream);
    SourcePluginRegistry.configXmlPersist(xstream);
  }

  /**
   * Configure an XStream instance with all the specializations necessary
   * for references to {@code GraphModel} elements.
   * 
   * @param xstream instance to configure
   */
  public static void configureRefXStream(XStream xstream) {
    // Basic configuration, class loaders, and plug-in specific types.
    // For reference XStreams, we don't need the plug-ins' Node types, but we
    // do need the class loaders and the plug-ins' Relation types.
    configureGraphXStream(xstream);

    // Add in reference converters as the favored converters.
    GraphElements.REF_XML_PERSIST.config(xstream);
  }
}
