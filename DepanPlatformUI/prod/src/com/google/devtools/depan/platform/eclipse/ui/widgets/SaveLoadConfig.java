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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.AbstractResourceDialog;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.LoadResourceDialog;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.SaveResourceDialog;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class SaveLoadConfig<T extends PropertyDocument<?>> {

  /**
   * Provide the container for the intended kind of resource.
   */
  public abstract ResourceContainer getContainer();

  /**
   * Provide an instance of the intended kind of resource.
   */
  public abstract AbstractDocXmlPersist<T>
      getDocXmlPersist(boolean readable);

  /////////////////////////////////////
  // Standard resource naming API.
  // Derived types delegate to Config instances.

  public abstract String getSaveLabel();

  public abstract String getLoadLabel();

  public abstract String getBaseName();

  public abstract String getExension();

  /** Overridable hook method */
  public SaveResourceDialog<T> buildSaveDialog(Shell shell) {
    return new SaveResourceDialog<T>(shell);
  }

  /** Overridable hook method */
  private LoadResourceDialog<T> buildLoadDialog(Shell shell) {
    return new LoadResourceDialog<T>(shell, getDocXmlPersist(false));
  }

  public IPath getBaseNameExt() {
    return PlatformTools.getBaseNameExtPath(getBaseName(), getExension());
  }

  public IFile getNewResourceFile(IContainer proj) {
    IPath treePath = getContainer().getPath();
    IFolder root = proj.getFolder(treePath);
    return root.getFile(getBaseNameExt());
  }

  /////////////////////////////////////
  // SaveAs support

  public void saveResource(T rsrc, Shell shell, IProject proj) {
    PropertyDocumentReference<T> saveRef =
        getSaveAsFile(rsrc.getName(), shell, proj);
    if (null == saveRef) {
      return;
    }
    AbstractDocXmlPersist<T> persist = getDocXmlPersist(false);
    saveRef.saveResource(rsrc, persist);
  }

  private PropertyDocumentReference<T> getSaveAsFile(
      String rsrcName, Shell shell, IProject proj) {
    IFile saveAs = guessSaveAsFile(proj, rsrcName);
    AbstractResourceDialog<T> saveDlg = buildSaveDialog(shell);
    saveDlg.setInput(getContainer(), saveAs);
    if (saveDlg.open() != SaveResourceDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    PropertyDocumentReference<T> result = saveDlg.getResult();
    return result;
  }

  private IFile guessSaveAsFile(IProject proj, String rsrcName) {
    IPath namePath = Path.fromOSString(rsrcName);
    namePath.addFileExtension(getExension());

    IPath treePath = getContainer().getPath();
    IPath destPath = treePath.append(namePath);
    return proj.getFile(destPath);
  }

  /////////////////////////////////////
  // LoadFrom support

  /**
   * Open a dialog to load a resource.
   */
  public PropertyDocumentReference<T> loadResource(
      Shell shell, IContainer proj) {
    AbstractResourceDialog<T> loadDlg = buildLoadDialog(shell);
    loadDlg.setInput(getContainer(), getNewResourceFile(proj));

    if (SaveResourceDialog.OK == loadDlg.open()) {
      return loadDlg.getResult();
    }

    return null;
  }
}
