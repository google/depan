package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph_doc.GraphDocResources;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.persistence.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

public class GraphDocXStreamConfig implements XStreamConfig {

  public static final String GRAPH_INFO_TAG = "graph-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(GRAPH_INFO_TAG, GraphDocument.class);

    GraphModelConverter.configXStream(xstream);
    EdgeConverter.configXStream(xstream);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.singletonList(GraphDocResources.BUNDLE);
  }
}
