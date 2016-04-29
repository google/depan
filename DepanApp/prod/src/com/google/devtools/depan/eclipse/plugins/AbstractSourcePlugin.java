/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.plugins;

import com.google.devtools.depan.eclipse.utils.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.relations.RelationSetDescriptor;

import com.google.common.collect.Lists;
import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

import java.util.Collection;

/**
 * Provides a standard implementation of SourcePlugin, although most methods
 * must be defined by each plugin.
 * 
 * Also provides some backward support for EdgeMatchers.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractSourcePlugin implements SourcePlugin {
  private Collection<GraphEdgeMatcherDescriptor> builtinEdgeMatchers;
  GraphEdgeMatcherDescriptor builtinDefaultEdgeMatcher;

  public void setupEdgeMatchers() {
    setupEdgeMatchers(getBuiltinRelationshipSets(), getDefaultRelationSetDescriptor());
  }

  public void setupEdgeMatchers(
      Collection<? extends RelationSetDescriptor> knownRelationSets,
      RelationSetDescriptor defaultRelationSet) {

    builtinEdgeMatchers =
        Lists.newArrayListWithExpectedSize(knownRelationSets.size());

    for (RelationSetDescriptor relationSet : knownRelationSets) {
      GraphEdgeMatcherDescriptor edgeMatcher =
          GraphEdgeMatcherDescriptors.buildRelationSetMatcher(relationSet);
      builtinEdgeMatchers.add(edgeMatcher);
      if (relationSet == defaultRelationSet) {
        builtinDefaultEdgeMatcher = edgeMatcher;
      }
    }
  }

  @Override
  public Collection<? extends GraphEdgeMatcherDescriptor> getBuiltinEdgeMatchers() {
    return builtinEdgeMatchers;
  }

  @Override
  public GraphEdgeMatcherDescriptor getDefaultEdgeMatcherDescriptor() {
    return builtinDefaultEdgeMatcher;
  }
}
