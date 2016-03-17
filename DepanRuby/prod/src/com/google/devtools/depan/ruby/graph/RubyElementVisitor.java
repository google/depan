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

package com.google.devtools.depan.ruby.graph;

import com.google.devtools.depan.model.ElementVisitor;

/**
 * Lists the requirements of any {@link ElementVisitor} that operates
 * on Ruby elements.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface RubyElementVisitor extends ElementVisitor {

  /**
   * Performs the tasks of this visitor on the given {@link AttributeElement}.
   *
   * @param element {@link AttributeElement} on which the tasks of this visitor
   * will be performed.
   */
  void visitClassElement(ClassElement element);

  /**
   * Performs the tasks of this visitor on the given {@link AttributeElement}.
   *
   * @param element {@link AttributeElement} on which the tasks of this visitor
   * will be performed.
   */
  void visitClassMethodElement(ClassMethodElement element);

  /**
   * Performs the tasks of this visitor on the given {@link AttributeElement}.
   *
   * @param element {@link AttributeElement} on which the tasks of this visitor
   * will be performed.
   */
  void visitInstanceMethodElement(InstanceMethodElement element);

  /**
   * Performs the tasks of this visitor on the given {@link AttributeElement}.
   *
   * @param element {@link AttributeElement} on which the tasks of this visitor
   * will be performed.
   */
  void visitSingletonMethodElement(SingletonMethodElement element);
}
