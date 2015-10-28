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

package com.google.devtools.depan.eclipse.wizards;

import com.google.devtools.depan.eclipse.editors.GraphDocument;
import com.google.devtools.depan.eclipse.editors.ResourceCache;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.model.GraphModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
 * A standard framework for implementing an DepAn analysis wizard.
 * With the accompanying {@link AbstractAnalysisPage}, new forms of
 * dependency analysis can be implemented in two simple steps:
 * <ol>
 * <li>Create an source control to accept the users input.</li>
 * <li>Create a generateAnalysisGraph() method to generate the result.</li>
 * </ol>
 * Other routine steps, especially persistence, is handled by this class.
 */
public abstract class AbstractAnalysisWizard extends Wizard
    implements INewWizard {

  private ISelection selection;

  /**
   * Constructor for NewGraphWizard.
   * Create the wizard (with a progress monitor)
   */
  public AbstractAnalysisWizard() {
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
        performAnalysis(monitor);
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
  protected void performAnalysis(IProgressMonitor monitor)
      throws InvocationTargetException {

    try {
      monitor.beginTask(
          "Creating " + getOutputFileName(), 2 + countAnalysisWork());

      GraphDocument graph = generateAnalysisDocument(monitor);

      saveAnalysisDocument(monitor, graph);
    } catch (CoreException e) {
      throw new InvocationTargetException(e);
    } catch (IOException errIo) {
      throw new InvocationTargetException(
          errIo, "Unable to store " + getOutputFileName());
    } finally {
      monitor.done();
    }
  }

  /**
   * Create a new {@link GraphDocument} with the list of analysis plugins.
   * The first listed plugin becomes the UI default for the document.
   */
  protected GraphDocument createGraphDocument(
      GraphModel graph, String... pluginIds) {
    return new GraphDocument(graph, 
      SourcePluginRegistry.buildPluginList(pluginIds));
  }

  /**
   * Provide a count of the number of {@code monitor.worked()} calls that
   * the {@link generateAnalysis()} method will generate.
   * 
   * This is a Template method that extending classes are intended to override.
   * 
   * @return count of the number of {@code monitor.worked()} to expect
   */
  protected abstract int countAnalysisWork();

  /**
   * Using the wizard's internal data, produce an analysis graph
   * that should be saved.
   * 
   * This is a Template method that extending classes are intended to override.
   * 
   * @param monitor receiver for {@code monitor.worked()} calls
   */
  protected abstract GraphDocument generateAnalysisDocument(
      IProgressMonitor monitor)
      throws CoreException, IOException;

  /**
   * Provide the name of the output file to generate.
   */
  protected abstract String getOutputFileName();

  /**
   * Provide the actual {@code IFile} object that should receive the
   * analysis graph.
   * 
   * @return {@code IFile} ready to write to
   */
  protected abstract IFile getOutputFile() throws CoreException; 

  /**
   * Save the graph generated by a doFinish implementation.
   * 
   * Note that this generates two (2) {@code monitor.worked()} calls.
   * 
   * @param monitor receiver for 2 {@code monitor.worked()} calls
   * @param graph analysis graph to write
   */
  private void saveAnalysisDocument(
      IProgressMonitor monitor, GraphDocument graph)
      throws CoreException {
    monitor.setTaskName("Getting File...");

    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Writing file...");

    ResourceCache.storeGraphDocument(file, graph);

    monitor.worked(1);
  }
}
