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

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.eclipse.wizards.ProgressListenerMonitor;
import com.google.devtools.depan.java.bytecode.ClassLookup;
import com.google.devtools.depan.java.bytecode.JarFileLister;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.DependenciesListener;
import com.google.devtools.depan.model.builder.ElementFilter;
import com.google.devtools.depan.util.FileLister;
import com.google.devtools.depan.util.FileListerListener;
import com.google.devtools.depan.util.ProgressListener;
import com.google.devtools.depan.util.QuickProgressListener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipException;

/**
 * Wizard for converting a set of Java {@code .class} files into a DepAn
 * analysis graph.  Based on user input, it can read for a {@code .jar} file
 * or a directory tree.
 */
public class NewGraphWizard extends AbstractAnalysisWizard {

  private NewGraphPage page;

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
    page = new NewGraphPage(getSelection());
    addPage(page);
  }

  @Override
  protected String getOutputFileName() {
    return page.getOutputFileName();
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected int countAnalysisWork() {
    return 2;
  }

  /**
   * Create an analysis graph by analyzing the .class files on the classpath.
   */
  @Override
  protected GraphModel generateAnalysisGraph(IProgressMonitor monitor) {

    // Step 1) Create the GraphModel to hold the analysis results
    String classPath = page.getClassPath();
    File classPathFile = new File(classPath);
    String directoryFilter = page.getDirectoryFilter();
    String packageFilter = page.getPackageFilter();

    // TODO(leeca): Extend UI to allow lists of packages.
    Collection<String> packageWhitelist = splitFilter(packageFilter);
    ElementFilter filter = new DefaultElementFilter(packageWhitelist);

    GraphModel result = new GraphModel();
    DependenciesListener builder =
        new DependenciesDispatcher(filter, result.getBuilder());

    // TODO(leeca): Extend UI to allow lists of directories.
    Collection<String> directoryWhitelist = splitFilter(directoryFilter);
    FileListerListener cl = new ClassLookup(directoryWhitelist, builder);

    monitor.worked(1);

    // Step 2) Read in the class files, depending on the source
    monitor.setTaskName("Load Classes...");

    if (classPath.endsWith(".jar")
        || classPath.endsWith(".zip")) {
      JarFileLister fl;
      try {
        fl = new JarFileLister(classPathFile, cl);
        ProgressListener baseProgress = new ProgressListenerMonitor(monitor);
        ProgressListener quickProgress = new QuickProgressListener(
            baseProgress, 300);
        fl.setProgressListener(quickProgress);
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

    // Done
    return result;
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
}
