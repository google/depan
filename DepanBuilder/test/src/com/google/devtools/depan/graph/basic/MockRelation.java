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

import com.google.devtools.depan.graph.api.Relation;

/**
 * A {@link Relation} implementation used in tests.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public enum MockRelation implements Relation {
  MEMBER_RELATION("member", "container"),
  SIMPLE_RELATION("reverse", "forward");

  /**
   * name of the element on the left of the relation.
   */
  public final String forwardName;

  /**
   * name of the element on the right side of the relation.
   */
  public final String reverseName;

  /**
   * Constructor for a new Relation.
   * @param forwardName name of the left hand side element.
   * @param reverseName name of the right hand side element.
   */
  private MockRelation(String reverseName, String forwardName) {
    this.forwardName = forwardName;
    this.reverseName = reverseName;
  }

  @Override
  public String getForwardName() {
    return forwardName;
  }

  @Override
  public String getReverseName() {
    return reverseName;
  }
}
