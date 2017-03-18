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

package com.google.devtools.depan.graph_doc.eclipse.ui.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.devtools.depan.graph_doc.model.DependencyModel;

import org.junit.Test;

import java.util.Collections;

/**
 * More an integration test, since it assumes several static data services
 * are available.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TestGraphResourceBuilder {

  @Test
  public void testStandardUsage() {
    DependencyModel model = DependencyModel.createFromRegistry();
    assertNotNull(model);
    GraphResources rcrs = GraphResourceBuilder.forModel(model);
    assertNotNull(rcrs);
  }

  /**
   * More an integration test, since it assume some several static data
   * services are available.
   */
  @Test
  public void testEmptyModel() {
    DependencyModel model = new DependencyModel(
        Collections.<String>emptyList(), Collections.<String>emptyList());

    // Even with an empty model, some resources should be available.
    GraphResources rcrs = GraphResourceBuilder.forModel(model);
    assertNotNull(rcrs);

    assertNotNull(rcrs.getDefaultEdgeMatcher());
    assertFalse(rcrs.getEdgeMatcherChoices().isEmpty());
    assertTrue(rcrs.getEdgeMatcherChoices().contains(rcrs.getDefaultEdgeMatcher()));

    assertNotNull("No default relation set",
        rcrs.getDefaultRelationSet());
    assertFalse(rcrs.getRelationSetsChoices().isEmpty());
    assertTrue(rcrs.getRelationSetsChoices().contains(rcrs.getDefaultRelationSet()));
  }
}
