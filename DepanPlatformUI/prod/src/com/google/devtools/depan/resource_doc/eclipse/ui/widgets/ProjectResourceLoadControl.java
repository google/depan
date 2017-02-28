/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.resource_doc.eclipse.ui.widgets;

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.DrillDownComposite;

import java.text.MessageFormat;

/**
 * Provide a standard component for loading a persistent resource,
 * whether built-in or user defined.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ProjectResourceLoadControl extends ProjectResourceControl {

  /////////////////////////////////////
  // UX elements

  private DrillDownComposite containerControl;

  /////////////////////////////////////
  // Public methods

  /**
   * Provide the composite GUI element.
   */
  public ProjectResourceLoadControl(
      Composite parent, UpdateListener client,
      ResourceContainer container, IContainer rsrcContainer,
      String fileName, String requiredExt) {
    super(parent, client, container, rsrcContainer, fileName, requiredExt);
    setLayout(Widgets.buildContainerLayout(1));
 
    Composite contents = setupContents(this);
    contents.setLayoutData(Widgets.buildGrabFillData());
  }

  public IFile getResourceLocation() throws CoreException {
    IFile result = getSelectedDocument();
    if (result == null) {
      PlatformTools.throwCoreException(
          "Document resource must be selected",
          "com.google.devtools.depan.platform.ui");
      
    }
    if (!result.exists()) {
      String msg = MessageFormat.format(
          "Resource \'{0}\' does not exist.",
          WorkspaceTools.fromPath(result.getFullPath()));
      PlatformTools.throwCoreException(
          msg, "com.google.devtools.depan.platform.ui");
    }

    return result;
  }

  @Override
  public String validateInputs() {
    IFile input = getSelectedDocument();
    String result = validateInputs(
        WorkspaceTools.fromPath(input.getFullPath()), input.getName());
    if (null != result) {
      return result;
    }

    result = validateExtension(input.getFileExtension());
    if (null != result) {
      return result;
    }
    return null;
  }

  /////////////////////////////////////
  // UX Setup

  @Override
  protected Composite setupContents(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Resource Location", 1);

    // Container by tree
    containerControl = setupContainerControl(result);
    containerControl.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }
}
