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

package com.google.devtools.depan.filesystem.graph;

import com.google.devtools.depan.model.ElementVisitor;

/**
 * Lists the requirements of any {@link ElementVisitor} that operates on file
 * system elements.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public interface FileSystemElementVisitor extends ElementVisitor {
  /**
   * Performs the tasks of this visitor on the given {@link DirectoryElement}.
   *
   * @param element {@link DirectoryElement} on which the tasks of this visitor
   * will be performed.
   */
  public void visitDirectoryElement(DirectoryElement element);

  /**
   * Performs the tasks of this visitor on the given {@link FileElement}.
   *
   * @param element {@link FileElement} on which the tasks of this visitor
   * will be performed.
   */
  public void visitFileElement(FileElement element);
}
