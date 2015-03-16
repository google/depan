/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript.graph;

import com.google.devtools.depan.graph.api.Relation;

/**
 * Lists relations that are normally defined between various
 * <code>JavaScriptElement</code>s.
 * <p/>
 * For historical reasons, this and every other enumeration implements the
 * Relation interface gets the forward and backward names reversed.  And the
 * relation-picker user interface expects them to be reversed.  So stick
 * with this until the Relation type is cleaned up.  See issue #50 for
 * more details.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public enum JavaScriptRelation implements Relation {
  // Source file declarations relationships
  DEFINES_NAME("defines", "inside"),
  IMPLIES_NAME("implies", "inside"),

  // Scoping relations
  BINDS_ELEMENT("binds", "within"),

  // Simple "file reference name" relationship
  REFERENCES_NAME("references", "user"),
  ;

  /**
   * Interpretation of the link going forward.
   */
  public final String forwardName;

  /**
   * Interpretation of the link going backward.
   */
  public final String reverseName;

  /**
   * constructor for a new Relation.
   * @param forwardName name for a forward link traversal
   * @param reverseName name for a reverse link traversal
   */
  private JavaScriptRelation(String reverseName, String forwardName) {
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
