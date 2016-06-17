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

package com.google.devtools.depan.graph_doc.eclipse.ui.registry;

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
  private GraphNode topNode;
  private Collection<GraphNode> nodes;

  public void init(
      IFile graphFile, GraphDocument graphDoc,
      GraphNode topNode, Collection<GraphNode> nodes) {
    this.graphFile = graphFile;
    this.graphDoc = graphDoc;
    this.topNode = topNode;
    this.nodes = nodes;
  }

  public IFile getGraphFile() {
    return graphFile;
  }

  public GraphDocument getGraphDoc() {
    return graphDoc;
  }

  public GraphNode getTopNode() {
    return topNode;
  }

  public Collection<GraphNode> getNodes() {
    return nodes;
  }
}
