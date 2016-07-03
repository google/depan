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

package com.google.devtools.depan.java.graph;

import com.google.devtools.depan.model.Element;

/**
 * Abstract class that can be used as a helper, to be sure that each case is
 * handled when we want to do a specific action and a return type for each
 * Element type.
 *
 * This is basically an {@link JavaElementVisitor} but with a return type
 * <code>R</code>.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <R> match return type.
 */
public abstract class JavaElementDispatcher<R> implements JavaElementVisitor {
  /**
   * The object that will be the final result of a call to <code>match</code>.
   */
  protected R returnValue = null;

  public abstract R match(TypeElement e);
  public abstract R match(MethodElement e);
  public abstract R match(FieldElement e);
  public abstract R match(InterfaceElement e);
  public abstract R match(PackageElement e);

  @Override
  public void visitTypeElement(TypeElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitMethodElement(MethodElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitFieldElement(FieldElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitInterfaceElement(InterfaceElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitPackageElement(PackageElement element) {
    returnValue = match(element);
  }

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
}
