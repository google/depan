/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.collect.Maps;

import java.net.URI;
import java.util.Map;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ResourceCache {
  
  /////////////////////////////////////
  // GraphModel cache
  private static Map<URI, GraphModel> loadedGraphs = Maps.newHashMap();

  public static GraphModel fetchGraphModel(URI uri) {
    GraphModel result = loadedGraphs.get(uri);

    if (null == result) {
      XmlPersistentGraph loader = new XmlPersistentGraph();
      result = loader.load(uri);
      loadedGraphs.put(uri, result);
    }

    return result;
  }
}
