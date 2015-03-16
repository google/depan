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


/**
 * Element representing a method. Use the class, methodname and signature as
 * unique ID.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class MethodElement extends JavaElement {

  /**
   * this method's signature (arguments)
   */
  private final String methodSignature;
  
  /**
   * this mentod's name
   */
  private final String methodName;
  
  /**
   * {@link TypeElement} containing this method 
   */
  private final TypeElement classElement;

  /**
   * Construct a new Method Element.
   * @param methodSignature this method signature (basically arguments types)
   * @param methodName method name
   * @param classElement the {@link TypeElement} containing this method
   */
  public MethodElement(String methodSignature, String methodName,
      TypeElement classElement) {
    super();
    this.methodSignature = methodSignature;
    this.methodName = methodName;
    this.classElement = classElement;
  }

  /**
   * Uses methodSignature, methodName and classElement to create a hashCode.
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int hash = 1;
    hash = hash * 31
        + (null == methodSignature ? 0 : methodSignature.hashCode());
    hash = hash * 31 + (null == methodName ? 0 : methodName.hashCode());
    hash = hash * 31 + (null == classElement ? 0 : classElement.hashCode());
    return hash;
  }
  
  /**
   * Two {@link MethodElement}s are equals iif their methodSignature, methodName
   * and classElement are equals.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MethodElement) {
      MethodElement that = (MethodElement) obj;
      return that.methodSignature.equals(this.methodSignature)
          && that.methodName.equals(this.methodName)
          && that.classElement.equals(this.classElement);
    }
    return super.equals(obj);
  }

  @Override
  public String getJavaId() {
    return getClassElement().getJavaId() + "." + getName()
        + "(" + getSignature() + ")";
  }

  @Override
  public String toString() {
    return "Method " + getJavaId();
  }

  @Override
  public String friendlyString() {
    return getClassElement().friendlyString() + "." + getName();
  }

  public TypeElement getClassElement() {
    return classElement;
  }

  public String getName() {
    return methodName;
  }

  public String getSignature() {
    return methodSignature;
  }

  @Override
  public void accept(JavaElementVisitor visitor) {
    visitor.visitMethodElement(this);
  }
}
