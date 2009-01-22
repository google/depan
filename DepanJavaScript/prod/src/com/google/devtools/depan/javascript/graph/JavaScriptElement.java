/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.javascript.graph;

import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

/**
 * Provide a marker-type for JavaScript graph elements, and define the
 * root method ({@link #accept(ElementVisitor)} for Vistors over these
 * graph elements.
 * 
 * Also, define some convenience methods for manipulating element names.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class JavaScriptElement extends GraphNode {

  /**
   * Convert a JavaScript style name to its parent name.
   * No everything that shows up in JavaScript analysis is a
   * {@code JavaScriptElement}.
   * 
   * @param qName Qualified name, with elements separated by periods
   * @return the qName of the parent, or empty if no parent is identified
   */
  public static String getParentName(String qName) {
    int dotSplit = qName.lastIndexOf('.');
    if (dotSplit < 1) {
      return "";
    }

    return qName.substring(0, dotSplit);
  }

  /**
   * Provide parent name as a common method for all {@code JavaScriptElement}s.
   * 
   * @return parent name for this element.  The empty {@code String} is
   *     provided if the element has no parent.
   */
  public String getParentName() {
    return getParentName(getId());
  }

  /**
   * Provide the name of the JavaScript element.  This is the final identifier
   * in any qualified name.
   * 
   * @return the simple name of a JavaScript element.
   */
  public String getElementName() {
    String qName = getId();

    int dotSplit = qName.lastIndexOf('.');
    if (dotSplit < 0) {
      return qName;
    }

    return qName.substring(dotSplit + 1);
  }

  /**
   * Accept an JavaScriptElementVisitor.
   * 
   * @param visitor
   */
  public abstract void accept(JavaScriptElementVisitor visitor);

  @Override
  public void accept(ElementVisitor visitor) {
    if (visitor instanceof JavaScriptElementVisitor) {
      accept((JavaScriptElementVisitor) visitor);
    }
  }
}
