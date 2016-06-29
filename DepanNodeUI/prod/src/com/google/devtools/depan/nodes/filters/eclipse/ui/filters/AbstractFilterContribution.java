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

package com.google.devtools.depan.nodes.filters.eclipse.ui.filters;

import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractFilterContribution<T extends ContextualFilter>
    implements ContextualFilterContributor<T> {

  @Override
  public T createElementFilter() {
    String msg = MessageFormat.format(
        "No element factory defined for {0}", getLabel());
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public T createWrapperFilter(ContextualFilter filter) {
    String msg = MessageFormat.format(
        "No wrapper factory defined {0}", getLabel());
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public T createGroupFilter(Collection<ContextualFilter> filters) {
    String msg = MessageFormat.format(
        "No group factory defined for {0}", getLabel());
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public T createStepsFilter(List<ContextualFilter> filters) {
    String msg = MessageFormat.format(
        "No group factory defined for {0}", getLabel());
    throw new UnsupportedOperationException(msg);
  }

  /////////////////////////////////////
  // Utilities for derived types

  protected boolean isAssignableAs(ContextualFilter filter, Class<?> type) {
    return filter.getClass().isAssignableFrom(type);
  }
}
