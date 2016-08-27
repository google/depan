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

package com.google.devtools.depan.nodes.filters.model;

/**
 * Marker interface that define the allowed set of keys for the 
 * {@link FilterContext}.
 * 
 * The interface may be extended beyond the {@link Base} set of enumerated
 * keys.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface ContextKey {

  /**
   * Provide a user-sensible label for the context key.
   */
  String getLabel();

  /**
   * Base set of keys that might be available in the context.
   */
  public enum Base implements ContextKey {
    UNIVERSE("Graph"),
    VIEWDOC("View");

    private final String label;

    private Base(String label) {
      this.label = label;
    }

    @Override
    public String getLabel() {
      return label;
    }
  }
}
