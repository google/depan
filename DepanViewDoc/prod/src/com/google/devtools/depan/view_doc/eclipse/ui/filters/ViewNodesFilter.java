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

package com.google.devtools.depan.view_doc.eclipse.ui.filters;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewNodesFilter extends BasicFilter {

  private static final Collection<? extends ContextKey> KEYS_VIEWDOC =
      Collections.singletonList(ContextKey.Base.VIEWDOC);

  private boolean include;

  public ViewNodesFilter(String name, String summary, boolean include) {
    super(name, summary);
    this.include = include;
  }

  public boolean isInclude() {
    return include;
  }

  @Override
  public Collection<GraphNode> computeNodes(Collection<GraphNode> nodes) {
    ViewEditor editor = (ViewEditor) getContextValue(
        ContextKey.Base.VIEWDOC);
    Collection<GraphNode> viewNodes = editor.getViewGraph().getNodes();

    Collection<GraphNode> result = Lists.newArrayList();
    for (GraphNode node : nodes) {
      if (include == viewNodes.contains(node)) {
        result.add(node);
      }
    }
    return result;
  }

  @Override
  public Collection<? extends ContextKey> getContextKeys() {
    return KEYS_VIEWDOC;
  }
}
