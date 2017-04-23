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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.platform.eclipse.ui.widgets.MapChoiceControl;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

import com.google.common.collect.Maps;

import org.eclipse.swt.widgets.Composite;

import java.util.List;
import java.util.Map;

/**
 * Provide a ComboViewer dropdown control for registry contributions.
 * @author Lee Carver
 */
public class LayoutGeneratorsControl
    extends MapChoiceControl<LayoutPlanDocument<?>> {

  public LayoutGeneratorsControl(Composite parent) {
    super(parent);

    List<LayoutPlanDocument<?>> topLayouts = LayoutResources.getTopLayouts();

    Map<String, LayoutPlanDocument<?>> layoutMap =
        Maps.newHashMapWithExpectedSize(topLayouts.size());
    for (LayoutPlanDocument<?> layoutDoc : topLayouts) {
      layoutMap.put(layoutDoc.getName(), layoutDoc);
    }

    setInput(getBestLayout(topLayouts), layoutMap);
  }

  private static LayoutPlanDocument<?> getBestLayout(
      List<LayoutPlanDocument<?>> layouts) {
    if (layouts.isEmpty()) {
      return null;
    }
    return layouts.get(0);
  }

  @Override
  protected LayoutPlanDocument<?> coerceResult(Object obj) {
    return (LayoutPlanDocument<?>) obj;
  }
}
