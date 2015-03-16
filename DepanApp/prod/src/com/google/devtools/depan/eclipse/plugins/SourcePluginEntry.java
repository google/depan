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

package com.google.devtools.depan.eclipse.plugins;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * A {@link SourcePluginEntry} represent an entry in a plugin.xml file,
 * describing a source plugin.
 *
 * @author Yohann Coppel
 *
 */
public class SourcePluginEntry {

  public final static String ATTR_ID = "id";
  public final static String ATTR_SOURCE = "source";
  public final static String ATTR_DESCRIPTION = "description";
  public final static String ATTR_CLASS = "class";

  /**
   * {@link IConfigurationElement} found in the xml file.
   */
  private final IConfigurationElement element;

  /**
   * Plugin's ID.
   */
  private final String id;

  /**
   * Sources handled by the plugin. For instance, "Java".
   */
  private final String source;

  /**
   * A short description of the plugin.
   */
  private final String description;

  /**
   * An instance of the plugin.
   */
  private SourcePlugin instance = null;

  /**
   * Constructor extract information from the {@link IConfigurationElement} to
   * setup constants.
   * @param element
   */
  public SourcePluginEntry(IConfigurationElement element) {
    this.element = element;
    this.id = element.getAttribute(ATTR_ID);
    this.source = element.getAttribute(ATTR_SOURCE);
    this.description = element.getAttribute(ATTR_DESCRIPTION);
  }

  /**
   * @return the ID for this plugin.
   */
  public String getId() {
    return id;
  }

  /**
   * @return a {@link String} describing the type of dependencies handled by
   * this plugin.
   */
  public String getSource() {
    return source;
  }

  /**
   * @return a short description of this plugin.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return try to instantiate the class described by the xml file, and return
   * it if the loading is successful and the class instantiated.
   * @throws CoreException if the instantiation failed
   */
  public SourcePlugin getInstance() throws CoreException {
    if (null == instance) {
      instance = (SourcePlugin) element.createExecutableExtension(ATTR_CLASS);
    }
    return instance;
  }
}
