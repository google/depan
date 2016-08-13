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
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.IWorkbenchAdapter;
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
public class ProjectResourceControl extends Composite {

  public static interface UpdateListener {
    void onUpdate();
  }

  private final Shell shell;

  // [Aug-2016] Assume only one interested party.
  private final UpdateListener client;

  // External context for this part
  private String fileName;

  private IContainer rsrcContainer;

  private String requiredExt;

  /////////////////////////////////////
  // UX elements

  private Text containerText;

  private DrillDownComposite containerControl;

  private TreeViewer containerViewer;

  private Text fileText;

  /////////////////////////////////////
  // Public methods

  /**
   * Provide the composite GUI element.
   * 
   * @param parent window context for the UI
   */
  public ProjectResourceControl(
      Composite parent, Shell shell, UpdateListener client,
      IContainer rsrcContainer, String fileName, String requiredExt) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));
 
    this.shell = shell;
    this.client = client;
    this.rsrcContainer = rsrcContainer;
    this.fileName = fileName;
    this.requiredExt = requiredExt;

    Composite contents = setupContents(this);
    contents.setLayoutData(Widgets.buildGrabFillData());

    validateInputs();
  }

  public String getContainerName() {
    return containerText.getText();
  }

  public String getFileName() {
    return fileText.getText();
  }

  public IFile getResourceLocation() throws CoreException {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(getContainerName()));
    if (!(resource instanceof IContainer) || !resource.exists()) {
      String msg = MessageFormat.format(
          "Container \'{0}\' does not exist.", getContainerName());
      PlatformTools.throwCoreException(
          msg, "com.google.devtools.depan.platform.ui");
    }

    IContainer container = (IContainer) resource;
    final IFile file = container.getFile(new Path(getFileName()));
    return file;
  }

  /**
   * Determine if the inputs are consistent.
   * 
   * @return error string if problems exist, or null if inputs are valid
   */
  public String validateInputs() {
    IPath containerPath = Path.fromOSString(getContainerName());
    IWorkspaceRoot wkspRoot = ResourcesPlugin.getWorkspace().getRoot();
    IResource container = wkspRoot.findMember(containerPath);
    String fileName = getFileName();

    if (getContainerName().length() == 0) {
      return "File container must be specified";
    }
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
    IPath filePath = Path.fromOSString(fileName);
    if ((1 != filePath.segmentCount()) || (filePath.hasTrailingSeparator())) {
      return "File name cannot be a path";
    }
    String extCheck = validateExtension(filePath.getFileExtension());
    if (null != extCheck) {
      return extCheck;
    }

    return null;
  }

  private String validateExtension(String fileExt) {
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

  private Composite setupContents(Composite parent) {
    Group result = Widgets.buildGridGroup(parent, "Resource Location", 1);

    // Container by name
    containerText = Widgets.buildGridBoxedText(result);
    containerText.setText(rsrcContainer.getFullPath().toString());

    // Container by tree
    containerControl = setupContainerControl(result);
    containerControl.setLayoutData(Widgets.buildGrabFillData());

    // File name selection
    Composite fileGrp = setupFilenameControl(result);
    fileGrp.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  private DrillDownComposite setupContainerControl(Composite parent) {
    DrillDownComposite result = new DrillDownComposite(parent, SWT.BORDER);

    containerViewer = new TreeViewer(result, SWT.NONE);
    result.setChildTree(containerViewer);

    IContentProvider provider =
        new ControlContentProvider(rsrcContainer.getProject());
    containerViewer.setContentProvider(provider);
    containerViewer.setLabelProvider(new WorkbenchLabelProvider());
    containerViewer.setComparator(new ViewerComparator());
    containerViewer.setUseHashlookup(true);
    containerViewer.setInput(rsrcContainer);

    containerViewer.addSelectionChangedListener(new ISelectionChangedListener () {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) event .getSelection();
        handleSelectionChange(selection.getFirstElement());
      }});
    return result;
  }

  private void handleSelectionChange(Object selection) {
    if (selection instanceof IFile) {
      IFile file = (IFile) selection;
      fileName = file.getName();
      rsrcContainer = file.getParent();

      containerText.setText(rsrcContainer.getFullPath().toString());
      fileText.setText(fileName);
    }
  }

  private Composite setupFilenameControl(Composite parent) {

    Composite result = Widgets.buildGridContainer(parent, 2);
    result.setLayoutData(Widgets.buildHorzFillData());
    Widgets.buildCompactLabel(result, "&File name:");

    fileText = Widgets.buildGridBoxedText(result);
    fileText.setText(fileName);

    fileText.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(ModifyEvent e) {
        ProjectResourceControl.this.client.onUpdate();
      }
    });
    return result;
  }

  private static class ControlContentProvider extends WorkbenchContentProvider {

    private final IProject proj;

    public ControlContentProvider(IProject proj) {
      this.proj = proj;
    }

    @Override
    protected IWorkbenchAdapter getAdapter(Object element) {
      return super.getAdapter(element);
    }
  }
}
