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

import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractResouceWizardPage;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.ResourceOptionWizard;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.ResourceOutputPart;

import com.google.common.base.Strings;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A wizard page to create a new named edge matcher.
 *
 * Based on the legacy {@code NewRelationshipSetWizard}, and should share the
 * same Toolkit persistance model with it.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewEdgeMatcherPage extends AbstractResouceWizardPage {

  /**
   * Name for new Edge Matcher.  Populated by the internal type
   * {@link EdgeMatcherOptionWizard}.
   */
  private Text nameText;

  /**
   * Configure a basic Resource Wizard Page.
   */
  protected NewEdgeMatcherPage() {
    super(null,
        "New Named Edge Matcher",
        "Create a new edge matcher resource");
  }

  public String getEdgeMatcherName() {
    return nameText.getText();
  }

  @Override
  protected ResourceOutputPart createOutputPart(
      AbstractResouceWizardPage containingPage) {
    IContainer outputContainer = getResourceContainer(
        GraphEdgeMatcherResources.getContainer());
    String filename = GraphEdgeMatcherResources.getBaseNameExt();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, filename, 1, 10);

    return new ResourceOutputPart(
        this, "Edge matcher location",
        outputContainer, outputFilename,
        GraphEdgeMatcherResources.EXTENSION);
  }

  @Override
  protected ResourceOptionWizard createOptionPart(
      AbstractResouceWizardPage containingPage) {
    return new EdgeMatcherOptionWizard();
  }

  private class EdgeMatcherOptionWizard implements ResourceOptionWizard {

    @Override
    public Composite createOptionsControl(Composite container) {
      Group result = new Group(container, SWT.NONE);
      result.setText("Edge Matcher Options");

      GridLayout grid = new GridLayout();
      grid.numColumns = 2;
      grid.verticalSpacing = 9;
      result.setLayout(grid);

      GridData fillHorz = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

      // Row 1) Container selection
      Label label = new Label(result, SWT.NULL);
      label.setText("&Name:");

      nameText = new Text(result, SWT.BORDER | SWT.SINGLE);
      nameText.setLayoutData(fillHorz);
      nameText.addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
          updatePageStatus();
        }
      });

      return result;
    }

    @Override
    public String getErrorMsg() {
      String name = getEdgeMatcherName();
      if (Strings.isNullOrEmpty(name)) {
        return "Name must not be empty";
      }

      // Everything is fine.
      return null;
    }

    @Override
    public boolean isComplete() {
      return null == getErrorMessage();
    }
  }
}
