/*
 * Copyright 2008 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.platform;

import org.eclipse.jface.viewers.ITableLabelProvider;

/**
 * Adapt a LabelProvider to provide sortable text.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class LabelProviderToString implements ViewerObjectToString {

  private final ITableLabelProvider labelProvider;
  private final int sortColumn;

  /**
   * @param labelProvider
   * @param sortColumn
   */
  public LabelProviderToString(
      ITableLabelProvider labelProvider, int sortColumn) {
    this.labelProvider = labelProvider;
    this.sortColumn = sortColumn;
  }

  @Override
  public String getString(Object object) {
    return labelProvider.getColumnText(object, sortColumn);
  }
}