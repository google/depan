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

package com.google.devtools.depan.eclipse.natures;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NewDepanProjectWizard extends BasicNewProjectResourceWizard {

  public static final String WIZARD_ID =
      "com.google.devtools.depan.eclipse.natures.NewDepanProjectWizard";

  private final static String[] DEPAN_NATURES =
      { DepAnNature.DEPAN_ID };

  @Override
  public boolean performFinish() {
    if (false == super.performFinish()) {
      return false;
    }

    try {
      IProject project = getNewProject();
      IProjectDescription description = project.getDescription();
      description.setNatureIds(DEPAN_NATURES);
      project.setDescription(description, null);
    } catch (CoreException e) {
      e.printStackTrace();
    }

    return true;
  }
}
