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
import com.google.devtools.depan.platform.PlatformTools;

import java.text.MessageFormat;
import java.util.Collection;

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
    Collection<GraphNode> nodes = getNodes();
    if ((null == nodes) || (nodes.isEmpty())) {
      return "Empty NodeList";
    }

    String srcBase = PlatformTools.getBaseName(getGraphFile());
    if (entireGraph()) {
      return srcBase;
    }

    return MessageFormat.format("{0}_{1}", srcBase, getDetail());
  }
}
