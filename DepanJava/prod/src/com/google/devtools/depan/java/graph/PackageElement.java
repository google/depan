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
 * Element representing a package. the full package name is used as ID.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class PackageElement extends JavaElement {

  /**
   * this package's name 
   */
  private final String packageName;

  /**
   * Construct a new {@link PackageElement}.
   * 
   * @param packageName the package name
   */
  public PackageElement(String packageName) {
    this.packageName = packageName;
  }
  
  /**
   * Uses packageName to create a hashCode.
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return packageName.hashCode();
  }
  
  /**
   * Two {@link PackageElement}s are equals iif their packageName are equals.
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PackageElement) {
      return ((PackageElement) obj).packageName.equals(this.packageName);
    }
    return super.equals(obj);
  }

  @Override
  public String getJavaId() {
    return getPackageName();
  }

  @Override
  public String toString() {
    return "Package " + getJavaId();
  }

  @Override
  public String friendlyString() {
    return packageName; 
  }

  /**
   * @return the package name of this element.
   */
  public String getPackageName() {
    return packageName;
  }

  @Override
  public void accept(JavaElementVisitor visitor) {
    visitor.visitPackageElement(this);
  }
}
