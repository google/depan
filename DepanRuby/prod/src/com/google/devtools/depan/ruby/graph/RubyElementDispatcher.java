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

import com.google.devtools.depan.model.Element;

/**
 * Element Dispatcher for the Maven Plug-in elements. Provides a mechanism to
 * visit all types of elements through a single call to {@link #match(Element)}
 * in this class. All elements implement the {@code void accept(Element visitor)}
 * method and all dispatchers implement corresponding {@code match()} methods.
 *
 * @param <R> Type of dispatcher which may range from color providers to image
 * providers.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class RubyElementDispatcher<R> implements
    RubyElementVisitor {
  /**
   * The object that will be the final result of a call to <code>match</code>.
   */
  protected R returnValue = null;

  public abstract R match(ClassElement element);

  public abstract R match(ClassMethodElement element);

  public abstract R match(InstanceMethodElement element);

  public abstract R match(SingletonMethodElement element);

  /**
   * Performs tasks of this dispatcher on the given element. This is the method
   * that must be called by the users of this dispatcher. The proper redirection
   * is handled by this dispatcher.
   *
   * @param element {@link Element} on which this dispatcher will operate.
   * @return Result of performing the tasks of this dispatcher on this element.
   */
  public R match(Element element) {
    element.accept(this);
    return returnValue;
  }

  @Override
  public void visitClassElement(ClassElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitClassMethodElement(ClassMethodElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitInstanceMethodElement(InstanceMethodElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitSingletonMethodElement(SingletonMethodElement element) {
    returnValue = match(element);
  }
}
