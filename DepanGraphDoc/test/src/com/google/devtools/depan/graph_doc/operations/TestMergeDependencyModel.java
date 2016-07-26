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

import static org.junit.Assert.*;

import com.google.devtools.depan.graph_doc.model.DependencyModel;

import com.google.common.collect.ImmutableList;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TestMergeDependencyModel {

  private static final List<String> ALPHA =
      ImmutableList.<String>of("Alpha");

  private static final List<String> ALPHA_BETA =
      ImmutableList.<String>of("Alpha", "Beta");

  private static final List<String> ZED_BETA =
      ImmutableList.<String>of("Zed", "Beta");

  private static final List<String> ALPHA_ZED_BETA =
      ImmutableList.<String>of("Alpha", "Zed", "Beta");

  private static final List<String> ZED_ALPHA_BETA =
      ImmutableList.<String>of("Zed", "Alpha","Beta");

  /**
   * This merge sequence mimics Java followed by FileSystem.
   */
  @Test
  public void testMergeModel_AB_A() {
    MergeDependencyModel merger = new MergeDependencyModel();
    DependencyModel testOne = new DependencyModel(ALPHA_BETA, ALPHA_BETA);
    DependencyModel testTwo = new DependencyModel(ALPHA, ALPHA);
    merger.merge(testOne);
    merger.merge(testTwo);
    DependencyModel result = merger.getDependencyModel();
    assertEquals(testOne, result);
  }

  /**
   * This merge sequence mimics FileSystem followed by Java.
   */
  @Test
  public void testMergeModel_A_AB() {
    MergeDependencyModel merger = new MergeDependencyModel();
    DependencyModel testOne = new DependencyModel(ALPHA, ALPHA);
    DependencyModel testTwo = new DependencyModel(ALPHA_BETA, ALPHA_BETA);
    merger.merge(testOne);
    merger.merge(testTwo);
    DependencyModel result = merger.getDependencyModel();
    assertEquals(testTwo, result);
  }

  /**
   * This merge sequence mimics Java followed by JavaScript.
   * The other order is permitted by the spec, but this tests implementation
   * details.
   */
  @Test
  public void testMergeModel_AB_ZB() {
    MergeDependencyModel merger = new MergeDependencyModel();
    DependencyModel testOne = new DependencyModel(ALPHA_BETA, ALPHA_BETA);
    DependencyModel testTwo = new DependencyModel(ZED_BETA, ZED_BETA);
    merger.merge(testOne);
    merger.merge(testTwo);
    DependencyModel result = merger.getDependencyModel();
    DependencyModel expected =
        new DependencyModel(ALPHA_ZED_BETA, ALPHA_ZED_BETA);
    assertEquals(expected, result);
  }

  /**
   * This merge sequence mimics Java followed by JavaScript.
   * The other order is permitted by the spec, but this tests implementation
   * details.
   */
  @Test
  public void testMergeModel_ZB_AB() {
    MergeDependencyModel merger = new MergeDependencyModel();
    DependencyModel testOne = new DependencyModel(ZED_BETA, ZED_BETA);
    DependencyModel testTwo = new DependencyModel(ALPHA_BETA, ALPHA_BETA);
    merger.merge(testOne);
    merger.merge(testTwo);
    DependencyModel result = merger.getDependencyModel();
    DependencyModel expected =
        new DependencyModel(ZED_ALPHA_BETA, ZED_ALPHA_BETA);
    assertEquals(expected, result);
  }

  private void assertEquals(
      DependencyModel expected, DependencyModel actual) {
    assertEquals(expected.getNodeContribs(), actual.getNodeContribs());
    assertEquals(expected.getRelationContribs(), actual.getRelationContribs());
  }

  /**
   * @param relationContribs
   * @param relationContribs2
   */
  private void assertEquals(
      List<String> expected, List<String> actual) {
    if (expected.size() != actual.size()) {
      fail("Lists are not the same length");
    }
    for (int index = 0; index < expected.size(); index++) {
      Assert.assertEquals(expected.get(index), actual.get(index));
    }
  }
}
