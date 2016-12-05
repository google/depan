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

package com.google.devtools.depan.platform.eclipse.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Define a part that allows the user to specify the destination
 * output file for the wizard's result.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface NewDocumentOutputPart extends NewWizardOptionPart {

  /**
   * Provide the actual {@code IFile} object that should receive the
   * analysis graph.
   * 
   * @return {@code IFile} ready to write to
   */
  IFile getOutputFile() throws CoreException;

  /**
   * Provide the name of the output file to generate.
   * This name is used in error messages.
   */
  String getFilename();
}
