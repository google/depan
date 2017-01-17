/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.plugins.NodeElementPluginRegistry;
import com.google.devtools.depan.model.Element;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NodeSorter extends ViewerComparator {

  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    if ((e1 instanceof Element) && (e2 instanceof Element)) {
      return NodeElementPluginRegistry.compare((Element) e1, (Element)e2);
    }
    return super.compare(viewer, e1, e2);
  }

  // TODO(leeca): add category determination to ContributionRegistry
  @Override
  public int category(Object element) {
    if (element instanceof Element) {
      Integer result =
          NodeElementPluginRegistry.getCategory((Element) element);
      if (null != result) {
        return result;
      }
    }
    return super.category(element);
  }
}
