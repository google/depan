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

package com.google.devtools.depan.view_doc.model;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Bundle of rendering options that can be modeled as a string to string
 * mapping.
 * 
 * @author Lee Carver
 *
 */
public class OptionPreference {
  private final Map<String, String> options = Maps.newHashMap();

  public void setOption(String key, String value) {
    options.put(key, value);
  }

  public String getOption(String key) {
    return options.get(key);
  }

  public boolean hasOption(String key) {
    return (null != getOption(key));
  }

  public String getOption(String key, String forNull) {
    String result = options.get(key);
    if (result != null) {
      return result;
    }
    return forNull;
  }

  public boolean getBooleanOption(String key) {
    return Boolean.parseBoolean(getOption(key));
  }

  public boolean getBooleanOption(String key, boolean forNull) {
    if (hasOption(key)) {
      return getBooleanOption(key);
    }
    return forNull;
  }
}
