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
   * Configure a basic Resource Wizard Page.
   */
  protected NewEdgeMatcherPage() {
    super(null,
        "New Named Edge Matcher",
        "Create a new edge matcher resource",
        "Edge matcher location",
        "matcher.emxml",
        ResourceOptionWizard.NO_OPTIONS);
  }
}
