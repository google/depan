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

package com.google.devtools.depan.java.bytecode.eclipse;

import com.google.devtools.depan.filesystem.FileSystemRelationContributor;
import com.google.devtools.depan.filesystem.builder.TreeLoader;
import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AbstractAnalysisWizard;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.java.JavaRelationContributor;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;
import com.google.devtools.depan.model.builder.chain.ElementFilter;
import com.google.devtools.depan.platform.jobs.ProgressListener;
import com.google.devtools.depan.platform.jobs.ProgressListenerMonitor;
import com.google.devtools.depan.platform.jobs.QuickProgressListener;

import com.google.common.collect.Lists;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

/**
 * Wizard for converting a set of Java {@code .class} files into a DepAn
 * analysis graph.  Based on user input, it can read for a {@code .jar} file
 * or a directory tree.
 */
public class NewGraphWizard extends AbstractAnalysisWizard {

  private static final Logger logger =
      Logger.getLogger(NewGraphWizard.class.getName());

  private final ClassAnalysisStats analysisStats = new ClassAnalysisStats();

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
  protected GraphDocument generateAnalysisDocument(IProgressMonitor monitor)
      throws IOException {

    // Step 1) Create the GraphModel to hold the analysis results
    String classPath = page.getClassPath();
    String directoryFilter = page.getDirectoryFilter();
    String packageFilter = page.getPackageFilter();

    // TODO(leeca): Extend UI to allow lists of packages.
    ElementFilter filter = DefaultElementFilter.build(packageFilter);

    GraphBuilder graphBuilder = GraphBuilders.createGraphModelBuilder();
    DependenciesListener builder =
        new DependenciesDispatcher(filter, graphBuilder);

    // TODO(leeca): Extend UI to allow lists of directories.
    Collection<String> directoryWhitelist = splitFilter(directoryFilter);

    monitor.worked(1);

    // Step 2) Read in the class files, depending on the source
    monitor.setTaskName("Load Classes...");

    ProgressListener baseProgress = new ProgressListenerMonitor(monitor);
    ProgressListener quickProgress = new QuickProgressListener(
        baseProgress, 300);

    if (classPath.endsWith(".jar") || classPath.endsWith(".zip")) {
      readZipFile(classPath, builder, quickProgress);
    } else {
      readTree(classPath, builder, quickProgress);
    }

    logger.info(
        analysisStats.getClassesLoaded() + "/" + analysisStats.getClassesTotal()
        + " classes loaded. " + analysisStats.getClassesFailed() + " failed.");

    monitor.worked(1);

    GraphModel resultGraph = graphBuilder.createGraphModel();

    DependencyModel.Builder modelBuilder = new DependencyModel.Builder();
    modelBuilder.addRelationContrib(FileSystemRelationContributor.ID);
    modelBuilder.addRelationContrib(JavaRelationContributor.ID);

    return new GraphDocument(modelBuilder.build(), resultGraph);
  }

  /**
   * Build Java dependencies from a Jar file.
   * 
   * @param classPath path to Jar file
   * @param builder destination of discovered dependencies
   * @param progress indicator for user interface
   * @throws IOException
   */
  private void readZipFile(
      String classPath, DependenciesListener builder,
      ProgressListener progress) throws IOException {

    ClassFileReader reader = new ClassFileReader(analysisStats);
    ZipFile zipFile = new ZipFile(classPath);
    JarFileLister jarReader =
        new JarFileLister(zipFile, builder, reader, progress);
    jarReader.start();
  }

  /**
   * Build Java dependencies from a file system tree.
   * 
   * @param classPath root of directory tree
   * @param builder destination of discovered dependencies
   * @param progress indicator for user interface
   * @throws IOException
   */
  private void readTree(
      String classPath, DependenciesListener builder,
      ProgressListener progress) throws IOException {

    // TODO(leeca): Instead of just assuming one level of path retention,
    // let the user decide like in NewFileSystemWizard.  But first, that needs
    // to be cleaned up and refactored.
    String treePrefix = new File(classPath).getParent();

    ClassFileReader reader = new ClassFileReader(analysisStats);

    TreeLoader loader =
        new ClassTreeLoader(treePrefix, builder, reader, progress);
    loader.analyzeTree(classPath);
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
