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

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.FilterContext;

import java.util.Collection;
import java.util.Collections;

/**
 * A basic, mutable {@link ContextualFilter} for many concrete types
 * to use as their base.
 * 
 * The name property should never be {@code null}.  If the summary
 * is null, a default summary is derived from the instance's parameters.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class BasicFilter implements ContextualFilter {

  public static final Collection<? extends ContextKey> KEYS_UNIVERSE =
      Collections.singletonList(ContextKey.Base.UNIVERSE);

  private String name;

  private String summary;

  /**
   * Context most recently received.  Never saved as part of filter.
   */
  private transient FilterContext context;

  public BasicFilter() {
    this("- unnamed -");
  }

  public BasicFilter(String name) {
    this(name, null);
  }

  public BasicFilter(String name, String summary) {
    this.name = name;
    this.summary = summary;
  }

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return Collections.emptyList();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSummary() {
    if (null != summary) {
      return summary;
    }
    return buildSummary();
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Override
  public void receiveContext(FilterContext context) {
    this.context = context;
  }

  /**
   * Describe this {@link ContextualFilter} based on it's parameters.
   * Derived types are encouraged to override this method.
   * 
   * This is public so UX elements can manipulate the result
   * before assigning an explicit summary.
   */
  public String buildSummary() {
    return "- empty summary -";
  }

  /////////////////////////////////////
  // Context tools for derived types

  protected FilterContext getFilterContext() {
    return context;
  }

  /**
   * Deliver the value for the supplied {@link ContextKey}.
   * The result may be {@code null}.
   */
  protected Object getContextValue(ContextKey key) {
    return context.get(key);
  }

  /**
   * Super-common case.
   */
  protected GraphModel getContextUniverse() {
    return (GraphModel) getContextValue(ContextKey.Base.UNIVERSE);
  }
}
