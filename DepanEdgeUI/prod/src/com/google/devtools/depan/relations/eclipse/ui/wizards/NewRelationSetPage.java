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

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
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
 * A wizard page to create a new named relationship set.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NewRelationSetPage extends AbstractResouceWizardPage {

  /**
   * The new name for the set.
   */
  private Text setName = null;

  /** Source of initial state. */
  private RelationSetDescriptor relationSet;

  /**
   * Construct the new Wizard page.
   */
  protected NewRelationSetPage(RelationSetDescriptor relationSet) {
    super(null,
        "New named set of Relations",
        "Save a RelationSet as a resource.");
    this.relationSet = relationSet;
  }

  @Override
  protected ResourceOutputPart createOutputPart(
      AbstractResouceWizardPage containingPage) {

    IContainer outputContainer = getResourceContainer(
        RelationSetResources.getContainer());
    String filename = RelationSetResources.getBaseNameExt();
    String outputFilename = PlatformTools.guessNewFilename(
        outputContainer, filename, 1, 10);

    return new ResourceOutputPart(
        this, "Relation Set location",
        outputContainer, outputFilename,
        RelationSetResources.EXTENSION);
  }

  @Override
  protected ResourceOptionWizard createOptionPart(
      AbstractResouceWizardPage containingPage) {
    return new RelationSetOptionWizard();
  }

  private class RelationSetOptionWizard implements ResourceOptionWizard {

    @Override
    public Composite createOptionsControl(Composite container) {
      Group result = new Group(container, SWT.NONE);
      result.setText("Relation Set Options");

      GridLayout grid = new GridLayout();
      grid.numColumns = 2;
      grid.verticalSpacing = 9;
      result.setLayout(grid);

      GridData fillHorz = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

      // Row 1) Container selection
      Label label = new Label(result, SWT.NULL);
      label.setText("&Name:");

      setName = new Text(result, SWT.BORDER | SWT.SINGLE);
      setName.setLayoutData(fillHorz);
      setName.setText(guessSetName());
      setName.addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(ModifyEvent e) {
          updatePageStatus();
        }
      });

      return result;
    }

    private String guessSetName() {
      if (null != relationSet) {
        return relationSet.getName();
      }
      return getResourceName();
    }

    @Override
    public String getErrorMsg() {
      String name = setName.getText();
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
