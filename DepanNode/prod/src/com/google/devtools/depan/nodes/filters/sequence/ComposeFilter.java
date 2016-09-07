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

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ComposeFilter extends BasicFilter {

  private static final String FILTER_NAME_DEFAULT = "Compose filter";

  private ContextualFilter filter;

  private ComposeMode mode;

  public ComposeFilter(ComposeMode mode) {
    super(FILTER_NAME_DEFAULT);
    this.mode = mode;
  }

  public ComposeFilter() {
    this(ComposeMode.UNION);
  }

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return filter.getContextKeys();
  }

  public ContextualFilter getFilter() {
    return filter;
  }

  public void setFilter(ContextualFilter filter) {
    this.filter = filter;
  }

  public ComposeMode getMode() {
    return mode;
  }

  public void setMode(ComposeMode mode) {
    this.mode = mode;
  }

  @Override
  public String buildSummary() {
    String filterName = getFilterName();
    if (null == filterName) {
      return FILTER_NAME_DEFAULT;
    }
    return MessageFormat.format("{0} on {1}", mode.getTitle(), filterName);
  }

  private String getFilterName() {
    if (null == filter) {
      return null;
    }
    String result = filter.getName();
    if (Strings.isNullOrEmpty(result)) {
      return null;
    }
    return result;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    Collection<GraphNode> input = Sets.newHashSet(nodes);

    filter.receiveContext(getFilterContext());
    Collection<GraphNode> compose = filter.computeNodes(input);

    return mode.compose(nodes, compose);
  }
}
