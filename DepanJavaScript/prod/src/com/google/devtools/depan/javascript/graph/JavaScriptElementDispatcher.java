/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript.graph;

import com.google.devtools.depan.model.Element;

/**
 * Element Dispatcher for JavaScript elements. Provides a mechanism to
 * visit all types of elements through a single call to {@code match} in this
 * class.  All JavaScript elements have to implement
 * {@code void accept(ElementVisitor visitor)}
 * method while each dispatcher only has to implement type-specific
 * {@code match()} methods.
 *
 * @param <R> Type of dispatcher which may range from color providers to image
 * providers.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class JavaScriptElementDispatcher<R> implements
    JavaScriptElementVisitor {

  /**
   * The object that will be the final result of a call to <code>match</code>.
   */
  protected R returnValue = null;

  public abstract R match(JavaScriptBuiltinElement builtinElement);

  public abstract R match(JavaScriptClassElement classElement);

  public abstract R match(JavaScriptEnumElement enumElement);

  public abstract R match(JavaScriptFieldElement fieldElement);

  public abstract R match(JavaScriptFunctionElement functionElement);

  public abstract R match(JavaScriptVariableElement variableElement);

  public R match(Element element) {
    element.accept(this);
    return returnValue;
  }

  @Override
  public void visitClass(JavaScriptBuiltinElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitClass(JavaScriptClassElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitEnum(JavaScriptEnumElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitField(JavaScriptFieldElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitFunction(JavaScriptFunctionElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitVariable(JavaScriptVariableElement element) {
    returnValue = match(element);
  }
}
