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

package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

/**
 * A sample {@link GraphNode} used in tests.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class MockElement extends GraphNode {
  /**
   * Name of this element.
   */
  private String name;

  /**
   * Constructs a new element with the given name.
   *
   * @param nodeName Name of this element.
   */
  public MockElement(String nodeName) {
    name = nodeName;
  }

  @Override
  public String friendlyString() {
    return name;
  }

  @Override
  public void accept(ElementVisitor visitor) {
    // nothing to do here
  }

  @Override
  public String getId() {
    return name;
  }
}

