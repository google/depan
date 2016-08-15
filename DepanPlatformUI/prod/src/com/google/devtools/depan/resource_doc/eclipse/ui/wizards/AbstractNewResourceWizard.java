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

package com.google.devtools.depan.resource_doc.eclipse.ui.wizards;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.persistence.StorageTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * A standard framework for implementing an new DepAn document wizard.
 * With the accompanying {@link AbstractNewDocumentPage}, new forms of
 * routine documents can be implemented in a few simple steps:
 * 
 * <ol>
 * <li>Create an source control to accept the users input.</li>
 * <li>Create a generateAnalysisGraph() method to generate the result.</li>
 * </ol>
 * Other routine steps, especially persistence, is handled by this class.
 */
public abstract class AbstractNewResourceWizard<T> extends Wizard
    implements INewWizard {

  /**
   * Needs to be consistent with number of {@link IProgressMonitor#worked(int)}
   * calls executed in {@link #saveDocumentTask(IProgressMonitor, Object)}.
   */
  private static final int SAVE_DOCUMENT_STEPS = 3;

  private ISelection selection;

  /**
   * Create the wizard, requesting a progress monitor.
   */
  public AbstractNewResourceWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  @Override
  public void init(IWorkbench workbench, IStructuredSelection sel) {
    this.selection = sel;
  }

  /**
   * This method is called when 'Finish' button is pressed in the wizard. We
   * will create an operation and run it using wizard as execution context.
   */
  @Override
  public boolean performFinish() {
    IRunnableWithProgress op = new IRunnableWithProgress() {

      @Override
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        storeDocument(monitor);
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

  protected ISelection getSelection() {
    return selection;
  }

  /**
   * Perform analysis of the data provided by the user, and save it
   * to disk.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   */
  protected void storeDocument(IProgressMonitor monitor)
      throws InvocationTargetException {

    String filename = getOutputFilename();
    try {
      monitor.beginTask(
          "Creating " + filename, SAVE_DOCUMENT_STEPS + countBuildWork());

      T graph = buildDocument(monitor);
      saveDocumentTask(monitor, graph);
    } catch (CoreException e) {
      throw new InvocationTargetException(e);
    } catch (IOException errIo) {
      String msg = "Unable to store " + filename;
      PersistenceLogger.logException(msg, errIo);
      throw new InvocationTargetException(errIo, msg);
    } finally {
      monitor.done();
    }
  }

  /////////////////////////////////////
  // Hook methods

  /**
   * Provide a count of the number of {@code monitor.worked()} calls that
   * the {@link generateAnalysis()} method will generate.
   * 
   * This is a Template method that extending classes are intended to override.
   * 
   * @return count of the number of {@code monitor.worked()} to expect
   */
  protected abstract int countBuildWork();

  /**
   * Using the wizard's internal data, produce an analysis graph
   * that should be saved.
   * 
   * This is a Template method that extending classes are intended to override.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   */
  protected abstract T buildDocument(IProgressMonitor monitor)
      throws CoreException, IOException;

  /**
   * Provide the name of the output file to generate.
   * This name is displayed in progress and log messages.
   */
  protected abstract String getOutputFilename();

  /**
   * Provide the actual {@code IFile} object that should receive the
   * analysis graph.
   * 
   * @return {@code IFile} ready to write to
   */
  protected abstract IFile getOutputFile() throws CoreException;

  protected IFile buildRootIFile(String filename) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFile(new Path(filename));
  }


  protected abstract AbstractDocXmlPersist<T> getDocXmlPersist();

  /**
   * Save the graph generated by a doFinish implementation.
   * 
   * Note that this generates two (2) {@code monitor.worked()} calls.
   * 
   * @param monitor receiver for 2 {@code monitor.worked()} calls
   * @param graph analysis graph to write
   */
  private void saveDocumentTask(IProgressMonitor monitor, T doc)
      throws CoreException {

    monitor.setTaskName("Building document...");
    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Saving document...");
    AbstractDocXmlPersist<T> persist = getDocXmlPersist();
    StorageTools.saveDocument(file, doc, persist, monitor);
    monitor.worked(1);

    monitor.setTaskName("Refreshing resources ...");
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    monitor.worked(1);
  }
}
