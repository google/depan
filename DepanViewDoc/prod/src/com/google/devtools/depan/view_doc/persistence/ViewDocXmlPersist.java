package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;
import com.google.devtools.depan.resources.analysis.AnalysisResources;
import com.google.devtools.depan.view_doc.model.ViewDocument;

import org.eclipse.core.resources.IFile;

import java.net.URI;
import java.text.MessageFormat;

/**
 * Provide easy to use load and save methods for {@link ViewDocument}s.
 * Do all the type coercions cleanly.
 * 
 * @author Lee Carver
 */
public class ViewDocXmlPersist extends AbstractDocXmlPersist<ViewDocument> {

  private final static ViewDocXStreamConfig VIEW_DOC_CONFIG =
      new ViewDocXStreamConfig();

  private final String opLabel;

  public ViewDocXmlPersist(ObjectXmlPersist xmlPersist, String opLabel) {
    super(xmlPersist);
    this.opLabel = opLabel;
  }

  public static ViewDocXmlPersist buildForSave(String opLabel) {
    ObjectXmlPersist persist = XStreamFactory.build(false, VIEW_DOC_CONFIG);
    return new ViewDocXmlPersist(persist, opLabel);
  }

  public static ViewDocXmlPersist buildForLoad(
      IFile wkspFile, String opLabel) {
    ViewDocXStreamConfig docConfig = new ViewDocXStreamConfig();
    ObjectXmlPersist persist = XStreamFactory.build(true, docConfig);
    docConfig.setProjectSource(wkspFile.getProject());
    docConfig.setResourceRoot(AnalysisResources.getRoot());
    return new ViewDocXmlPersist(persist, opLabel);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @Override
  protected ViewDocument coerceLoad(Object load) {
      return (ViewDocument) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg("Unable to load View document from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    String msg = MessageFormat.format("Unable to {0} View document to {1}",
        opLabel, uri.toString());
    return msg;
  }
}
