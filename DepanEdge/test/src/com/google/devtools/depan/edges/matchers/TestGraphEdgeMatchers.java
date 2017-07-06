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

package com.google.devtools.depan.edges.matchers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.test.TestUtils;

import org.junit.Test;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TestGraphEdgeMatchers {

  @Test
  public void testCreateForwardEdgeMatcher() {
    GraphEdgeMatcher matcher =
        GraphEdgeMatchers.createForwardEdgeMatcher(TestUtils.RELATION_SET);
    assertTrue(matcher.relationForward(TestUtils.RELATION)); 
    assertFalse(matcher.relationReverse(TestUtils.RELATION)); 
  }

  @Test
  public void testCreateBinaryEdgeMatcher() {
    GraphEdgeMatcher forward = GraphEdgeMatchers.createBinaryEdgeMatcher(
        TestUtils.RELATION_SET, RelationSets.EMPTY);
    assertTrue(forward.relationForward(TestUtils.RELATION)); 
    assertFalse(forward.relationReverse(TestUtils.RELATION)); 

    GraphEdgeMatcher reverse = GraphEdgeMatchers.createBinaryEdgeMatcher(
        RelationSets.EMPTY, TestUtils.RELATION_SET);
    assertFalse(reverse.relationForward(TestUtils.RELATION)); 
    assertTrue(reverse.relationReverse(TestUtils.RELATION)); 
  }
}
