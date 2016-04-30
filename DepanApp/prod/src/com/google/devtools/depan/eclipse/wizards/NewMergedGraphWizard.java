/*
 * Copyright 2009 The Depan Project Authors
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

import com.google.devtools.depan.eclipse.editors.ResourceCache;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.platform.wizards.AbstractAnalysisWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.util.List;

/**
 * Create a new dependency graph by merging the nodes and edges from two
 * or more existing dependency graphs.
 * 
 * Ideas for future enhancements include:
 * - user-selectable option to control whether duplicate edges from different
 *   dependency graphs are included.
 * - renaming nodes from one dependency graph to match the identities in
 *   another graph (e.g. add parent directories to Directory and File nodes).
 * - richer UI presentation of .dgi resources from multiple projects.  For
 *   example, right now it is hard for the user to tell which Java.dgi from
 *   multiple projects to include.  For now, the user just has to be careful.
 *   This is not a problem if all analyzes are in a single DepAn project.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NewMergedGraphWizard extends AbstractAnalysisWizard {

  /*
  * Eclipse extension identifier for this wizard.
  */
  public static final String ANALYSIS_WIZARD_ID =
      "com.google.devtools.depan.eclipse.wizards.NewMergedGraph";

  /**
   * Wizard page with user-selected options.
   */
  private NewMergedGraphPage page;

  /**
   * Adding the user options page to the wizard.
   */
  @Override
  public void addPages() {
    page = new NewMergedGraphPage(getSelection());
    addPage(page);
  }

  /**
   * {@inheritDoc}
   * 
   * Count 1 step for creating the graph model, plus a load and a merge step
   * for each dependency graph (1 +2n). 
   * @see com.google.devtools.depan.platform.wizards.AbstractAnalysisWizard#countAnalysisWork()
   */
  @Override
  protected int countAnalysisWork() {
    return 1 + (2 * page.getMergeGraphs().size());
  }

  /**
   * @return a composite graph document, or {@code null} if the merge list
   *     is emtpy.  This should not happen with well behaved UIs.
   */
  @Override
  protected GraphDocument generateAnalysisDocument(IProgressMonitor monitor) {

    List<IResource> mergeGraphs = page.getMergeGraphs();
    if (mergeGraphs.size() <= 0) {
      return null;
    }

    monitor.setTaskName("Creating result graph...");
    MergeGraphBuilder resultBuilder = new MergeGraphBuilder();
    monitor.worked(1);

    for (IResource graphResource : mergeGraphs) {
      String graphName = graphResource.getName();
      monitor.setTaskName("Loading dependency graph " + graphName + "...");
      GraphDocument nextGraph =
          ResourceCache.importGraphDocument((IFile) graphResource);
      monitor.worked(1);

      String taskName = "Merging dependency graph " + graphName + "...";
      monitor.setTaskName(taskName);
      resultBuilder.merge(nextGraph);
      monitor.worked(1);
    }

    GraphDocument result = resultBuilder.getGraphDocument();
    return result;
  }

  @Override
  protected IFile getOutputFile() throws CoreException {
    return page.getOutputFile();
  }

  @Override
  protected String getOutputFileName() {
    return page.getOutputFileName();
  }
}
