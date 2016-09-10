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

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import com.google.common.collect.Sets;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Filter nodes by executing the individual {@link ContextualFilter}s
 * in a list in order.
 * 
 * Editors (e.g. {@code FilterTableEditorControl}) need to be aware that
 * the internal list of steps from {@link #getSteps()} may not be editable.
 * Direct access to the internal structure helps a bit with rendering.
 * Updates to the steps should always use {@link #setSteps(List)}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class SteppingFilter extends BasicFilter {

  public static final String FILTER_NAME_DEFAULT = "Filter sequence";

  /**
   * Sequence of filters to execute.
   */
  private List<ContextualFilter> steps = Collections.emptyList();

  public SteppingFilter() {
    this(FILTER_NAME_DEFAULT);
  }

  public SteppingFilter(String name) {
    super(name);
  }

  public SteppingFilter(String name, String summary) {
    super(name, summary);
  }

  public List<ContextualFilter> getSteps() {
    return steps;
  }

  public void setSteps(List<ContextualFilter> steps) {
    this.steps = steps;
  }

  /////////////////////////////////////
  // ContextualFilter methods

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    Collection<GraphNode> result = nodes;
    for (ContextualFilter filter : steps) {
      filter.receiveContext(getFilterContext());
      result = filter.computeNodes(result);
    }
    return result;
  }

  /**
   * Provides the aggregate of all the context keys from each step.
   */
  @Override
  public Collection<ContextKey> getContextKeys() {
    Collection<ContextKey> result = Sets.newHashSet();
    for (ContextualFilter filter : steps) {
      result.addAll(filter.getContextKeys());
    }
    return result;
  }

  /////////////////////////////////////
  // BasicFilter methods

  /**
   * @return
   */
  @Override
  public String buildSummary() {
    return MessageFormat.format("Sequence of {0} filter steps", steps.size());
  }
}
