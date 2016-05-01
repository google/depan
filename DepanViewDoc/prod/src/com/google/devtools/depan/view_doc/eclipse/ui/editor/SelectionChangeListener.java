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

package com.google.devtools.depan.view_doc.eclipse.ui.editor;

import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;

/**
 * A listener to notify when a set of nodes associated with a {@link ViewEditor}
 * have their selection state changed.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public interface SelectionChangeListener {

  /**
   * Notify that the given set of node was selected.
   */
  public void extendSelection(Collection<GraphNode> extension);

  /**
   * Notify that the given set of node was unselected.
   */
  public void reduceSelection(Collection<GraphNode> reduction);
}
