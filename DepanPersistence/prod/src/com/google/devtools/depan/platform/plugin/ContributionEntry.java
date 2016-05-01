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

package com.google.devtools.depan.platform.plugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Common manager for information about a contribution.
 * 
 * Many components have a plugin registry for contributions of a simple type.
 *
 * @author Yohann Coppel
 *
 */
public abstract class ContributionEntry<T> {

  public final static String ATTR_ID = "id";
  public final static String ATTR_SOURCE = "source";
  public final static String ATTR_DESCRIPTION = "description";
  public final static String ATTR_CLASS = "class";

  /**
   * {@link IConfigurationElement} found in the xml file.
   */
  private final IConfigurationElement element;

  /**
   * ContributionEntry ID.
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
  private T instance = null;

  /**
   * Constructor extract information from the {@link IConfigurationElement} to
   * setup constants.
   * 
   * A derived type might collect additional attributes from the
   * {@link IConfigurationElement}
   */
  public ContributionEntry(IConfigurationElement element) {
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

  /////////////////////////////////////
  // Invokable class support

  /**
   * Utility method to obtain a Java callable instance from the extension point.
   * 
   * Due to erasure, the result cannot be cast to the correct type based solely
   * on the class's type parameter {@code T}.  Use this method to implement the
   * type-coercing hook method {@link #createInstance()}.
   */
  protected Object buildInstance(String classTag) throws CoreException {
    return element.createExecutableExtension(classTag);
  }

  /**
   * Type-coercing hook method to cast the Eclipse supplied {@link Object}
   * instance into the type expected by this extension point.
   * 
   * Typically implemented as
   * {@code return (type-cast) buildInstance(ATTR_CLASS);}
   * 
   * Due to erasure, the type is lost and no type-cast can be synthesized from
   * Java's type-parameter.
   */
  protected abstract T createInstance() throws CoreException;

  /**
   * @return try to instantiate the class described by the xml file, and return
   * it if the loading is successful and the class instantiated.
   * @throws CoreException if the instantiation failed
   */
  public T getInstance() throws CoreException {
    if (null == instance) {
      instance = createInstance();
    }
    return instance;
  }
}
