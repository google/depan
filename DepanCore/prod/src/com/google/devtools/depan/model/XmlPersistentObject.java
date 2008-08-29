/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.model;

import com.google.devtools.depan.collect.Lists;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * A base type to write any type of object to persistent storage. All child
 * classes must implement save and load methods.
 *
 * @author tugrul@google.com (Tugrul Ince)
 *
 * @param <T> The type of object that will be stored in persistent storage.
 */
public abstract class XmlPersistentObject<T> {
  /**
   * The object that converts the object to XML.
   */
  protected XStream xstream;

  /**
   * List of configuration objects that are used to configure
   * <code>XStream</code> object.
   */
  protected static List<Config> configs = Lists.newArrayList();

  static {
    configs.add(GraphElements.configXmlPersist);
  }

  /**
   * Adds a new configuration object to this class.
   * 
   * @param configXmlPersist Configuration object that will be used to configure
   * <code>XStream</code>.
   */
  public static void addConfig(Config configXmlPersist) {
    if (!configs.contains(configXmlPersist)) {
      configs.add(configXmlPersist);
    }
  }

  /**
   * Constructs and configures this object.
   */
  public XmlPersistentObject() {
    this.xstream = new XStream(new StaxDriver());
    xstream.setMode(XStream.NO_REFERENCES);
    for (Config c : configs) {
      config(c);
    }
  }

  /**
   * Configures <code>XStream</code> object with the given configuration.
   *
   * @param framework
   */
  public void config(Config framework) {
    framework.config(xstream);
  }

  /**
   * Writes the given item to the given location. It is a good practice to call
   * <code>SourcePluginRegistry.setupXMLConfig()</code> before calling this
   * method.
   *
   * @param uri Persistent storage location.
   * @param item Object that is being written.
   * @throws IOException if persistent storage is not accessible.
   */
  public abstract void save(URI uri, T item) throws IOException;

  /**
   * Loads the object that is stored at the given location. It is a good
   * practice to call <code>SourcePluginRegistry.setupXMLConfig()</code> before
   * calling this method.
   *
   * @param uri Location of the object.
   * @return Object that is read from the given location.
   */
  public abstract T load(URI uri);

  /**
   * Configuration class that is responsible for configuring
   * <code>XStream</code> objects. 
   */
  public interface Config {
    void config(XStream xstream);
  }
}
