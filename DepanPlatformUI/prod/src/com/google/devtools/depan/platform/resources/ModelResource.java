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

package com.google.devtools.depan.platform.resources;

import com.google.devtools.depan.graph_doc.model.DependencyModel;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Common resource type that supports generic info, {@link DependencyModel}
 * matching, and {@link PropertyResource}s for each entity.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ModelResource<T> implements PropertyResource {

  private final T info;
  private final ModelMatcher matcher;
  private final Map<String, String> properties = Maps.newHashMap();

  public ModelResource(T info, ModelMatcher matcher) {
    this.info = info;
    this.matcher = matcher;
  }

  public T getInfo() {
    return info;
  }

  public boolean forModel(DependencyModel model) {
    return matcher.forModel(model);
  }

  @Override
  public String getProperty(String key) {
    return properties.get(key);
  }

  public void setProperty(String key, String value) {
    properties.put(key, value);
  }
}
