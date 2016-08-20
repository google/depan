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

package com.google.devtools.depan.graph_doc.operations;

import static org.junit.Assert.assertEquals;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.test.TestUtils;

import org.junit.Test;

import java.util.Collection;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class SubtractNodesTest {

  @Test
  public void testSubtract() {
    GraphDocument baseDoc = TestUtils.buildTestDoc(5);
    GraphDocument minusDoc = TestUtils.buildTestDoc(4);

    SubtractNodes subtract = new SubtractNodes(baseDoc.getGraph());
    subtract.subtract(minusDoc.getGraph());

    Collection<GraphNode> testNodes = subtract.getNodes();
    assertEquals(1, testNodes.size());
    assertEquals("node 4", testNodes.iterator().next().getId());
  }
}
