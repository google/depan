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

package com.google.devtools.depan.resources;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ResourceContainerTest {

  @Test
  public void testRootConstructor() {
    ResourceContainer testRoot =
        ResourceContainer.buildRootContainer("root");
    assertContainer(null, "root", testRoot);
  }

  @Test
  public void testOneChild() {
    ResourceContainer testRoot =
        ResourceContainer.buildRootContainer("root");
    assertContainer(null, "root", testRoot);

    ResourceContainer testAdd = testRoot.addChild("child");
    assertContainer(testRoot, "child", testAdd);

    ResourceContainer testFind = testRoot.getChild("child");
    assertContainer(testRoot, "child", testFind);
    assertSame(testAdd, testFind);
  }

  @Test
  public void testGetPath() {
    ResourceContainer testRoot =
        ResourceContainer.buildRootContainer("root");
    ResourceContainer testOne = testRoot.addChild("one");
    ResourceContainer testTwo = testOne.addChild("two");
    assertContainer(null, "root", testRoot);
    assertContainer(testRoot, "one", testOne);
    assertContainer(testOne, "two", testTwo);

    IPath testResult = testTwo.getPath();
    assertEquals(3, testResult.segmentCount());
    assertEquals("root", testResult.segment(0));
    assertEquals("one", testResult.segment(1));
    assertEquals("two", testResult.segment(2));

    // Early implementations modified the parent for the receiving instance.
    // Verify container properties have not changed.
    assertContainer(null, "root", testRoot);
    assertContainer(testRoot, "one", testOne);
    assertContainer(testOne, "two", testTwo);
  }

  private void assertContainer(
      ResourceContainer expectedParent,
      String expectedLabel,
      ResourceContainer testResource) {
    assertEquals(expectedParent, testResource.getParent());
    assertEquals(expectedLabel, testResource.getLabel());
  }
}
