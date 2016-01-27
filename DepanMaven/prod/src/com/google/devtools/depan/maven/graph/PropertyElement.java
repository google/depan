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

package com.google.devtools.depan.maven.graph;

/**
 * {@link MavenElement} that represents an Maven property definition.
 * 
 * @author Lee Carver
 */
public class PropertyElement extends MavenElement {

  /**
   * Name of the Maven property.
   */
  private final String property;

  public PropertyElement(String property) {
    this.property = property;
  }

  @Override
  protected String getCoordinate() {
    return property;
  }

  @Override
  public void accept(MavenElementVisitor visitor) {
    visitor.visitPropertyElement(this);
  }

  @Override
  public String friendlyString() {
    return getCoordinate();
  }

  /**
   * Two {@link PropertyElement}s are equals iff their names are equals.
   */
  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof PropertyElement)) {
      return super.equals(obj);
    }
    PropertyElement that = (PropertyElement) obj;

    return property.equals(that.property);
  }

  @Override
  public int hashCode() {
    return property.hashCode();
  }

  /**
   * Returns the <code>String</code> representation of this object.
   *
   * @return <code>String</code> representation of this object.
   */
  @Override
  public String toString() {
    return "Property " + getCoordinate();
  }
}
