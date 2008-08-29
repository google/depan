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
 * Element representing a field in a class. It's name, type and container class
 * are saved. Use all of this elements to get a unique id.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class FieldElement extends JavaElement {

  /**
   * field name.
   */
  private final String name;

  /**
   * pointer to this variable type.
   */
  private final TypeElement type;

  /**
   * pointer to this variable container class.
   */
  private final TypeElement containerClass;

  /**
   * Create a new {@link FieldElement}
   * @param name name of the variable
   * @param type variable's type
   * @param containerClass variable's container class
   */
  public FieldElement(String name, TypeElement type,
      TypeElement containerClass) {
    this.name = name;
    this.type = type;
    this.containerClass = containerClass;
  }
  
  /**
   * Uses name, type and containerClass hashs to create a hashCode.
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int hash = 1;
    hash = hash * 31 + (null == name ? 0 : name.hashCode());
    hash = hash * 31 + (null == type ? 0 : type.hashCode());
    hash = hash * 31 + (null == containerClass ? 0 : containerClass.hashCode());
    return hash;
  }
  
  /**
   * Two {@link FieldElement}s are equals iif their containerClass and name are
   * equals.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FieldElement) {
      FieldElement that = (FieldElement) obj;
      return that.name.equals(this.name) && that.type.equals(this.type)
          && that.containerClass.equals(this.containerClass);
    }
    return super.equals(obj);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.google.deptool.bytecodevisitor.interfaces.Element#getId()
   */
  public String getId() {
    return containerClass.getId() + "." + name + "/" + type.getId();
  }

  @Override
  public String toString() {
    return "Field " + getId();
  }

  @Override
  public String friendlyString() {
    return containerClass.friendlyString() + "." + name;
  }

  /**
   * @return the containerClass
   */
  public TypeElement getContainerClass() {
    return containerClass;
  }

  /**
   * @return the type
   */
  public TypeElement getType() {
    return type;
  }

  /**
   * @return the name for this field.
   */
  public String getName() {
    return name;
  }

  @Override
  public void accept(JavaElementVisitor visitor) {
    visitor.visitFieldElement(this);
  }
}
