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

/**
 * {@link RubyElement} that represents a Ruby class.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ClassElement extends RubyElement {

  /**
   * Name of the Ruby class.
   */
  private final String type;

  public ClassElement(String type) {
    this.type = type;
  }

  @Override
  protected String getCoordinate() {
    return type;
  }

  @Override
  public void accept(RubyElementVisitor visitor) {
    visitor.visitClassElement(this);
  }

  @Override
  public String friendlyString() {
    return getCoordinate();
  }

  /**
   * Two {@link ClassElement}s are equals iff their names are equals.
   */
  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof ClassElement)) {
      return super.equals(obj);
    }
    ClassElement that = (ClassElement) obj;

    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  /**
   * Returns the <code>String</code> representation of this object.
   *
   * @return <code>String</code> representation of this object.
   */
  @Override
  public String toString() {
    return "Class " + getCoordinate();
  }
}
