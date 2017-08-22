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

package com.google.devtools.depan.cmd.setops;

import com.google.devtools.depan.cmd.CmdLogger;
import com.google.devtools.depan.cmd.dispatch.AbstractCommandExec;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.operations.SubtractNodes;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListDocXmlPersist;

import java.io.File;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class SubtractCommand extends AbstractCommandExec {

  @Override
  public void exec() {
    if (getArgs().size() < 2) {
      CmdLogger.LOG.warn(
          "The subtract command requires at least two arguments:"
          + " a destination and a base graph model term (the minuend)."
          + " Nodes from additional terms are removed from the result.");
    }

    String outName = getParm(0);
    File baseFile = new File(getParm(1));
    String baseName = baseFile.getName();
    File outFile = new File(baseFile.getParentFile(), outName);

    GraphDocument baseDoc = buildGraphDoc(baseFile.toURI());

    SubtractNodes subtract = new SubtractNodes(baseDoc.getGraph());
    for (String name : getParmsAfter(1)) {
      GraphDocument subtractDoc = buildGraphDoc(name);
      if (null == subtractDoc) {
        continue;
      }
      subtract.subtract(subtractDoc.getGraph());
    }

    GraphModelReference parentGraph =
        new GraphModelReference(baseName, baseDoc);
    NodeListDocument nodeListDoc =
        new NodeListDocument(parentGraph, subtract.getNodes());
    NodeListDocXmlPersist persist = NodeListDocXmlPersist.buildForSave();
    persist.save(outFile.toURI(), nodeListDoc);
  }
}
