/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.graph_doc.operations.SubtractNodes;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.nodelist_doc.persistence.NodeListDocXmlPersist;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.net.URI;

/**
 * Wizard to create new node lists by subtraction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NewSubtractNodesWizard
    extends AbstractNewDocumentWizard<NodeListDocument> {

  /**
   * Eclipse extension identifier for this wizard.
   */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.nodelist_doc.eclipse.ui.wizards.NewSubtractNodesWizard";

  private NewSubtractNodesPage page;

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewSubtractNodesPage(getSelection());
    addPage(page);
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
  protected int countCreateWork() {
    return 1 + page.getSubtrahends().size();
  }

  @Override
  protected NodeListDocument createNewDocument(IProgressMonitor monitor)
      throws CoreException, IOException {

    String baseName = page.getMinuend();
    IFile baseFile = WorkspaceTools.buildResourceFile(baseName);
    GraphDocument baseDoc = buildGraphDoc(baseFile.getLocationURI());

    SubtractNodes subtract = new SubtractNodes(baseDoc.getGraph());
    for (IResource name : page.getSubtrahends()) {
      GraphDocument subtractDoc = buildGraphDoc(name.getLocationURI());
      if (null == subtractDoc) {
        continue;
      }
      subtract.subtract(subtractDoc.getGraph());
    }

    GraphModelReference parentGraph =
        new GraphModelReference(baseName, baseDoc);
    return new NodeListDocument(parentGraph, subtract.getNodes());
  }

  /**
   * Provide the {@link GraphDocument} associated
   * with the supplied {@link URI}.
   * 
   * If the URI fails to load as a {@link GraphDocument}, writes a message
   * to the log and returns {@code null}.
   */
  private GraphDocument buildGraphDoc(URI graphUri) {
    try {
      PersistenceLogger.LOG.info("Loading GraphDoc from {}", graphUri);
      GraphModelXmlPersist loader = GraphModelXmlPersist.build(true);
      return loader.load(graphUri);
    } catch (RuntimeException err) {
      PersistenceLogger.LOG.error(
          "Unable to load GraphDoc from {}", graphUri, err);
    }
    return null;
  }

  @Override
  protected int countSaveWork() {
    return 3;
  }

  @Override
  protected void saveNewDocument(
      IProgressMonitor monitor, NodeListDocument doc)
      throws CoreException {

    monitor.setTaskName("Creating document...");
    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Saving document...");
    AbstractDocXmlPersist<NodeListDocument> persist =
        NodeListDocXmlPersist.buildForSave();
    persist.saveDocument(file, doc, monitor);
    monitor.worked(1);

    monitor.setTaskName("Refreshing resources ...");
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    monitor.worked(1);
  }
}
