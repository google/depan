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

package com.google.devtools.depan.view_doc.eclipse.ui.widgets;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocRegistry;
import com.google.devtools.depan.platform.eclipse.ui.widgets.MapChoiceControl;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.FromViewDocContributor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.FromViewDocRegistry;

import com.google.common.collect.Maps;

import org.eclipse.swt.widgets.Composite;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Since this control traffics in two independent contributions,
 * have to handle type coercion external to this class.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FromViewDocListControl
    extends MapChoiceControl<Object> {

  public FromViewDocListControl(Composite parent) {
    super(parent);

    Map<String, FromViewDocContributor> byView =
        FromViewDocRegistry.getRegistryContributionMap();
    Map<String, FromGraphDocContributor> byNodes =
        FromGraphDocRegistry.getRegistryContributionMap();

    Map<String, Object> choices = Maps.newHashMap();
    choices.putAll(byView);
    for (Entry<String, FromGraphDocContributor> byNode : byNodes.entrySet()) {
      choices.put(byNode.getKey() + " (nodes)", byNode.getValue());
    }

    setInput(getBestFrom(choices), choices);
  }

  public void selectFrom(FromGraphDocContributor choice) {
    Map<String, FromGraphDocContributor> froms =
        FromGraphDocRegistry.getRegistryContributionMap();
    setSelection(buildSelection(choice, froms));
  }

  private static Object getBestFrom(
      Map<String, Object> contribs) {
    if (contribs.isEmpty()) {
      return null;
    }
    return contribs.values().iterator().next();
  }

  /**
   * Due to mixed types, calls must distinguish result processing.
   */
  @Override
  protected Object coerceResult(Object obj) {
    return obj;
  }
}
