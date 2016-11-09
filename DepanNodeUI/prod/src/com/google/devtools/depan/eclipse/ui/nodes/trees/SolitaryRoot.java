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

package com.google.devtools.depan.eclipse.ui.nodes.trees;

import org.eclipse.core.runtime.PlatformObject;

public class SolitaryRoot<E> extends PlatformObject {

  private final GraphData<E> data;
  private final String label;

  private NodeWrapper<E>[] children;

  public SolitaryRoot(GraphData<E> nodes, String label) {
    this.data = nodes;
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public NodeWrapper<E>[] getChildren() {
    if (null == children) {
      children = data.computeRootWrappers();
    }
    return children;
  }

  public GraphData<E> getGraphData() {
    return data;
  }
}
