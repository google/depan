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

import com.google.devtools.depan.graph.api.Relation;


/**
 * Lists relations that takes place between various
 * <code>FileSystemElement</code>s.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public enum FileSystemRelation implements Relation {
  // "Container" relationships
  CONTAINS_DIR("directory", "subdirectory"),
  CONTAINS_FILE("directory", "file"),
  SYMBOLIC_LINK("link", "file")
  ;

  /**
   * name of the element on the left of the relation.
   */
  public final String forwardName;
  /**
   * name of the element on the right side of the relation.
   */
  public final String reverseName;

  /**
   * constructor for a new Relation.
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
  private FileSystemRelation(String reverseName, String forwardName) {
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  /**
   * Returns the forward name of this relation.
   *
   * @return Forward name.
   */
  @Override
  public String getForwardName() {
    return forwardName;
  }

  /**
   * Returns the reverse name of this relation.
   *
   * @return Reverse name.
   */
  @Override
  public String getReverseName() {
    return reverseName;
  }
}
