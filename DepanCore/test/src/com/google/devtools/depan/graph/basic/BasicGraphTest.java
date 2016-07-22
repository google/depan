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

import org.junit.Test;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class BasicGraphTest extends BasicGraphTestCase {

  @Test
  public void testBasic() {
    SimpleGraphFixture fixture = new SimpleGraphFixture();
    fixture.create();

    assertNotNull(fixture.headNode);
    assertNotNull(fixture.tailNode);
    assertNotNull(fixture.edge);
    assertNotNull(fixture.graph);

    BasicNode<? extends String> headNode = fixture.findNode(HEAD);
    BasicNode<? extends String> tailNode = fixture.findNode(TAIL);
    assertSame(fixture.headNode, headNode);
    assertSame(fixture.tailNode, tailNode);
    assertNotSame(headNode, tailNode);

    assertSame(MockRelation.SIMPLE_RELATION, fixture.edge.getRelation());
    assertSame(headNode, fixture.edge.getHead());
    assertSame(tailNode, fixture.edge.getTail());
  }
}
