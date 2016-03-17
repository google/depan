/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.ruby.graph;

import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

/**
 * Basic definition for a Maven node.
 *
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class RubyElement extends GraphNode {

  public static final String RUBY_ID_PREFIX = "ruby";

  protected abstract String getCoordinate();

  @Override
  public String getId() {
    return RUBY_ID_PREFIX + ":" + getCoordinate();
  }

  /**
   * Accepts a {@link RubyElementVisitor} and performs whatever operation
   * that visitor requires.
   *
   * @param visitor Responsible for operating on the element.
   */
  public abstract void accept(RubyElementVisitor visitor);

  /**
   * Accepts an {@link ElementVisitor} and performs whatever operation
   * that visitor requires iff it is a {@link RubyElementVisitor}. This
   * method silently ignores all other types of visitors.
   *
   * @param visitor Responsible for operating on the element.
   */
  @Override
  public void accept(ElementVisitor visitor) {
    if (visitor instanceof RubyElementVisitor) {
      accept((RubyElementVisitor) visitor);
    }
  }
}
