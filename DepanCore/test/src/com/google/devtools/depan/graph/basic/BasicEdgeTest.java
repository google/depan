/*
 * Copyright 2006 The Depan Project Authors
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

package com.google.devtools.depan.graph.basic;

import static org.junit.Assert.*;

import com.google.devtools.depan.graph.api.Relation;

import org.junit.Test;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class BasicEdgeTest extends BasicGraphTestCase {

  @Test
  public void testBasic() {
    Relation relation = new BasicRelation("forward", "reverse");

    BasicNode<String> head = createSimpleNode("head");
    BasicNode<String> tail = createSimpleNode("tail");

    BasicEdge<String> test = new BasicEdge<String>(relation, head, tail);

    assertSame(relation, test.getRelation());
    assertSame(head, test.getHead());
    assertSame(tail, test.getTail());
  }

}
