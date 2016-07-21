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
import com.google.devtools.depan.view_doc.layout.plugins.LayoutGeneratorContributor;
import com.google.devtools.depan.view_doc.layout.plugins.LayoutGeneratorRegistry;

import org.eclipse.swt.widgets.Composite;

import java.util.Map;

/**
 * Provide a ComboViewer dropdown control for registry contributions.
 * @author Lee Carver
 */
public class LayoutGeneratorsControl
    extends MapChoiceControl<LayoutGeneratorContributor> {

  public LayoutGeneratorsControl(Composite parent) {
    super(parent);

    Map<String, LayoutGeneratorContributor> layouts =
        LayoutGeneratorRegistry.getRegistryContributionMap();

    setInput(getBestLayout(layouts), layouts);
  }

  private static LayoutGeneratorContributor getBestLayout(
      Map<String, LayoutGeneratorContributor> contribs) {
    if (contribs.isEmpty()) {
      return null;
    }
    return contribs.values().iterator().next();
  }

  @Override
  protected LayoutGeneratorContributor coerceResult(Object obj) {
    return (LayoutGeneratorContributor) obj;
  }
}
