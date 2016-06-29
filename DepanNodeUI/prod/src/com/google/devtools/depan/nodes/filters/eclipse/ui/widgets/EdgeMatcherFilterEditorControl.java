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

import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherEditorControl;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadControl;
import com.google.devtools.depan.matchers.eclipse.ui.wizards.NewEdgeMatcherWizard;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.nodes.filters.sequence.EdgeMatcherFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import java.net.URI;
import java.text.MessageFormat;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherFilterEditorControl extends Composite {

  /**
   * {@link EdgeMatcherFilter} document that is being edited.
   */
  @SuppressWarnings("unused") // It's likely to be used in the future.
  private EdgeMatcherFilter editFilter;

  /////////////////////////////////////
  // UX Elements

  private final BasicFilterEditorControl basicControl;

  private EdgeMatcherEditorControl edgeMatcherEditor;

  /////////////////////////////////////
  // Public methods

  public EdgeMatcherFilterEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Handle common stuff, like name and summary
    basicControl = new BasicFilterEditorControl(this);
    basicControl.setLayoutData(Widgets.buildHorzFillData());

    Composite matchEditor = setupEdgeMatcherEditor(this);
    matchEditor.setLayoutData(Widgets.buildGrabFillData());
  }

  public void setInput(EdgeMatcherFilter editFilter) {
    this.editFilter = editFilter;
    basicControl.setInput(editFilter);

    edgeMatcherEditor.updateTable(RelationRegistry.getRegistryRelations());
    edgeMatcherEditor.updateEdgeMatcher(editFilter.getEdgeMatcher());
  }

  public EdgeMatcherFilter buildEdgeMatcherFilter() {
    GraphEdgeMatcher matcher = edgeMatcherEditor.buildEdgeMatcher();
    EdgeMatcherFilter result = new EdgeMatcherFilter(matcher);
    result.setName(basicControl.getFilterName());
    result.setSummary(basicControl.getFilterSummary());
    return result;
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
    protected Wizard getSaveWizard() {
      GraphEdgeMatcher matcher = edgeMatcherEditor.buildEdgeMatcher();
      String label = MessageFormat.format(
          "{0} filter", basicControl.getFilterName());
      GraphEdgeMatcherDescriptor target =
          new GraphEdgeMatcherDescriptor(label, matcher);
      return new NewEdgeMatcherWizard(target);
    }

    @Override
    protected void loadURI(URI uri) {
      GraphEdgeMatcherDescriptor loadMatcher = loadEdgeMatcherDoc(uri);
      edgeMatcherEditor.updateEdgeMatcher(loadMatcher.getEdgeMatcher());
    }
  }
}
