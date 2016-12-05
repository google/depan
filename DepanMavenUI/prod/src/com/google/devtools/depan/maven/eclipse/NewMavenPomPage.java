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

package com.google.devtools.depan.maven.eclipse;

import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AbstractAnalysisPage;
import com.google.devtools.depan.maven.builder.PomProcessing;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

import java.io.File;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dgi).
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class NewMavenPomPage extends AbstractAnalysisPage {

  public static final String PAGE_LABEL = "New Maven POM Analysis";

  private NewMavenPomOptionPart mavenPomOptions;

  /**
   * @param selection
   */
  public NewMavenPomPage(ISelection selection) {
    super(selection, PAGE_LABEL,
        "This wizard creates a new dependency graph"
        + " from an analysis of a Maven POM file.",
        createFilename("pom"));
  }

  @Override
  protected void createOptionsParts(Composite container) {
    mavenPomOptions = new NewMavenPomOptionPart(this);
    addOptionPart(container, mavenPomOptions);
  }

  public String getPathText() {
    return mavenPomOptions.getPathText();
  }

  public File getPathFile() {
    return mavenPomOptions.getPathFile();
  }

  public String getTreePrefix() {
    return mavenPomOptions.getTreePrefix();
  }

  public PomProcessing getProcessing() {
    return mavenPomOptions.getProcessing();
  }
}
