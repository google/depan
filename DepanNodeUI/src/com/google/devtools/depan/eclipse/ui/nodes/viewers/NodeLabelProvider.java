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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPluginRegistry;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NodeLabelProvider extends LabelProvider implements
    ITableLabelProvider {

  @Override
  public Image getColumnImage(Object element, int columnIndex) {
    if (element instanceof GraphNode) {
      return NodeElementPluginRegistry.getImage((GraphNode) element);
    }
    return null;
  }

  @Override
  public String getColumnText(Object element, int columnIndex) {
    if (element instanceof GraphNode) {
      return getColumnText((GraphNode) element, columnIndex);
    }
    return null;
  }

  public String getColumnText(GraphNode element, int columnIndex) {
    if (0 == columnIndex) {
      return element.friendlyString();
    }
    return "";
  }
}
