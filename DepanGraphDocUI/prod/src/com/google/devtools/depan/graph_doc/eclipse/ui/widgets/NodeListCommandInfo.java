/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.widgets;

import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NodeListCommandInfo {

  private final IFile file;

  private final GraphDocument graph;

  private final GraphResources graphResources;

  private final Shell shell;

  public NodeListCommandInfo(
      IFile file, GraphDocument graph,
      GraphResources graphResources, Shell shell) {
    this.file = file;
    this.graph = graph;
    this.graphResources = graphResources;
    this.shell = shell;
  }

  public GraphModel getGraphModel() {
    return graph.getGraph();
  }

  public HierarchyCache<GraphNode> buildHierachyCache() {
    return new HierarchyCache<GraphNode>(
        NodeTreeProviders.GRAPH_NODE_PROVIDER, getGraphModel());
  }

  public void runWizard(
      FromGraphDocContributor choice,
      GraphNode topNode, Collection<GraphNode> nodes) {

    // Prepare the wizard.
    FromGraphDocWizard wizard = choice.newWizard();
    String name = FromGraphDocWizard.calcDetailName(topNode);
    wizard.init(file, graph, graphResources, nodes, name);

    // Run the wizard.
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }
}
