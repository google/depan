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

import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentOutputPart;
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentPage;
import com.google.devtools.depan.platform.eclipse.ui.wizards.NewDocumentOutputPart;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;

import java.util.List;

/**
 * Provide the UX elements for subtracting node lists.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NewSubtractNodesPage extends AbstractNewDocumentPage {

  public static final String DEFAULT_FILENAME =
      "subtract." + NodeListDocument.EXTENSION;

  private SubtractOptionPart subtractOptions;

  public NewSubtractNodesPage(ISelection selection) {
    super(selection,
        "New Node List", 
        "Create new nodes lists by subtraction");
  }

  @Override
  protected NewDocumentOutputPart createOutputPart() {
    IContainer outputContainer = guessContainer();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, DEFAULT_FILENAME, 1, 10);

    return new AbstractNewDocumentOutputPart(
        "Node List Subtraction", this, outputContainer,
        NodeListDocument.EXTENSION, outputFilename);
  }

  @Override
  protected void createOptionsParts(Composite container) {
    subtractOptions = new SubtractOptionPart(this);
    addOptionPart(container, subtractOptions);
  }

  public String getMinuend() {
    return subtractOptions.getMinuend();
  }

  public List<IResource> getSubtrahends() {
    return subtractOptions.getSubtrahends();
  }
}
