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

import org.objectweb.asm.Type;

import com.google.devtools.depan.java.JavaElementVisitor;

/**
 * Element representing a type. Fully qualified name is used as unique ID.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class TypeElement extends JavaElement {

  /**
   * this type fully qualified name.
   */
  private final String fullyQualifiedName;

  /**
   * Construct a new TypeElement.
   * 
   * @param type {@link Type} of this TypeElement. The constructor extract the
   *        fully qualified name from this {@link Type}.
   */
  private TypeElement(Type type) {
    if (type.getSort() == Type.ARRAY) {
      this.fullyQualifiedName = Type.getType(
          type.toString().replaceAll("^\\[*", "")).getClassName();
    } else {
      this.fullyQualifiedName = type.getClassName();
    }
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
  
  /* (non-Javadoc)
   * @see com.google.devtools.depan.bytecodevisitor.interfaces.Element#getId()
   */
  public String getId() {
    return fullyQualifiedName;
  }

  /**
   * Creates and return a ClassElement given its descriptor (e.g.
   * Ljava/lang/String;).
   * 
   * @param desc type descriptor
   * @return a new TypeElement for the given descriptor.
   */
  public static TypeElement fromDescriptor(String desc) {
    return new TypeElement(Type.getType(desc));
  }

  /**
   * Creates and return a ClassElement given its internalName (e.g.
   * java/lang/String).
   * 
   * @param internalName type internal name.
   * @return a new TypeElement for the given internal name.
   */
  public static TypeElement fromInternalName(String internalName) {
    return new TypeElement(Type.getObjectType(internalName));
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
