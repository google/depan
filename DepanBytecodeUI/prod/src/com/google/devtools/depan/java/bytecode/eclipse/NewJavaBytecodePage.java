/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.java.bytecode.eclipse;

import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AbstractAnalysisPage;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (dgi).
 */
public class NewJavaBytecodePage extends AbstractAnalysisPage {

  private static final String PAGE_LABEL = "New Java Analysis";

  private NewJavaBytecodeOptionPart javaBytecodeOptions;

  /**
   * @param selection
   */
  public NewJavaBytecodePage(ISelection selection) {
    super(selection, PAGE_LABEL,
        "This wizard creates a new dependency graph"
        + " from an analysis of Java .class files.",
        createFilename("Java"));
  }

  @Override
  protected void createOptionsParts(Composite container) {
    javaBytecodeOptions = new NewJavaBytecodeOptionPart(this);
    addOptionPart(container, javaBytecodeOptions);
  }

  public String getClassPath() {
    return javaBytecodeOptions.getClassPath();
  }

  public String getDirectoryFilter() {
    return javaBytecodeOptions.getDirectoryFilter();
  }

  public String getPackageFilter() {
    return javaBytecodeOptions.getPackageFilter();
  }

  public AsmFactory getAsmFactory() {
    return javaBytecodeOptions.getAsmFactory();
  }
}
