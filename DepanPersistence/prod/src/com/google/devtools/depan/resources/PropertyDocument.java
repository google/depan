/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.resources;

import com.google.common.collect.Maps;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Common resource type that supports generic info, {@link DependencyModel}
 * matching, and {@link PropertyResource}s for each entity.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PropertyDocument<T> implements PropertyResource {

  public static final String NAME_KEY = "name";

  private final String name;

  private Map<String, String> properties = Maps.newHashMap();

  public PropertyDocument(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String getProperty(String key) {
    if (NAME_KEY.equals(key)) {
      return getName();
    }
    return properties.get(key);
  }

  @Override
  public Object getObject(String key) {
    return null;
  }

  public void setProperty(String key, String value) {
    validateWritable(key);
    properties.put(key, value);
  }

  /**
   * @param key
   */
  private void validateWritable(String key) {
    if (isWritable(key)) {
      return;
    }

    throw new IllegalAccessError(
        MessageFormat.format("Property key {0} is not writable", key));
  }

  /**
   * Hook method to integrated with {@link #setProperty(String, String)}
   * validation.
   * 
   * Override (and forward) when derived types introduce new unchangable
   * properties.
   */
  protected boolean isWritable(String key) {
    if (NAME_KEY.equals(key)) {
      return false;
    }
    return true;
  }
}
