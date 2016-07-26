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

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * Re-usable base for filter contributions that want to be flexible about
 * the input.  Simple derived types typically override
 * {@link #createElementFilter()}.
 * 
 * If the derived types wants all singleton groups or steps handled as
 * wrapped contributions, the {@link #createWrapperFilter(ContextualFilter)}
 * method must be overridden.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class DefaultingFilterContribution<T extends ContextualFilter>
    extends AbstractFilterContribution<T> {

  @Override
  public T createGroupFilter(Collection<ContextualFilter> filters) {
    if (1 == filters.size()) {
      return createWrapperFilter(filters.iterator().next());
    }
    String msg = MessageFormat.format(
        "Group for {0} must be a singleton, has {1} elements",
        getLabel(), filters.size());
    throw new UnsupportedOperationException(msg);
  }

  @Override
  public T createStepsFilter(List<ContextualFilter> filters) {
    return createGroupFilter(filters);
  }
}
