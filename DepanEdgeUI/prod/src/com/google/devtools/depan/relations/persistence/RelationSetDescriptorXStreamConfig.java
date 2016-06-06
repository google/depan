package com.google.devtools.depan.relations.persistence;

import com.google.devtools.depan.persistence.XStreamConfig;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

public class RelationSetDescriptorXStreamConfig implements XStreamConfig {

  public static final String RELATION_SET_DESCRIPTOR_INFO_TAG = "rel-set-info";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);
    xstream.alias(RELATION_SET_DESCRIPTOR_INFO_TAG, RelationSetDescriptor.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    // Bundle for document module is provided by
    // contribution to XStreamConfigRegistry.
    return Collections.emptyList();
  }
}
