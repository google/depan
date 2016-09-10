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
import com.google.devtools.depan.persistence.StorageTools;
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.LoadResourceDialog;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.SaveResourceDialog;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;

import java.text.MessageFormat;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class SaveLoadConfig<T extends PropertyDocument<?>> {

  /**
   * Provide the container for the intended kind of resource.
   */
  public abstract ResourceContainer getContainer();

  /**
   * Provide an the intended kind of resource.
   */
  public abstract AbstractDocXmlPersist<T>
      getDocXmlPersist(boolean readable);

  public abstract String getSaveLabel();

  public abstract String getLoadLabel();

  public abstract String getExension();

  /////////////////////////////////////
  // SaveAs support

  public void saveResource(T rsrc, Shell shell, IProject proj) {
    IFile saveFile = getSaveAsFile(rsrc.getName(), shell, proj);
    if (null == saveFile) {
      return;
    }
    AbstractDocXmlPersist<T> persist = getDocXmlPersist(false);
    StorageTools.saveDocument(saveFile, rsrc, persist, null);
  }

  private IFile getSaveAsFile(String rsrcName, Shell shell, IProject proj) {
    IFile saveAs = guessSaveAsFile(proj, rsrcName);
    SaveResourceDialog saveDlg = new SaveResourceDialog(shell);
    saveDlg.setInput(saveAs);
    if (saveDlg.open() != SaveResourceDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    try {
      return WorkspaceTools.calcFileWithExt(
          saveDlg.getResult(), getExension());
    } catch (CoreException errCore) {
      String msg = MessageFormat.format(
          "Error saving resource to {0}", saveAs);
      PlatformLogger.logException(msg, errCore);
    }
    return null;
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
  public T loadResource(Shell shell, IContainer proj) {
    IFile loadFile = getLoadFromFile(shell, proj);
    if (null == loadFile) {
      return null;
    }

    AbstractDocXmlPersist<T> persist = getDocXmlPersist(false);
    return persist.load(loadFile.getRawLocationURI());
  }

  /**
   * Get container and file name from user, with good handling for defaults.
   */
  private IFile getLoadFromFile(Shell shell, IContainer proj) {
    IContainer rsrcRoot = guessResourceRoot(proj);

    LoadResourceDialog loadDlg = new LoadResourceDialog(shell);
    loadDlg.setInput(rsrcRoot, getExension());
    if (loadDlg.open() != SaveResourceDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    try {
      return WorkspaceTools.calcFileWithExt(
          loadDlg.getResult(), getExension());
    } catch (CoreException errCore) {
      String msg = MessageFormat.format(
          "Error loading resource from {0}", rsrcRoot);
      PlatformLogger.logException(msg, errCore);
    }
    return null;
  }

  /**
   * Infer the expected container for filter resources.
   * 
   * The resulting file follow the naming conventions for project resources.
   *   [ViewDoc-Project][Resource-Type-Path][Resource-Name]
   * 
   * The user will be able to edit this result before a storage action is
   * performed.
   * @param proj 
   */
  private IContainer guessResourceRoot(IContainer proj) {
    IPath treePath = getContainer().getPath();
    return proj.getFolder(treePath);
  }
}
