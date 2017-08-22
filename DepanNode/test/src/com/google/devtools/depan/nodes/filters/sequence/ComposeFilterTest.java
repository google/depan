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

package com.google.devtools.depan.nodes.filters.sequence;

import static org.junit.Assert.assertEquals;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.test.TestUtils;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ComposeFilterTest {

  @Test
  public void testGetContextKeys() {
    List<TestKey> keys = Collections.singletonList(TestKey.TEST);

    MockFilter mock = new MockFilter();
    mock.keys = keys;

    ComposeFilter test = new ComposeFilter();
    test.setFilter(mock);

    assertEquals(keys, test.getContextKeys());
  }

  @Test
  public void testMatchingNodes() {
    Set<GraphNode> nodes =
        new HashSet<>(Arrays.asList(TestUtils.buildNodes(5)));
    MockFilter mock = new MockFilter();
    mock.compute = nodes;

    ComposeFilter test = new ComposeFilter();
    test.setFilter(mock);

    test.setMode(ComposeMode.INTERSECT);
    assertEquals(nodes, test.computeNodes(nodes));

    test.setMode(ComposeMode.SUBTRACT);
    assertEquals(Collections.emptySet(), test.computeNodes(nodes));

    test.setMode(ComposeMode.UNION);
    assertEquals(nodes, test.computeNodes(nodes));
  }

  @Test
  public void testOverlapNodes() {
    GraphNode[] rawNodes = TestUtils.buildNodes(5);
    List<GraphNode> nodes = Arrays.asList(rawNodes);
    Set<GraphNode> lower = new HashSet<>(nodes.subList(0, 3));
    Set<GraphNode> upper = new HashSet<>(nodes.subList(2, 5));

    MockFilter mock = new MockFilter();
    mock.compute = upper;

    ComposeFilter test = new ComposeFilter();
    test.setFilter(mock);

    test.setMode(ComposeMode.INTERSECT);
    assertEquals("[012] * [234] = [2]",
        new HashSet<>(nodes.subList(2, 3)), test.computeNodes(lower));

    test.setMode(ComposeMode.SUBTRACT);
    assertEquals("[012] - [234] = [01]",
        new HashSet<>(nodes.subList(0, 2)), test.computeNodes(lower));

    test.setMode(ComposeMode.UNION);
    assertEquals("[012] * [234] = [01234]",
        new HashSet<>(nodes), test.computeNodes(lower));
  }

  public enum TestKey implements ContextKey {
    TEST;

    @Override
    public String getLabel() {
      return "test";
    }
  }
}
