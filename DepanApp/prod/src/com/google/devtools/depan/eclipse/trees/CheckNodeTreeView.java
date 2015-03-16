/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.trees;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> The type of the treeView
 */
public class CheckNodeTreeView<E> extends NodeTreeView<E> {

  public CheckNodeTreeView(Composite parent, int style) {
    super(parent, style);
  }

  @Override
  protected void initWidget(Composite parent, int style) {
    tree = new CheckboxTreeViewer(parent, style);
    tree.setLabelProvider(new WorkbenchLabelProvider());
    tree.setContentProvider(new BaseWorkbenchContentProvider());
  }

  public CheckboxTreeViewer getCheckboxTreeViewer() {
    return (CheckboxTreeViewer) tree;
  }
}
