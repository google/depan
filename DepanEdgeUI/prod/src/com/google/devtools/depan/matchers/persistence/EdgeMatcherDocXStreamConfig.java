package com.google.devtools.depan.matchers.persistence;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.persistence.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

public class EdgeMatcherDocXStreamConfig implements XStreamConfig {

  public static final String EDGE_MATCHER_INFO_TAG = "edge-matcher-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(EDGE_MATCHER_INFO_TAG, GraphEdgeMatcherDescriptor.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    // Bundle for document module is provided by
    // contribution to XStreamConfigRegistry.
    return Collections.emptyList();
  }
}
