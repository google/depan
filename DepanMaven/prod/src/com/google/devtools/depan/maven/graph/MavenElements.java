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

package com.google.devtools.depan.maven.graph;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provide reference collections for Maven's contributed graph elements.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class MavenElements {

  private MavenElements() {
    // Prevent instantiations.
  }

  public static final Collection<Class<? extends Element>> NODES;
  static {
    NODES = Lists.newArrayList();
    NODES.add(ArtifactElement.class);
    NODES.add(PropertyElement.class);
  }
 
  public static final Collection<? extends Relation> RELATIONS =
      Arrays.asList(MavenRelation.values());
}
