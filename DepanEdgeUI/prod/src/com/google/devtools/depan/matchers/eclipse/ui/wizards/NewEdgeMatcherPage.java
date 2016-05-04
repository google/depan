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

import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.AbstractResouceWizardPage;
import com.google.devtools.depan.resource_doc.eclipse.ui.wizards.ResourceOptionWizard;

import org.eclipse.swt.widgets.Text;

/**
 * A wizard page to create a new named edge matcher.
 *
 * Based on the legacy {@code NewRelationshipSetWizard}, and should share the
 * same Toolkit persistance model with it.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NewEdgeMatcherPage extends AbstractResouceWizardPage {

  /**
   * The file where we want to add the new relationship set (can be a new file)
   */
  private Text file = null;

  /**
   * Construct the new Wizard page.
   */
  protected NewEdgeMatcherPage() {
    super(null,
        "New Named Edge Matcher",
        "Create a new edge matcher resource",
        "Edge matcher location",
        "matcher.emxml",
        ResourceOptionWizard.NO_OPTIONS);
  }

  /**
   * @return the filename chosen by the user.
   */
  public String getFilename() {
    return file.getText();
  }
}
