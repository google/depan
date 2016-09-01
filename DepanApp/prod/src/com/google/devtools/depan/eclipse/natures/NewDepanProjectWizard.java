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

import com.google.devtools.depan.eclipse.ApplicationLogger;
import com.google.devtools.depan.matchers.models.MatcherResources;
import com.google.devtools.depan.nodes.filters.eclipse.ui.persistence.ContextualFilterResources;
import com.google.devtools.depan.relations.models.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import java.text.MessageFormat;

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
      buildAnalysisResources();
    } catch (CoreException errCore) {
      ApplicationLogger.logException("Failed to create new project", errCore);
    }

    return true;
  }

  private void buildAnalysisResources() {
    // TODO: from registered resource containers.
    buildContainerFolder(MatcherResources.getContainer());
    buildContainerFolder(ContextualFilterResources.getContainer());
    buildContainerFolder(RelationSetResources.getContainer());
  }

  private void buildContainerFolder(ResourceContainer rsrcCntr) {
    IPath rsrcPath = rsrcCntr.getPath();
    IFolder rsrcFolder = getNewProject().getFolder(rsrcPath);
    buildContainerFolder(rsrcFolder);
  }

  private void buildContainerFolder(IFolder rsrcFolder) {
    if (rsrcFolder.exists()) {
      return;
    }
    // Recursively create parents as necessary
    IContainer parent = rsrcFolder.getParent();
    if (parent instanceof IFolder) {
      buildContainerFolder((IFolder) parent);
    }
    try {
      rsrcFolder.create(false, true, null);
    } catch (CoreException errCore) {
      String msg = MessageFormat.format(
          "Unable to create resource folder {0}", rsrcFolder.getFullPath());
      ApplicationLogger.logException(msg, errCore);
    }
  }
}
