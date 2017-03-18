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

import com.google.devtools.depan.graph.api.EdgeMatcher;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherEditorControl;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.nodes.filters.sequence.EdgeMatcherFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * Enhances {@link EdgeMatcherFilter} editing with a
 * {@link EdgeMatcherEditorControl}.
 * 
 * Includes save-load support for the embedded {@link GraphEdgeMatcher}
 * (e.g. {@link GraphEdgeMatcherDescriptor}).
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherFilterEditorControl
    extends FilterEditorControl<EdgeMatcherFilter> {

  /////////////////////////////////////
  // UX Elements

  EdgeMatcherEditorControl edgeMatcherEditor;

  /////////////////////////////////////
  // Public methods

  public EdgeMatcherFilterEditorControl(Composite parent) {
    super(parent);
  }

  @Override
  public EdgeMatcherFilter buildFilter() {
    GraphEdgeMatcher matcher = edgeMatcherEditor.buildEdgeMatcher();
    EdgeMatcherFilter result = new EdgeMatcherFilter(matcher);
    updateBasicFields(result);
    return result;
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void setupControls(Composite parent) {
    Composite matchEditor = setupEdgeMatcherEditor(this);
    matchEditor.setLayoutData(Widgets.buildGrabFillData());
  }

  @Override
  protected void updateControls() {
    Collection<Relation> projectRelations =
        RelationRegistry.getRegistryRelations(getModel().getRelationContribs());
    edgeMatcherEditor.updateTable(projectRelations);
    edgeMatcherEditor.updateEdgeMatcher(getFilter().getEdgeMatcher());
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupEdgeMatcherEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Edge Matcher", 1);

    Composite saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());

    // relation picker (list of relationships with forward/backward selectors)
    edgeMatcherEditor = new EdgeMatcherEditorControl(result);
    edgeMatcherEditor.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  /////////////////////////////////////
  // Integration classes

  private class ControlSaveLoadControl
      extends EdgeMatcherSaveLoadControl {

    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected IProject getProject() {
      return EdgeMatcherFilterEditorControl.this.getProject();
    }

    @Override
    protected GraphEdgeMatcherDescriptor buildSaveResource() {
      EdgeMatcherFilter filter = buildFilter();
      String label = MessageFormat.format( "{0} filter", filter.getName());
      return new GraphEdgeMatcherDescriptor(
          label, getModel(), filter.getEdgeMatcher());
    }

    @Override
    protected void installLoadResource(
        PropertyDocumentReference<GraphEdgeMatcherDescriptor> ref) {
      if (null != ref) {
        EdgeMatcher<String> matcher =  ref.getDocument().getInfo();
        edgeMatcherEditor.updateEdgeMatcher(matcher);
      }
    }
  }
}
