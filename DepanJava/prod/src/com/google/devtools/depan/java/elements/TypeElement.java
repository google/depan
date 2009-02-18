/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.java.elements;

import com.google.devtools.depan.java.JavaElementVisitor;

/**
 * Element representing a type. Fully qualified name is used as unique ID.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class TypeElement extends JavaElement {

  /**
   * This type's fully qualified name.
   */
  private final String fullyQualifiedName;

  /**
   * @param fullyQualifiedName
   */
  public TypeElement(String fullyQualifiedName) {
    super();
    this.fullyQualifiedName = fullyQualifiedName;
  }

  /**
   * @return the fully qualified name of this TypeElement.
   */
  public String getFullyQualifiedName() {
    return fullyQualifiedName;
  }

  /**
   * Uses fullyQualifiedName to create a hashCode.
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return fullyQualifiedName.hashCode();
  }
  
  /**
   * Two {@link TypeElement}s are equals iif their fullyQualifiedName are
   * equals.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object arg0) {
    if (arg0 instanceof TypeElement) {
      return ((TypeElement) arg0).fullyQualifiedName.equals(fullyQualifiedName);
    }
    return super.equals(arg0);
  }

  @Override
  public String getId() {
    return fullyQualifiedName;
  }

  @Override
  public String toString() {
    return "Type " + fullyQualifiedName;
  }

  @Override
  public String friendlyString() {
    if (fullyQualifiedName.contains(".")) {
      return fullyQualifiedName.substring(
          fullyQualifiedName.lastIndexOf('.') + 1, fullyQualifiedName.length());
    } else {
      return fullyQualifiedName;
    }
  }

  @Override
  public void accept(JavaElementVisitor visitor) {
    visitor.visitTypeElement(this);
  }
}
