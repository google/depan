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

package com.google.devtools.depan.graph_doc.eclipse.ui.plugins;

import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.Wizard;

import java.util.Collection;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class FromGraphDocWizard extends Wizard {

  private IFile graphFile;
  private GraphDocument graphDoc;
  private GraphResources graphResources;
  private Collection<GraphNode> nodes;
  private String detail;

  public void init(
      IFile graphFile, GraphDocument graphDoc, GraphResources graphResources,
      Collection<GraphNode> nodes, String detail) {
    this.graphFile = graphFile;
    this.graphDoc = graphDoc;
    this.graphResources = graphResources;
    this.nodes = nodes;
    this.detail = detail;
  }

  public IFile getGraphFile() {
    return graphFile;
  }

  public GraphDocument getGraphDoc() {
    return graphDoc;
  }

  protected GraphResources getGraphResources() {
    return graphResources;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }

  public String getDetail() {
    return detail;
  }

  /**
   * Indicate if the selected nodes match the nodes from the graph document.
   */
  protected boolean entireGraph() {
    if (null == getNodes()) {
      return false;
    }
    return getNodes().size() == getGraphDoc().getGraph().getNodes().size();
  }

  public static String calcDetailName(GraphNode node) {
    String baseName = node.friendlyString();
    int period = baseName.lastIndexOf('.');
    if (period > 0) {
      String segment = baseName.substring(period + 1);
      if (segment.length() > 3) {
        return segment;
      }
    }

    return baseName;
  }
}
