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

package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditorInput;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ViewFromGraphDocWizard extends FromGraphDocWizard {

  private ViewFromGraphDocPage page;

  @Override
  public void addPages() {
    PropertyDocumentReference<RelationSetDescriptor> visRelations =
        RelationSetResources.ALL_REF;
    page = new ViewFromGraphDocPage(
        getGraphFile().getProject(), getGraphResources(), visRelations);
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    ViewEditorInput viewInput = buildViewInput();
    ViewEditor.startViewEditor(viewInput);
    return true;
  }

  /**
   * Unpack wizard page controls into a {@link ViewEditorInput}.
   */
  private ViewEditorInput buildViewInput() {
    String basename = calcName();

    // Create ViewDocument elements
    GraphModelReference graphRef =
        new GraphModelReference(getGraphFile(), getGraphDoc());

    ViewPreferences userPrefs = new ViewPreferences();
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher =
        page.getLayoutMatcher();
    userPrefs.setLayoutMatcherRef(matcher);
    userPrefs.setVisibleRelationSet(page.getVisibleRelationSet());

    ViewDocument viewInfo = new ViewDocument(graphRef, getNodes(), userPrefs);

    ViewEditorInput result = new ViewEditorInput(viewInfo, basename);
    result.setInitialLayout(calcInitialLayout());

    return result;
  }

  private LayoutPlan calcInitialLayout() {
    LayoutPlan layout = page.getLayoutPlan();
    if (null != layout) {
      return layout;
    }
    return GridLayoutPlan.GRID_LAYOUT_PLAN;
  }

  private String calcName() {
    Collection<GraphNode> nodes = getNodes();
    if ((null == nodes) || (nodes.isEmpty())) {
      return "Empty View";
    }

    String srcBase = PlatformTools.getBaseName(getGraphFile());
    if (entireGraph()) {
      return srcBase;
    }

    return MessageFormat.format("{0}_{1}", srcBase, getDetail());
  }
}
