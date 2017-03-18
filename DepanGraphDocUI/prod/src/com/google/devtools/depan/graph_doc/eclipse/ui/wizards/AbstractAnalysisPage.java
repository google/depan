/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentOutputPart;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewDocumentOutputPart;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ISelection;

/**
 * A standard page for accepting user input in an analysis wizard.
 * The top portion allows the user to specify the file name for
 * the analysis output.
 * 
 * Extending classes should implement the
 * {@link AbstractResouceWizardPage.createSourceControl(Composite)} method to
 * define user's content to analyze.
 */
public abstract class AbstractAnalysisPage extends AbstractNewDocumentPage {

  private final String defaultFilename;

  public AbstractAnalysisPage(
      ISelection selection, String pageLabel, String pageDescription,
      String defaultFilename) {
    super(selection, pageLabel, pageDescription);
    this.defaultFilename =  defaultFilename;
  }

  /**
   * Add the graph document's file extension to the filename.
   */
  protected static String createFilename(String filename) {
    return filename + '.' + GraphDocument.EXTENSION;
  }

  @Override
  protected NewDocumentOutputPart createOutputPart() {
    IContainer outputContainer = guessContainer();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, defaultFilename, 1, 10);

    return new AbstractNewDocumentOutputPart(
        "Analysis Output File", this, outputContainer,
        GraphDocument.EXTENSION, outputFilename);
  }
}
