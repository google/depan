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

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Basic contribution with a unique identifier bound to a contributed class.
 * 
 * Extension point schemas based on this class should define their 
 * contribution class to have two attributes.
 * 
 * The class attribute should be required, and type java.  The schema should
 * specify the required conforming interface for contributing extensions.
 * 
 * The id attribute should be required, and type string.  This should uniquely
 * identify the contribution within the universe of all contributions.  The
 * FQCN for the implementing class is a typical value.
 * 
 * Many components have a plugin registry for contributions of a simple type.
 *
 * @author Yohann Coppel
 */
public abstract class ContributionEntry<T> {

  public final static String ATTR_ID = "id";
  public final static String ATTR_CLASS = "class";

  /**
   * The OSGi bundle providing the contribution.
   */
  private final String bundleId;

  /**
   * {@link IConfigurationElement} found in the xml file.
   */
  private final IConfigurationElement element;

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
  public ContributionEntry(String bundleId, IConfigurationElement element) {
    this.bundleId = bundleId;
    this.element = element;
  }

  /**
   * @return the ID for this plugin.
   */
  public String getId() {
    return element.getAttribute(ATTR_ID);
  }


  /**
   * @return the ID for the contributing OSGi bundle.
   */
  public String getBundleId() {
    return bundleId;
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
  public T prepareInstance() throws CoreException {
    if (null == instance) {
      instance = createInstance();
    }
    return instance;
  }

  /**
   * Provide the plugin's instance.  Assumes the instance has already been
   * created, which is common when used with the collaborating 
   * {@link ContributionRegistry} type.
   */
  public T getInstance() {
    return instance;
  }

  public Collection<String> getDependIds(
      String dependTag, String dependAttr) {
    IConfigurationElement[] depends = element.getChildren(dependTag);
    if (0 == depends.length) {
      return Collections.emptyList();
    }
    List<String> result = Lists.newArrayListWithExpectedSize(depends.length);
    for (IConfigurationElement depend : depends) {
      result.add(depend.getAttribute(dependAttr));
    }
    return result;
  }
}
