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
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractNewResourceWizard;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayDocXmlPersist;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayResources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Collections;
import java.util.Map;

/**
 * A wizard to create a new {@link RelationDisplayDocument}.
 * 
 * The name of the supplied (or default) {@link RelationDisplayDocument} can be
 * changed, but the rest of the document is unchanged as it is saved.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewRelationDisplayDocWizard
    extends AbstractNewResourceWizard<RelationDisplayDocument> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.wizards.NewEdgeDisplayDocWizard";

  /**
   * The unique page for this wizard.
   */
  private NewRelationDisplayDocPage page;

  /**
   * {@link RelationSet} to be saved.
   */
  private final RelationDisplayDocument propInfo;

  /**
   * Constructor for a new wizard, for the creation of a new named
   * {@link RelationshipSet} described by <code>finder</code>.
   *
   * @param finder A {@link DirectedRelationFinder} describing the new set.
   */
  public NewRelationDisplayDocWizard(RelationDisplayDocument propInfo) {
    this.propInfo = propInfo;
  }

  public NewRelationDisplayDocWizard(
      Map<Relation, EdgeDisplayProperty> edgeProps) {
    this(new RelationDisplayDocument(
        RelationDisplayResources.BASE_NAME,
        DependencyModel.createFromRegistry(),
        edgeProps));
  }

  /**
   * Constructor for a new wizard, for the creation of a new edge
   * display property bundle from the standard menu item.
   */
  public NewRelationDisplayDocWizard() {
    this(Collections.<Relation, EdgeDisplayProperty>emptyMap());
  }

  /////////////////////////////////////
  // Wizard hook methods

  @Override
  public void addPages() {
    page = new NewRelationDisplayDocPage(propInfo);
    addPage(page);
  }

  /////////////////////////////////////
  // AbstractNewResourceWizard hook methods

  @Override
  protected int countCreateWork() {
    return 1;
  }

  @Override
  protected RelationDisplayDocument createNewDocument(
      IProgressMonitor monitor) {
    monitor.beginTask("Preparing edge display properties", 1);
    monitor.worked(1);

    String docName = page.getDocName();
    if (docName == propInfo.getName()) {
      return propInfo;
    }

    RelationDisplayDocument result =
        new RelationDisplayDocument(
            docName, propInfo.getModel(), propInfo.getInfo());
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
  protected AbstractDocXmlPersist<RelationDisplayDocument>
      getDocXmlPersist() {
    return RelationDisplayDocXmlPersist.build(false);
  }
}
