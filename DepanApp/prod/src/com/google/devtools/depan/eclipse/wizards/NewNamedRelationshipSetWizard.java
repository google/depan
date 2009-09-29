/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.wizards;

import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

/**
 * The RelationshipSet wizard. Helps the user to create a new file.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NewNamedRelationshipSetWizard extends Wizard
    implements INewWizard {

  private NewNamedRelationshipSetPage page;

  private ISelection selection;

  public NewNamedRelationshipSetWizard() {
    setNeedsProgressMonitor(true);
  }

  @Override
  public boolean performFinish() {
    final String containerName = page.getContainerName();
    final String filename = page.getFileName();
    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        try {
          doFinish(containerName, filename, monitor);
        } catch (CoreException errCore) {
          throw new InvocationTargetException(errCore);
        } catch (IOException errIo) {
          throw new InvocationTargetException(errIo,
              "Unable to save Named Relationship to " + filename);
        } finally {
          monitor.done();
        }
      }
    };
    try {
      getContainer().run(false, false, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      realException.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Actually creates the file.
   *
   * @param containerName project name
   * @param filename filename
   * @param monitor a {@link IProgressMonitor} to track advancement.
   * @throws CoreException if the container doesn't exists.
   */
  private void doFinish(
      String containerName, String filename, IProgressMonitor monitor)
      throws CoreException, IOException {
    monitor.beginTask("Creating " + filename, 1);

    monitor.setTaskName("Creating file...");

    // get the project path
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(containerName));
    if (!resource.exists() || !(resource instanceof IContainer)) {
      throwCoreException("Container \"" + containerName + "\" does not exist.");
    }
    IContainer container = (IContainer) resource;
    final IFile file = container.getFile(new Path(filename));

    // save the builtins relationships as an "example" in the file.
    // TODO(leeca):  Is this configured with the correct XStream flavor?
    ObjectXmlPersist persist =
        new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
    persist.save(file.getLocationURI(), Collections.EMPTY_LIST);

    monitor.worked(1);
  }

  private void throwCoreException(String message) throws CoreException {
    IStatus status = new Status(IStatus.ERROR, "com.google.devtools.depan",
        IStatus.OK, message, null);
    throw new CoreException(status);
  }

  @Override
  public void addPages() {
    page = new NewNamedRelationshipSetPage(selection);
    addPage(page);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection select) {
    this.selection = select;
  }
}
