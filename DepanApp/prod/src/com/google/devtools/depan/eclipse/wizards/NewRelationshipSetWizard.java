/*
 * Copyright 2007 The Depan Project Authors
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
import com.google.devtools.depan.eclipse.utils.DefaultRelationshipSet;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A wizard to add a new {@link RelationshipSet} in an existing file, or to
 * create a new file with this set.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NewRelationshipSetWizard extends Wizard implements INewWizard {

  /**
   * The unique page for this wizard.
   */
  private NewRelationshipSetPage page;

  /**
   * {@link DirectedRelationFinder} describing the {@link RelationshipSet}.
   */
  @SuppressWarnings("unused")  // This needs rework.
  private final DirectedRelationFinder finder;

  /**
   * Constructor for a new wizard, for the creation of a new named
   * {@link RelationshipSet} described by <code>finder</code>.
   *
   * @param finder A {@link DirectedRelationFinder} describing the new set.
   */
  public NewRelationshipSetWizard(DirectedRelationFinder finder) {
    this.finder = finder;
  }

  @Override
  public boolean performFinish() {
    final String filename = page.getFilename();
    final String setname = page.getSetname();
    IRunnableWithProgress op = new IRunnableWithProgress() {

      @Override
      public void run(IProgressMonitor monitor) {
        try {
          doFinish(filename, setname, monitor);
        } catch (CoreException errCore) {
          throw new RuntimeException(errCore);
        } catch (IOException errIo) {
          throw new RuntimeException(errIo);
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
   * Add the relationship to the file (creates the file if necessary).
   *
   * @param fileName project name
   * @param setName filename
   * @param monitor a {@link IProgressMonitor} to track advancement.
   */
  private void doFinish(String fileName, String setName,
      IProgressMonitor monitor) throws IOException, CoreException {
    monitor.beginTask("Creating " + setName, 1);

    monitor.setTaskName("Creating file...");

    // get the project path
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    //IResource resource = root.findMember(fileName);
    IFile file = root.getFile(new Path(fileName));

    if (file.exists()) {
      addToSet(file, setName);
    } else {
      newFileAndSet(file, setName);
    }

    monitor.worked(1);
  }

  /**
   * Add the set to an existing file.
   *
   * @param file the existing file
   * @param setName the set name
   */
  private void addToSet(IFile file, String setName)
      throws IOException, CoreException {
    // TODO(leeca):  Is this configured with the correct XStream flavor?
    ObjectXmlPersist persist =
        new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
    Collection<RelationshipSet> updateSet = loadRelationshipSet(persist, file);
    RelationshipSet set = DefaultRelationshipSet.SET;
    updateSet.add(set);
    persist.save(file.getLocationURI(), updateSet);
    file.refreshLocal(IResource.DEPTH_ZERO, null);
  }

  /**
   * Isolate unchecked conversion.
   */
  @SuppressWarnings("unchecked")
  private Collection<RelationshipSet> loadRelationshipSet(
      ObjectXmlPersist persist, IFile file) throws IOException {
    return (Collection<RelationshipSet>) persist.load(file.getLocationURI());
  }

  /**
   * Create a new file and save into it the set under <code>setName</code>.
   *
   * @param file a new file
   * @param setName the set name
   */
  private void newFileAndSet(IFile file, String setName)
      throws IOException, CoreException {
    RelationshipSet set = DefaultRelationshipSet.SET;

    Collection<RelationshipSet> collection = new ArrayList<RelationshipSet>();
    collection.add(set);

    // save data in the file
    // TODO(leeca):  Is this configured with the correct XStream flavor?
    ObjectXmlPersist persist =
        new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
    persist.save(file.getLocationURI(), collection);
    file.refreshLocal(IResource.DEPTH_ZERO, null);
  }

  @Override
  public void addPages() {
    page = new NewRelationshipSetPage();
    addPage(page);
  }

  @Override
  public void init(IWorkbench iworkbench, IStructuredSelection selection) {
  }
}
