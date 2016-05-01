package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.persistence.XStreamConfig;

import com.thoughtworks.xstream.XStream;

public class GraphDocXStreamConfig implements XStreamConfig {

  public static final String GRAPH_INFO_TAG = "graph-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(GRAPH_INFO_TAG, GraphDocument.class);

    GraphModelConverter.configXStream(xstream);
    EdgeConverter.configXStream(xstream);
    DependencyModelConverter.configXStream(xstream);
  }
}
