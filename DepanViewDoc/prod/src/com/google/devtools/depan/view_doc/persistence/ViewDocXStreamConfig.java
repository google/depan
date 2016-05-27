package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.persistence.XStreamConfig;

import com.thoughtworks.xstream.XStream;

/**
 * @author Lee Carver
 *
 */
public class ViewDocXStreamConfig implements XStreamConfig {

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    // xstream.alias(EDGE_MATCHER_INFO_TAG, GraphEdgeMatcherDescriptor.class);
  }
}
