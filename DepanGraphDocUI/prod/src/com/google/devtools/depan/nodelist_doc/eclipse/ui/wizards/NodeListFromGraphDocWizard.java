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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditor;
import com.google.devtools.depan.nodelist_doc.eclipse.ui.editor.NodeListEditorInput;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;

import org.eclipse.core.resources.IFile;

import java.text.MessageFormat;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListFromGraphDocWizard extends FromGraphDocWizard {

  private NodeListFromGraphDocPage page;

  @Override
  public void addPages() {
    page = new NodeListFromGraphDocPage(getGraphResources());
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    NodeListEditorInput input = buildNodeListInput();
    NodeListEditor.startNodeListEditor(input);
    return true;
  }

  /**
   * Unpack wizard page controls into a {@link ViewEditorInput}.
   */
  private NodeListEditorInput buildNodeListInput() {
    String basename = calcName();

    // Create ViewDocument elements
    GraphModelReference graphRef =
        new GraphModelReference(getGraphFile(), getGraphDoc());

    NodeListDocument viewInfo = new NodeListDocument(graphRef, getNodes());

    NodeListEditorInput result = new NodeListEditorInput(viewInfo, basename);

    return result;
  }

  private String calcName() {
    String srcBase = getSourceBase();
    if (entireGraph()) {
      return srcBase;
    }
    GraphNode node = getTopNode();
    if (null == node) {
      return "Empty NodeList";
    }

    String detail = calcDetailName(node);
    return MessageFormat.format("{0}_{1}", srcBase, detail);
  }

  private String calcDetailName(GraphNode node) {
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

  /**
   * Indicate if the selected nodes match the nodes from the graph document.
   */
  private boolean entireGraph() {
    if (null == getTopNode()) {
      return false;
    }
    if (null == getNodes()) {
      return false;
    }
    return getNodes().size() == getGraphDoc().getGraph().getNodes().size();
  }

  private String getSourceBase() {
    IFile graph = getGraphFile();
    String name = graph.getName();
    String ext = graph.getFileExtension();
    // If null, no period is present
    if (null == ext) {
      return name;
    }
    // remove period and extension from end
    return name.substring(0, name.length() - 1 - ext.length());
  }
}
