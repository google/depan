/*
 * Copyright 2007 The Depan Project Authors
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
import com.google.devtools.depan.platform.PlatformLogger;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.LoadResourceDialog;
import com.google.devtools.depan.resource_doc.eclipse.ui.persistence.SaveResourceDialog;
import com.google.devtools.depan.resources.PropertyDocument;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import java.text.MessageFormat;

/**
 * Control for saving and loading many types of documents.
 * 
 * Provides a space-filling pair of buttons, one for save and one for load.
 * The {@link SaveLoadConfig} instance provides type-specific UX elements
 * and serialization capabilities.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class GenericSaveLoadControl<T extends PropertyDocument<?>>
    extends Composite {

  private SaveLoadConfig<T> config;

  public GenericSaveLoadControl(Composite parent, SaveLoadConfig<T> config) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(2));

    this.config = config; 

    Button saveButton = Widgets.buildGridPushButton(
        this, config.getSaveLabel());

    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleSave();
      }
    });

    Button loadButton = Widgets.buildGridPushButton(
        this, config.getLoadLabel());

    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        handleLoad();
      }
    });
  }

  /**
   * Open a dialog to save a resource.
   */
  private void handleSave() {

    T rsrc = buildSaveResource();
    IFile saveFile = getSaveAsFile(rsrc.getName());
    if (null == saveFile) {
      return;
    }
    AbstractDocXmlPersist<T> persist = config.getDocXmlPersist(false);
    persist.save(saveFile.getLocationURI(), rsrc);
  }

  /**
   * Open a dialog to load a resource.
   */
  private void handleLoad() {
    IFile loadFile = getLoadFromFile();
    if (null == loadFile) {
      return;
    }

    AbstractDocXmlPersist<T> persist = config.getDocXmlPersist(false);
    T doc = persist.load(loadFile.getRawLocationURI());
    installLoadResource(doc);
  }

  /////////////////////////////////////
  // SaveAs support

  private IFile getSaveAsFile(String rsrcName) {
    IFile saveAs = guessSaveAsFile(rsrcName);
    SaveResourceDialog saveDlg = new SaveResourceDialog(getShell());
    saveDlg.setInput(saveAs);
    if (saveDlg.open() != SaveResourceDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    try {
      return WorkspaceTools.calcFileWithExt(
          saveDlg.getResult(), config.getExension());
    } catch (CoreException errCore) {
      String msg = MessageFormat.format(
          "Error saving resource to {0}", saveAs);
      PlatformLogger.logException(msg, errCore);
    }
    return null;
  }

  protected IFile guessSaveAsFile(String rsrcName) {
    IPath namePath = Path.fromOSString(rsrcName);
    namePath.addFileExtension(config.getExension());

    IPath treePath = config.getContainer().getPath();
    IPath destPath = treePath.append(namePath);

    IProject proj = getProject();
    return proj.getFile(destPath);
  }

  /////////////////////////////////////
  // LoadFrom support

  /**
   * Get container and file name from user, with good handling for defaults.
   */
  private IFile getLoadFromFile() {
    IContainer rsrcRoot = guessResourceRoot();

    LoadResourceDialog loadDlg = new LoadResourceDialog(getShell());
    loadDlg.setInput(rsrcRoot, config.getExension());
    if (loadDlg.open() != SaveResourceDialog.OK) {
      return null;
    }

    // get the file relatively to the workspace.
    try {
      return WorkspaceTools.calcFileWithExt(
          loadDlg.getResult(), config.getExension());
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
   */
  private IContainer guessResourceRoot() {
    IPath treePath = config.getContainer().getPath();
    return getProject().getFolder(treePath);
  }

  /////////////////////////////////////
  // Hook methods for button actions

  protected abstract IProject getProject();

  /**
   * Provide a document to save,
   * based on the current state of the editor's controls.
   */
  protected abstract T buildSaveResource();

  /**
   * Configure the editor's controls, based on the supplied document.
   */
  protected abstract void installLoadResource(T doc);
}
