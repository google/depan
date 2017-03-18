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

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.NodeKindTableControl.ElementKindDescriptor;
import com.google.devtools.depan.nodes.filters.model.NodeKindDocument;
import com.google.devtools.depan.nodes.filters.sequence.NodeKindFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import com.google.common.collect.ImmutableList;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * Enhances {@link NodeKindFilter} editing with a
 * {@link NodeKindTableControl}.
 * 
 * Includes save-load support for the embedded collection
 * of {@code Element} classes (e.g. {@link NodeKindDocument}).
 * 
 * Based on an earlier {@code ElementKindSelectorTool}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeKindFilterEditorControl
    extends FilterEditorControl<NodeKindFilter> {

  /////////////////////////////////////
  // UX Elements

  /** Control for selecting Element Kinds. */
  private NodeKindTableControl nodeTable;

  /////////////////////////////////////
  // Public methods

  /**
   * Construct the UI for element kind selection.
   * 
   * @param parent container windows
   * @param style standard window style
   */
  public NodeKindFilterEditorControl(Composite parent) {
    super(parent);
  }

  @Override
  public NodeKindFilter buildFilter() {
    NodeKindFilter result = new NodeKindFilter(
        nodeTable.getSelectedElementKindSet());
    updateBasicFields(result);
    return result;
  }

  @Override
  public void dispose() {
    nodeTable.dispose();
    super.dispose();
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void updateControls() {
    nodeTable.setInput(
        ImmutableList.<NodeKindTableControl.ElementKindDescriptor>of());
    Collection<ElementKindDescriptor> selection =
        nodeTable.findDescriptors(getFilter().getNodeKinds());
    nodeTable.setSelection(selection);
  }

  @Override
  protected void setupControls(Composite parent) {
    Composite editor = setupNodeKindEditor(this);
    editor.setLayoutData(Widgets.buildGrabFillData());
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupNodeKindEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Node Kind Matcher", 1);

    Composite saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());

    // relation picker (list of relationships with forward/backward selectors)
    nodeTable = new NodeKindTableControl(this);
    nodeTable.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  /////////////////////////////////////
  // Integration classes

  private class ControlSaveLoadControl
      extends NodeKindSaveLoadControl {

    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected IProject getProject() {
      return NodeKindFilterEditorControl.this.getProject();
    }

    @Override
    protected NodeKindDocument buildSaveResource() {
      NodeKindFilter filter = buildFilter();
      String label = MessageFormat.format("{0} kinds", filter.getName());
      return new NodeKindDocument(label, getModel(), filter.getNodeKinds());
    }

    @Override
    protected void installLoadResource(
        PropertyDocumentReference<NodeKindDocument> ref) {
      if (null != ref) {
        Collection<Class<? extends Element>> kinds = ref.getDocument().getInfo();
        Collection<ElementKindDescriptor> selection =
            nodeTable.findDescriptors(kinds);
        nodeTable.setSelection(selection);
      }
    }
  }
}
