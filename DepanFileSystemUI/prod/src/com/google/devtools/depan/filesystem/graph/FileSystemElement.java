/*
 * Copyright 2008 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.filesystem.graph;

import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

/**
 * The node of a graph which represents an abstract file system element such as
 * a file or directory.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public abstract class FileSystemElement extends GraphNode {

  public static final String FILESYSTEM_ID_PREFIX = "fs";

  public abstract String getPath();

  @Override
  public String getId() {
    return FILESYSTEM_ID_PREFIX + ":" + getPath();
  }

  /**
   * Accepts a {@link FileSystemElementVisitor} and performs whatever operation
   * that visitor requires.
   *
   * @param visitor Responsible for operating on the element.
   */
  public abstract void accept(FileSystemElementVisitor visitor);

  /**
   * Accepts an {@link ElementVisitor} and performs whatever operation
   * that visitor requires iff it is a {@link FileSystemElementVisitor}. This
   * method silently ignores all other types of visitors.
   *
   * @param visitor Responsible for operating on the element.
   */
  @Override
  public void accept(ElementVisitor visitor) {
    if (visitor instanceof FileSystemElementVisitor) {
      accept((FileSystemElementVisitor) visitor);
    }
  }
}
