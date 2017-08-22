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
import com.google.devtools.depan.graph_doc.operations.MergeGraphDoc;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;

import java.net.URI;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class UnionCommand extends AbstractCommandExec {

  @Override
  public void exec() {
    if (getArgs().size() < 2) {
      CmdLogger.LOG.warn(
          "The union command requires a destination and at least one input");
    }

    URI output = buildLocation(getParm(0));

    MergeGraphDoc builder = new MergeGraphDoc();
    for (String name : getParmsAfter(0)) {
      URI nextUri = buildLocation(name);

      mergeURI(builder, nextUri);
    }

    GraphDocument result = builder.getGraphDocument();
    GraphModelXmlPersist persist = GraphModelXmlPersist.build(false);
    persist.save(output, result);
  }

  private void mergeURI(MergeGraphDoc builder, URI mergeUri) {
    try {
      CmdLogger.LOG.info("Loading GraphDoc from {}", mergeUri);
      GraphModelXmlPersist loader = GraphModelXmlPersist.build(true);
      GraphDocument nextDoc = loader.load(mergeUri);
      builder.merge(nextDoc);
    } catch (RuntimeException err) {
      CmdLogger.LOG.error("Unable to load GraphDoc from {}", mergeUri, err);
    }
  }
}
