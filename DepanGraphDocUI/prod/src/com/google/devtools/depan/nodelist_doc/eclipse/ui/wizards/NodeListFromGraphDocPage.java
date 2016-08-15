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

import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListFromGraphDocPage extends WizardPage {

  public static final String PAGE_NAME = "Create NodeList document";

  public static final String PAGE_DESCRIPTION =
      "Setup node list document";

  @SuppressWarnings("unused")
  private GraphResources graphInfo;

  protected NodeListFromGraphDocPage(GraphResources graphInfo) {
    super(PAGE_NAME);
    this.graphInfo = graphInfo;

    setTitle(PAGE_NAME);
    setDescription(PAGE_DESCRIPTION);
 }

  @Override
  public void createControl(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);

    GridLayout layout = Widgets.buildContainerLayout(1);
    layout.verticalSpacing = 9;
    result.setLayout(layout);

    Widgets.buildGridLabel(
        result, "No options at this time for NodeList creation.");
    Widgets.buildGridLabel(
        result, "Please press Finish to complete NodeList creation.");

    setControl(result);
  }

  protected void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete((null == message) && isPageComplete());
  }
}
