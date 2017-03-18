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

package com.google.devtools.depan.resource_doc.eclipse.ui.widgets;

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.DrillDownComposite;

import java.text.MessageFormat;

/**
 * Provide a standard component for specifying the output
 * of an analysis page.
 * 
 * Based on {@code AnalysisOutputPart}.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class ProjectResourceControl<T extends PropertyDocument<?>>
    extends Composite {

  public static interface UpdateListener {
    void onUpdate();
  }

  // [Aug-2016] Assume only one interested party.
  private final UpdateListener client;

  private final ResourceContainer container;

  private IContainer folder;

  private String fileName;

  private String requiredExt;

  /////////////////////////////////////
  // UX elements

  private TreeViewer containerViewer;

  /////////////////////////////////////
  // Public methods

  /**
   * Provide the composite GUI element.
   * 
   * @param parent window context for the UI
   */
  public ProjectResourceControl(
      Composite parent, UpdateListener client,
      ResourceContainer container, IContainer folder,
      String fileName, String requiredExt) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    this.client = client;
    this.container = container;
    this.folder = folder;
    this.fileName = fileName;
    this.requiredExt = requiredExt;
  }

  protected String getFileName() {
    return fileName;
  }

  protected ResourceContainer getContainer() {
    return container;
  }

  public IContainer getSelectedContainer() {
    ITreeSelection blix = containerViewer.getStructuredSelection();
    Object item = blix.getFirstElement();
    if (item instanceof IContainer) {
      return (IContainer) item;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public T getSelectedResource() {
    ITreeSelection blix = containerViewer.getStructuredSelection();
    Object item = blix.getFirstElement();
    if (item instanceof PropertyDocument<?>) {
      return (T) item;
    }
    return null;
  }

  public IFile getSelectedDocument() {
    ITreeSelection blix = containerViewer.getStructuredSelection();
    Object item = blix.getFirstElement();
    if (item instanceof IFile) {
      return (IFile) item;
    }
    return null;
  }

  public abstract PropertyDocumentReference<T> getDocumentReference()
      throws CoreException;

  public abstract String validateInputs();

  /////////////////////////////////////
  // Support methods for derived types

  /**
   * Determine if the inputs are consistent.
   * 
   * @return error string if problems exist, or null if inputs are valid
   */
  public static String validateInputs(String containerName, String fileName) {

    if (containerName.length() == 0) {
      return "File container must be specified";
    }

    IPath containerPath = PlatformTools.buildPath(containerName);
    IResource container = WorkspaceTools.buildWorkspaceResource(containerPath);
    if (container == null
        || (container.getType()
            & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      return "File container must exist";
    }
    if (!container.isAccessible()) {
      return "Project must be writable";
    }
    if (fileName.length() == 0) {
      return "File name must be specified";
    }
    IPath filePath = PlatformTools.buildPath(fileName);
    if ((1 != filePath.segmentCount()) || (filePath.hasTrailingSeparator())) {
      return "File name cannot be a path";
    }
    filePath.getFileExtension();

    return null;
  }

  public String validateExtension(String fileExt) {
    if (null == requiredExt) {
      return null;
    }
    // No extension is ok.
    if (null == fileExt) {
      return null;
    }
    // Unexpected extension is bad.
    if (fileExt.equalsIgnoreCase(requiredExt)) {
      return null;
    }

    // Report unexpected extensions.
    if (fileExt.isEmpty()) {
      return MessageFormat.format(
          "File extension should not be be empty."
          + "Extension \'{0}\' is expected", fileExt, requiredExt);
    }
    return MessageFormat.format(
        "File extension \'{0}\' must be \\'{1}\\'", fileExt, requiredExt);
  }

  /////////////////////////////////////
  // UX Setup

  protected abstract Composite setupContents(Composite parent);

  protected Text buildContainerText(Composite parent) {
    Text result = Widgets.buildGridBoxedText(parent);
    result.setText(folder.getFullPath().toString());
    return result;
  }

  protected DrillDownComposite setupContainerControl(Composite parent) {
    DrillDownComposite result = new DrillDownComposite(parent, SWT.BORDER);

    containerViewer = new TreeViewer(result, SWT.NONE);
    result.setChildTree(containerViewer);

    containerViewer.setContentProvider(new WorkbenchContentProvider());
    containerViewer.setLabelProvider(new WorkbenchLabelProvider());
    containerViewer.setComparator(new ViewerComparator());
    containerViewer.setUseHashlookup(true);
    containerViewer.setInput(prepareInput());
    return result;
  }

  protected void addSelectionChangedListener(
      ISelectionChangedListener listener) {
    containerViewer.addSelectionChangedListener(listener);
  }

  protected void fireClientOnUpdate() {
    client.onUpdate();
  }

  private ResourceRoot prepareInput() {
    return new ResourceRoot(
        ResourceRoot.DEFAULT_LABEL, container, folder);
  }
}
