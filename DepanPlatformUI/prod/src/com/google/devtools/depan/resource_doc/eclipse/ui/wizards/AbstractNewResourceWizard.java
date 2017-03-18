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
import com.google.devtools.depan.platform.eclipse.ui.wizards.AbstractNewDocumentWizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
public abstract class AbstractNewResourceWizard<T>
    extends AbstractNewDocumentWizard<T> {

  /**
   * Needs to be consistent with number of {@link IProgressMonitor#worked(int)}
   * calls executed in {@link #saveDocumentTask(IProgressMonitor, Object)}.
   */
  private static final int SAVE_DOCUMENT_STEPS = 3;

  /////////////////////////////////////
  // Implemented hooks

  @Override
  protected int countSaveWork() {
    return SAVE_DOCUMENT_STEPS;
  }

  @Override
  protected void saveNewDocument(IProgressMonitor monitor, T doc)
      throws CoreException {

    monitor.setTaskName("Building document...");
    final IFile file = getOutputFile();
    monitor.worked(1);

    monitor.setTaskName("Saving document...");
    AbstractDocXmlPersist<T> persist = getDocXmlPersist();
    persist.saveDocument(file, doc, monitor);
    monitor.worked(1);

    monitor.setTaskName("Refreshing resources ...");
    file.refreshLocal(IResource.DEPTH_ZERO, null);
    monitor.worked(1);
  }

  /////////////////////////////////////
  // Hook methods

  protected abstract AbstractDocXmlPersist<T> getDocXmlPersist();
}
