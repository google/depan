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

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;

import org.eclipse.swt.widgets.Composite;

/**
 * Concrete {@link FilterEditorControl} for a {@link BasicFilter}.
 * The {@link #setInput(BasicFilter)} filter is edited in place.
 * Use the {@link #buildFilter()} method to obtain a value consistent
 * with the current UI changes.
 * 
 * Suitable for {@link ContextualFilter}s that have no user-editable
 * properties beyond name and summary.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
class SimpleFilterEditorControl<T extends BasicFilter>
    extends FilterEditorControl<T> {

  public SimpleFilterEditorControl(Composite parent) {
    super(parent);
  }

  @Override
  protected void setupControls(Composite parent) {
    // No additional controls.
  }

  @Override
  protected void updateControls() {
    // No additional controls.
  }
}
