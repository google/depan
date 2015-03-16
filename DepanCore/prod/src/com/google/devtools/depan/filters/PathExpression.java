/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.filters;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.List;

/**
 * A filtering model where one can specify a set of
 * <code>PathMatcher</code> filters that are applied in order to a
 * list of nodes that are provided by a <code>GraphModel</code> object.
 * 
 * @author tugrul@google.com (Tugrul Ince)
 */
public class PathExpression implements PathMatcher {
  /**
   * A list of <code>PathMatcher</code> filters that will be used during
   * filtering.
   */
  private final List<PathMatcherTerm> matchers = Lists.newArrayList();

  /**
   * Adds a new <code>PathMatcher</code> to the list of current filters.
   * 
   * @param matcher The <code>RelationshipSetMatcher</code> to be added to the 
   * ordered list of this object
   * @return true if successful; false otherwise
   */
  public boolean addPathMatcher(PathMatcherTerm matcher) {
    return matchers.add(matcher);
  }

  /**
   * Returns the String representation of this object. The returned string is 
   * the same as the output of <code>String toString()</code>.
   * 
   *  @return Returns the <code>String</code> representation of this object.
   */
  @Override
  public String getDisplayName() {
    return toString();
  }
  
  /**
   * Computes a list of nodes that are the output of this filter, given a set of
   * nodes as input.
   * 
   * @param graph The model that contains the relations between nodes.
   * @param input A collection of <code>GraphNode</code> objects given as input.
   * @return A collection of <code>GraphNode</code> objects that is the result
   * of applying this filter to the input.
   */
  @Override
  public Collection<GraphNode> nextMatch(GraphModel graph,
      Collection<GraphNode> input) {
    /*
     * nextTermInput denotes the set of nodes that will be fed to the
     * next matcher. When all matchers are processed, it will be returned
     * to the user
     */
    Collection<GraphNode> nextTermInput = Sets.newHashSet();
    nextTermInput.addAll(input);
    
    for (PathMatcherTerm pathMatcher : matchers) {
      /*
       *  localInput is the set of nodes that will be fed to this matcher
       *  only at this iteration. Unlike nextTermInput, the nodes in localInput
       *  is removed before next iteration. Therefore, localInput only contains
       *  nodes that have never been processed by this matcher
       */
      Collection<GraphNode> localInput = Sets.newHashSet();
      localInput.addAll(nextTermInput);

      /*
       * termOutput is the set of nodes that holds all nodes that were in the
       * output set of this matcher at some iteration. The results are
       * accumulated by traversing localOutput which contains the set of nodes
       * for this iteration.
       */
      Collection<GraphNode> termOutput = Sets.newHashSet();
      
      boolean loop = false;
      do {
        loop = false;
        boolean elementAdded = false;
        /*
         * localOutput is the set of nodes that are output by this matcher only
         * at this iteration.
         */
        Collection<GraphNode> localOutput = 
            pathMatcher.getPathMatcher().nextMatch(graph, localInput);
        for (GraphNode node : localOutput) {
          // do this in two steps to avoid short-cut confusions!
          boolean added = termOutput.add(node);
          elementAdded = elementAdded || added;
        }
        /*
         * if any elements added in previous step and if this matcher has to be
         * applied recursively, set loop variable so that we execute the loop
         * body again.
         */
        if (elementAdded && pathMatcher.isRecursive()) {
          localInput = localOutput;
          loop = true;
        }
      } while (loop);
      
      /*
       * If cumulative, add the input and output of this term and feed it to the
       * next term (or return if no more terms)
       */
      if (pathMatcher.isCumulative()) {
        nextTermInput.addAll(termOutput);
      } else {
        nextTermInput = termOutput;
      }
    } // end of matchers iteration
    return nextTermInput;
  }
}
