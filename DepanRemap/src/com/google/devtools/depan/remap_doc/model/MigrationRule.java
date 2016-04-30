/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.remap_doc.model;

import com.google.devtools.depan.model.Element;

/**
 * A {@link MigrationRule} is a definition for a refactoring rule. It can
 * basically be a move (change the container) or a rename of an element.
 * 
 * @author leeca@google.com (Lee Carver)
 * @author ycoppel@google.com (Yohann Coppel)
 * @param <E> the type of elements this migration rule concern.
 */
public class MigrationRule<E extends Element> {

  /**
   * Source of the rule: the element before applying this rule.
   */
  private final E source;
  
  /**
   * Target: the element after applying this rule.
   */
  private E target;

  /**
   * Construct a MigrationRule without any changes. The target is the same as
   * the source.
   * 
   * @param source source {@link JavaElement}.
   */
  public MigrationRule(final E source) {
    this(source, source);
  }

  /**
   * Construct a full MigrationRule with the given source and target.
   * 
   * @param source the {@link JavaElement} before modification.
   * @param target the {@link JavaElement} after modification.
   */
  public MigrationRule(final E source, E target) {
    this.source = source;
    this.target = target;
  }

  public E getSource() {
    return source;
  }

  public E getTarget() {
    return target;
  }
  
  public void setTarget(E newTarget) {
    this.target = newTarget;
  }
  
  @Override
  public String toString() {
    return source.toString() + " to " + target;
  }
}
