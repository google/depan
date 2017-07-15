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

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.FileDocumentReference;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.ResourceDocumentReference;

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
public class ProjectResourceLoadControl<T extends PropertyDocument<?>>
    extends ProjectResourceControl<T> {

  private AbstractDocXmlPersist<T> persist;

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
      ResourceContainer container, IContainer folder,
      String fileName, String requiredExt, AbstractDocXmlPersist<T> persist) {
    super(parent, client, container, folder, fileName, requiredExt);
    this.persist = persist;
    setLayout(Widgets.buildContainerLayout(1));
 
    Composite contents = setupContents(this);
    contents.setLayoutData(Widgets.buildGrabFillData());
  }


  @Override
  public PropertyDocumentReference<T> getDocumentReference()
      throws CoreException {
    T resource = getSelectedResource();
    if (null != resource) {
      return ResourceDocumentReference.buildResourceReference(
          getContainer(), resource);
    }

    IFile location = getSelectedDocument();
    if (null != location) {
      if (!location.exists()) {
        String msg = MessageFormat.format(
            "Resource \'{0}\' does not exist.",
            PlatformTools.fromPath(location.getFullPath()));
        PlatformTools.throwCoreException(
            msg, "com.google.devtools.depan.platform.ui");
      }
      T document = persist.load(location.getRawLocationURI());

      return FileDocumentReference.buildFileReference(location, document);
    }

    return null;
  }

  @Override
  public String validateInputs() {
    IFile input = getSelectedDocument();
    String result = validateInputs(
        PlatformTools.fromPath(input.getFullPath()), input.getName());
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
