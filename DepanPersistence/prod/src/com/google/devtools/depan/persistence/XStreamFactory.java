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

import com.google.devtools.depan.persistence.plugins.PersistencePluginRegistry;

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
   * Prevent instantiation of this namespace class
   */
  private XStreamFactory() {
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
    PersistencePluginRegistry.config(xstream);
  }
}
