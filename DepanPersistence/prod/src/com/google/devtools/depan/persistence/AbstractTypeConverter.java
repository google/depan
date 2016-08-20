/*
 * Copyright 2015 The Depan Project Authors
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;

/**
 * Handle the common case that a converter knows the type for which
 * it is converting.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractTypeConverter implements Converter {

  /**
   * Hook method to provide type the registration and converter processes.
   */
  public abstract Class<?> getType();

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return getType().isAssignableFrom(type);
  }

  /**
   * Register this {@link Converter} for its type with
   * the indicated alias.
   */
  public void registerWithTag(XStream xstream, String typeTag) {
    xstream.aliasType(typeTag, getType());
    xstream.registerConverter(this);
  }
}
