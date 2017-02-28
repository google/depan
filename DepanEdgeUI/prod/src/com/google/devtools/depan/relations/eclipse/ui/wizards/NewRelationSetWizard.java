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

package com.google.devtools.depan.relations.eclipse.ui.wizards;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor.Builder;
import com.google.devtools.depan.relations.persistence.RelationSetDescriptorXmlPersist;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractNewResourceWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A wizard to add a new {@link RelationSetDescriptor} in an existing file,
 * or to create a new file with this set.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewRelationSetWizard
    extends AbstractNewResourceWizard<RelationSetDescriptor> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.relations.eclipse.ui.wizards.NewRelationSetWizard";

  /**
   * The unique page for this wizard.
   */
  private NewRelationSetPage page;

  /**
   * {@link RelationSet} to be saved.
   */
  private final RelationSetDescriptor relSetDescr;

  /**
   * Constructor for a new wizard, for the creation of a new named
   * {@link RelationshipSet} described by <code>finder</code>.
   *
   * @param finder A {@link DirectedRelationFinder} describing the new set.
   */
  public NewRelationSetWizard(RelationSetDescriptor relationSet) {
    this.relSetDescr = relationSet;
  }

  /**
   * Constructor for a new wizard, for the creation of a new RelSet
   * from the standard menu item.
   */
  public NewRelationSetWizard() {
    this(buildNewRelSetDescriptor());
  }

  private static RelationSetDescriptor buildNewRelSetDescriptor() {
    DependencyModel model = DependencyModel.createFromRegistry();
    Builder result = RelationSetDescriptor.createBuilder(
        RelationSetResources.BASE_NAME, model);
    return result.build();
  }

  /////////////////////////////////////
  // Wizard hook methods

  @Override
  public void addPages() {
    page = new NewRelationSetPage(relSetDescr);
    addPage(page);
  }

  /////////////////////////////////////
  // AbstractNewResourceWizard hook methods

  @Override
  protected int countCreateWork() {
    return 1;
  }

  @Override
  protected RelationSetDescriptor createNewDocument(IProgressMonitor monitor) {
    monitor.beginTask("Preparing edge matcher", 1);
    monitor.worked(1);
    return relSetDescr;
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
  protected AbstractDocXmlPersist<RelationSetDescriptor>
      getDocXmlPersist() {
    return RelationSetDescriptorXmlPersist.build(false);
  }
}
