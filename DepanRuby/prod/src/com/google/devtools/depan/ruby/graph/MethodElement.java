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
 * {@link RubyElement} that represents the basics of a Ruby method.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class MethodElement extends RubyElement {

  /** Name of the owning Ruby class. */
  private final String type;

  /** Name of the Ruby method. */
  private final String method;

  public MethodElement(String type, String method) {
    this.type = type;
    this.method = method;
  }

  @Override
  public String friendlyString() {
    return getCoordinate();
  }

  protected String formatCoordinate(String sep) {
    return type + sep + method;
  }

  protected boolean checkParts(Object obj) {
    MethodElement test = (MethodElement) obj;
    if (type.equals(test.type)) {
      return false;
    }
    return method.equals(test.method);
  }

  /**
   * Two {@link ClassElement}s are equals iff their names are equals.
   */
  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof ClassElement)) {
      return super.equals(obj);
    }
    return checkParts(obj);
  }

  @Override
  public int hashCode() {
    int result = 37;
    result = 31 * result + type.hashCode();
    result = 31 * result + method.hashCode();
    return result;
  }
}
