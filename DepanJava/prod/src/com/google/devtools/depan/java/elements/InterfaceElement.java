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
 * Element representing an interface in the code source. Use the fully qualified
 * name to inuquely identify this element.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class InterfaceElement extends JavaElement {

  private final String fullyQualifiedName;

  /**
   * @param type
   */
  public InterfaceElement(Type type) {
    if (type.getSort() == Type.ARRAY) {
      this.fullyQualifiedName = Type.getObjectType(type.getInternalName())
          .getClassName();
    } else {
      this.fullyQualifiedName = type.getClassName();
    }
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
   * Two {@link InterfaceElement}s are equals iif their fullyQualifiedName are
   * equals.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof InterfaceElement) {
      return ((InterfaceElement) obj).fullyQualifiedName
          .equals(this.fullyQualifiedName);
    }
    return super.equals(obj);
  }
  
  public String getId() {
    return fullyQualifiedName;
  }

  public String getFullyQualifiedName() {
    return fullyQualifiedName;
  }

  /**
   * Creates and return an InterfaceElement given its descriptor from the
   * bytecode (e.g. "Ljava/lang/String;").
   * 
   * @param desc
   * @return a new InterfaceElement coresponding to the given descriptor.
   */
  public static JavaElement fromDescriptor(String desc) {
    return new InterfaceElement(Type.getType(desc));
  }

  /**
   * Creates and return an InterfaceElement given its internalName from the
   * bytecode (e.g. "java/lang/String").
   * 
   * @param internalName
   * @return a new InterfaceElement coresponding to the given internal name.
   */
  public static JavaElement fromInternalName(String internalName) {
    return new InterfaceElement(Type.getObjectType(internalName));
  }

  @Override
  public String toString() {
    return "Interface " + fullyQualifiedName;
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
    visitor.visitInterfaceElement(this);
  }
}
