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

package com.google.devtools.depan.matchers.eclipse.ui.wizards;

import com.google.devtools.depan.edges.matchers.GraphEdgeMatchers;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.EdgeMatcherDocXmlPersist;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractNewResourceWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A wizard to add a new {@link GraphEdgeMatcherDescriptor} in an existing
 * file, or to create a new file with this set.
 *
 * Based on the legacy {@code NewRelationshipSetWizard}, and should share the
 * same Toolkit persistance model with it.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewEdgeMatcherWizard
    extends AbstractNewResourceWizard<GraphEdgeMatcherDescriptor> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.matchers.eclipse.ui.wizards.NewEdgeMatcher";

  /**
   * The unique page for this wizard.
   */
  private NewEdgeMatcherPage page;

  /**
   * {@link GraphEdgeMatcherDescriptor} to be saved.
   */
  private final GraphEdgeMatcherDescriptor matcherInfo;

  /**
   * Constructor for a new wizard, for the creation of a new named
   * {@link GraphEdgeMatcherDescriptor} described by {@code edgeMatcher}.
   *
   * @param finder A {@link DirectedRelationFinder} describing the new set.
   */
  public NewEdgeMatcherWizard() {
    this(new GraphEdgeMatcherDescriptor(
        "Empty",
        DependencyModel.createFromRegistry(),
        GraphEdgeMatchers.EMPTY));
  }

  public NewEdgeMatcherWizard(GraphEdgeMatcherDescriptor matcherInfo) {
    this.matcherInfo = matcherInfo;
  }

  /////////////////////////////////////
  // Wizard hook methods

  @Override
  public void addPages() {
    page = new NewEdgeMatcherPage();
    addPage(page);
  }

  /////////////////////////////////////
  // AbstractNewResourceWizard hook methods

  @Override
  protected int countCreateWork() {
    return 1;
  }

  @Override
  protected GraphEdgeMatcherDescriptor createNewDocument(
      IProgressMonitor monitor) {
    monitor.beginTask("Preparing edge matcher", 1);
    monitor.worked(1);
    return matcherInfo;
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
  protected AbstractDocXmlPersist<GraphEdgeMatcherDescriptor>
      getDocXmlPersist() {
    return EdgeMatcherDocXmlPersist.build(false);
  }
}
