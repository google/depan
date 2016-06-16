/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractNewResourceWizard;
import com.google.devtools.depan.view_doc.model.EdgeDisplayDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.persistence.EdgeDisplayDocumentXmlPersist;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Collections;
import java.util.Map;

/**
 * A wizard to create a new {@link EdgeDisplayDocument}.
 * 
 * The name of the supplied (or default) {@link EdgeDisplayDocument} can be
 * changed, but the rest of the document is unchanged as it is saved.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewEdgeDisplayDocWizard
    extends AbstractNewResourceWizard<EdgeDisplayDocument> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.wizards.NewEdgeDisplayDocWizard";

  /**
   * The unique page for this wizard.
   */
  private NewEdgeDisplayDocPage page;

  /**
   * {@link RelationSet} to be saved.
   */
  private final EdgeDisplayDocument propInfo;

  /**
   * Constructor for a new wizard, for the creation of a new named
   * {@link RelationshipSet} described by <code>finder</code>.
   *
   * @param finder A {@link DirectedRelationFinder} describing the new set.
   */
  public NewEdgeDisplayDocWizard(EdgeDisplayDocument propInfo) {
    this.propInfo = propInfo;
  }

  public NewEdgeDisplayDocWizard(
      Map<Relation, EdgeDisplayProperty> edgeProps) {
    this(new EdgeDisplayDocument("unnamed", edgeProps));
  }

  public NewEdgeDisplayDocWizard() {
    this(Collections.<Relation, EdgeDisplayProperty>emptyMap());
  }

  /////////////////////////////////////
  // Wizard hook methods

  @Override
  public void addPages() {
    page = new NewEdgeDisplayDocPage(propInfo);
    addPage(page);
  }

  /////////////////////////////////////
  // AbstractNewResourceWizard hook methods

  @Override
  protected int countBuildWork() {
    return 1;
  }

  protected EdgeDisplayDocument buildDocument(IProgressMonitor monitor) {
    monitor.beginTask("Preparing edge display properties", 1);
    monitor.worked(1);

    String docName = page.getDocName();
    if (docName == propInfo.getName()) {
      return propInfo;
    }

    EdgeDisplayDocument result =
        new EdgeDisplayDocument(docName, propInfo.getRelationProperties());
    return result;
  }

  @Override
  protected String getOutputFilename() {
    return page.getOutputFilename();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected AbstractDocXmlPersist<EdgeDisplayDocument>
      getDocXmlPersist() {
    return EdgeDisplayDocumentXmlPersist.build(false);
  }
}
