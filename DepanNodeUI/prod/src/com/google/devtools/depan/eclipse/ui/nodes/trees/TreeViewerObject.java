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

import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Provide an object that allows non-{@link GraphNode}s to be rendered in an
 * Eclipse {code TreeViewer}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class TreeViewerObject extends PlatformObject {

  private final String name;

  private final PlatformObject parent;

  private final PlatformObject[] children;

  public TreeViewerObject(String name, PlatformObject[] children) {
    this(name, null, children);
  }

   public TreeViewerObject(
      String name, PlatformObject parent, PlatformObject[] children) {
    this.name = name;
    this.parent = parent;
    this.children = children;
  }

  public String getName() {
    return name;
  };

  public PlatformObject getParent() {
    return parent;
  };

  public PlatformObject[] getChildren() {
    return children;
  }

  public ImageDescriptor getImageDescriptor() {
    return null;
  }
}