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

import com.google.devtools.depan.model.GraphNode;


/**
 * Elements are a way to communicate between the .class parser to the graph
 * maker. They are not made to be included into a graph directly. Two different
 * objects can be created (and most likely will) by the parser, event if they
 * refer to the same element (e.g. java.lang.Object can be represented by
 * various Elements in the parser, but we only need one in the graph.
 * 
 * An ElementToNodeMapper object should be used to be sure that we don't use two
 * different object for the same represented element.
 *
 * The {@link #friendlyString()} should return a unique id for this element.
 * This id must not change after saving the element to a persistent storage
 * for example, so don't use a hashCode made from the memory address.
 * Instead, use for example a fully qualified name.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public abstract class JavaElement extends GraphNode {

  public static final String JAVA_ID_PREFIX = "java";

  public abstract String getJavaId();

  @Override
  public String getId() {
    return JAVA_ID_PREFIX + ":" + getJavaId();
  }

  /**
   * Accept an ElementVisitor.
   * 
   * @param visitor
   */
  public abstract void accept(JavaElementVisitor visitor);

  @Override
  public void accept(com.google.devtools.depan.model.ElementVisitor visitor) {
    if (visitor instanceof JavaElementVisitor) {
      accept((JavaElementVisitor) visitor);
    }
  }
}
