/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.java.bytecode.eclipse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.zip.ZipException;

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

import com.google.devtools.depan.collect.Lists;
import com.google.devtools.depan.java.bytecode.ClassLookup;
import com.google.devtools.depan.java.bytecode.DependenciesDispatcher;
import com.google.devtools.depan.java.bytecode.DependenciesListener;
import com.google.devtools.depan.java.bytecode.JarFileLister;
import com.google.devtools.depan.java.graph.DefaultElementFilter;
import com.google.devtools.depan.java.graph.ElementFilter;
import com.google.devtools.depan.java.graph.JavaElements;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.XmlPersistentGraph;
import com.google.devtools.depan.util.FileLister;
import com.google.devtools.depan.util.FileListerListener;
import com.google.devtools.depan.util.ProgressListener;
import com.google.devtools.depan.util.QuickProgressListener;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the
 * provided container. If the container resource (a folder or a project) is
 * selected in the workspace when the wizard is opened, it will accept it as the
 * target container. The wizard creates one file with the extension "dpang". If
 * a sample multi-page editor (also available as a template) is registered for
 * the same extension, it will be able to open it.
 */
public class NewGraphWizard extends Wizard implements INewWizard,
    ProgressListener {
  private NewGraphPage page;
  //private NewGraphPage2 page2;
  private ISelection selection;

  private IProgressMonitor progressMonitor;

  /**
   * Constructor for NewGraphWizard.
   */
  public NewGraphWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  /**
   * Adding the page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewGraphPage(selection);
    addPage(page);
  }

  /**
   * This method is called when 'Finish' button is pressed in the wizard. We
   * will create an operation and run it using wizard as execution context.
   */
  @Override
  public boolean performFinish() {
    final String containerName = page.getContainerName();
    final String fileName = page.getFileName();
    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor)
          throws InvocationTargetException {
        try {
          doFinish(containerName, fileName, monitor);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        } catch (IOException errIo) {
          throw new InvocationTargetException(
            errIo, "Unable to store " + fileName);
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
   * For now, split a filter input line into a filter whitelist.  In the future
   * a better UI would be appropriate.
   *
   * Split the input line on spaces, and build up the whitelist from the split()
   * results.  If the generated whitelist is empty, add on empty string to the
   * whitelist so that it matches all packages or directories.
   *
   * @param formFilter user input with possibly multiple patterns
   *     for a whitelist
   * @return Collection of Strings suitable for a whitelist.
   */
  private Collection<String> splitFilter(String formFilter) {
    Collection<String> result = Lists.newArrayList();
    for (String filter : formFilter.split("\\p{Space}+")) {
      if ((filter != null) && (!filter.isEmpty())) {
        result.add(filter);
      }
    }

    // If the constructed filter wound up empty, make it accept everything
    if (result.size() <= 0) {
      result.add("");
    }
    return result;
  }

  /**
   * The worker method. It will find the container, create the file if missing
   * or just replace its contents, and open the editor on the newly created
   * file.
   */
  private void doFinish(String containerName, String fileName,
      IProgressMonitor monitor) throws CoreException, IOException {
    this.progressMonitor = monitor;
    // create a file
    monitor.beginTask("Creating " + fileName, 5);

    monitor.setTaskName("Preparing data...");

    String classPath = page.getClassPath();
    File classPathFile = new File(classPath);
    String directoryFilter = page.getDirectoryFilter();
    String packageFilter = page.getPackageFilter();

    // TODO(leeca): Extend UI to allow lists of packages.
    Collection<String> packageWhitelist = splitFilter(packageFilter);
    ElementFilter filter = new DefaultElementFilter(packageWhitelist);

    GraphModel graph = new GraphModel();

    DependenciesListener dld =
      new DependenciesDispatcher(filter, graph.getBuilder());

    // TODO(leeca): Extend UI to allow lists of directories.
    Collection<String> directoryWhitelist = splitFilter(directoryFilter);
    FileListerListener cl = new ClassLookup(directoryWhitelist, dld);

    monitor.worked(1);
    monitor.setTaskName("Load Classes...");

    if (classPath.endsWith(".jar")
        || classPath.endsWith(".zip")) {
      JarFileLister fl;
      try {
        fl = new JarFileLister(classPathFile, cl);
        fl.setProgressListener(new QuickProgressListener(this, 300));
        fl.start();
      } catch (ZipException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      FileLister fl = new FileLister(classPathFile, cl);
      fl.start();
    }


    monitor.worked(1);
    monitor.setTaskName("Getting File...");

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IResource resource = root.findMember(new Path(containerName));
    if (!resource.exists() || !(resource instanceof IContainer)) {
      throwCoreException("Container \"" + containerName + "\" does not exist.");
    }
    IContainer container = (IContainer) resource;
    final IFile file = container.getFile(new Path(fileName));


    monitor.worked(1);
    monitor.setTaskName("Writing file...");

    // PersistentGraph persist = new PersistentGraph(graph);
    // persist.save(file.getLocationURI());

    XmlPersistentGraph persist = new XmlPersistentGraph();
    persist.config(JavaElements.configXmlPersist);
    persist.save(file.getLocationURI(), graph);

    file.refreshLocal(IResource.DEPTH_ZERO, null);
    System.out.println(file.getLocationURI());
    monitor.worked(1);
  }

  private void throwCoreException(String message) throws CoreException {
    IStatus status = new Status(IStatus.ERROR, "com.google.devtools.depan",
        IStatus.OK, message, null);
    throw new CoreException(status);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IWorkbenchWizard #init(org.eclipse.ui.IWorkbench,
   *      org.eclipse.jface.viewers.IStructuredSelection)
   */
  public void init(IWorkbench workbench, IStructuredSelection sel) {
    this.selection = sel;
  }

  public void progress(String curentJob, int n, int total) {
    progressMonitor
        .setTaskName("Loading " + n + "/" + total + ": " + curentJob);
  }

}
