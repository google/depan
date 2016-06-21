/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.NodeKindTableControl.ElementKindDescriptor;
import com.google.devtools.depan.nodes.filters.sequence.NodeKindFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import com.google.common.collect.ImmutableList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

/**
 * Based on an earlier {@code ElementKindSelectorTool}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindFilterEditorControl extends Composite {

  /** Control for selecting Element Kinds. */
  private NodeKindTableControl nodeTable;

  /**
   * Construct the UI for element kind selection.
   * 
   * @param parent container windows
   * @param style standard window style
   */
  public NodeKindFilterEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Top: element kind selection
    nodeTable = new NodeKindTableControl(this);
    nodeTable.setLayoutData(Widgets.buildHorzFillData());
  }

  /**
   * Update all the UI values to the settings for the current 
   * {@code ViewEditor}.
   * 
   * @param editor source of settings for UI configuration
   * @param input 
   */
  public void setInput(NodeKindFilter input) {
    // TODO: Should be universe of known node types.
    nodeTable.setInput(
        ImmutableList.<NodeKindTableControl.ElementKindDescriptor>of());
    Collection<ElementKindDescriptor> selection =
        nodeTable.findDescriptors(input.getNodeKinds());
    nodeTable.setSelection(selection);
  }

  @Override
  public void dispose() {
    nodeTable.dispose();
    super.dispose();
  }
}
