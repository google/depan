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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FilterEditorDialog<T extends ContextualFilter> extends Dialog {

  private final T filter;

  /**
   * @param parentShell
   */
  protected FilterEditorDialog(Shell parentShell, T filter) {
    super(parentShell);
    this.filter = filter;
  }

  protected T getFilter() {
    return filter;
  }
}
