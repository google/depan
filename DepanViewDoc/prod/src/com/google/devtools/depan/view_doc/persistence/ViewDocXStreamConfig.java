package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.persistence.XStreamConfig;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import com.thoughtworks.xstream.XStream;

/**
 * @author Lee Carver
 *
 */
public class ViewDocXStreamConfig implements XStreamConfig {

  private static final String VIEW_PREFS = "view-prefs";

  @Override
  public void config(XStream xstream) {
    xstream.setMode(XStream.NO_REFERENCES);

    xstream.alias(VIEW_PREFS, ViewPreferences.class);

    ViewDocumentConverter converter = ViewDocumentConverter.configXStream(xstream);
    EdgeReferenceConverter.configXStream(xstream, converter);
    NodeReferenceConverter.configXStream(xstream, converter);

    CameraPosConverter.configXStream(xstream);
    CameraDirConverter.configXStream(xstream);
    GraphModelReferenceConverter.configXStream(xstream);
    Point2DConverter.configXStream(xstream);
  }
}
