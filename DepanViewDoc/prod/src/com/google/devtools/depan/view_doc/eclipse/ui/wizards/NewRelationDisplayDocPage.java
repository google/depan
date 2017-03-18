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

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractResouceWizardPage;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.ResourceOptionWizard;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.ResourceOutputPart;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayResources;

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
 * A wizard page to create a new {@link RelationDisplayDocument}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewRelationDisplayDocPage extends AbstractResouceWizardPage {

  private final RelationDisplayDocument propInfo;

  /**
   * The new name for the edge properties.
   */
  private Text docName = null;

  /**
   * Construct the new Wizard page.
   * @param propInfo 
   */
  protected NewRelationDisplayDocPage(RelationDisplayDocument propInfo) {
    super(null,
        "New edge display properties",
        "Save edge display properties as a resource.");
    this.propInfo = propInfo;
  }

  @Override
  protected ResourceOutputPart createOutputPart(
      AbstractResouceWizardPage containingPage) {

    IContainer outputContainer = getResourceContainer(
        RelationDisplayResources.getContainer());
    String filename = RelationDisplayResources.getBaseNameExt();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, filename, 1, 10);

    return new ResourceOutputPart(
        this, "Edge display properties location",
        outputContainer, outputFilename,
        RelationDisplayResources.EXTENSION);
  }

  @Override
  protected ResourceOptionWizard createOptionPart(
      AbstractResouceWizardPage containingPage) {
    return new EdgeDisplayDocOptionWizard();
  }

  public String getDocName() {
    return docName.getText();
  }

  private class EdgeDisplayDocOptionWizard implements ResourceOptionWizard {

    @Override
    public Composite createOptionsControl(Composite container) {
      Group result = new Group(container, SWT.NONE);
      result.setText("Edge Display Document Options");

      GridLayout grid = new GridLayout();
      grid.numColumns = 2;
      grid.verticalSpacing = 9;
      result.setLayout(grid);

      GridData fillHorz = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

      // Row 1) Container selection
      Label label = new Label(result, SWT.NULL);
      label.setText("&Name:");

      docName = new Text(result, SWT.BORDER | SWT.SINGLE);
      docName.setLayoutData(fillHorz);
      docName.setText(propInfo.getName());
      docName.addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
          updatePageStatus();
        }
      });

      return result;
    }

    @Override
    public String getErrorMsg() {
      String name = docName.getText();
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
