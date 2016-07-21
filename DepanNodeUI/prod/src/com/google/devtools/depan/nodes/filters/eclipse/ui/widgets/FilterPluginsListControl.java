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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterContributor;
import com.google.devtools.depan.nodes.filters.eclipse.ui.plugins.ContextualFilterRegistry;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.MapChoiceControl;

import org.eclipse.swt.widgets.Composite;

import java.util.Map;

/**
 * @author Lee Carver
 */
public class FilterPluginsListControl
    extends MapChoiceControl<ContextualFilterContributor<? extends ContextualFilter>> {
  
  public FilterPluginsListControl(Composite parent) {
    super(parent);

    Map<String, ContextualFilterContributor<? extends ContextualFilter>>
        filters = ContextualFilterRegistry.getRegistryContributionMap();
    setInput(getBestFrom(filters), filters);
  }

  private static
  ContextualFilterContributor<? extends ContextualFilter> getBestFrom(
      Map<String,
      ContextualFilterContributor<? extends ContextualFilter>> contribs) {
    if (contribs.isEmpty()) {
      return null;
    }
    return contribs.values().iterator().next();
  }


  @Override
  @SuppressWarnings("unchecked")
  protected ContextualFilterContributor<? extends ContextualFilter> coerceResult(Object obj) {
    return (ContextualFilterContributor<? extends ContextualFilter>) obj;
  }
}
