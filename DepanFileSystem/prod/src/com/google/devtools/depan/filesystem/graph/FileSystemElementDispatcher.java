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

import com.google.devtools.depan.model.Element;

/**
 * Element Dispatcher for File System Plug-in elements. Provides a mechanism to
 * visit all types of elements through a single call to match in this class. All
 * elements has to implement <code>void accept(ElementVisitor visitor)</code>
 * method while all dispatchers has to implement various match methods.
 *
 * @author tugrul@google.com (Tugrul Ince)
 *
 * @param <R> Type of dispatcher which may range from color providers to image
 * providers.
 */
public abstract class FileSystemElementDispatcher<R> implements
    FileSystemElementVisitor {
  /**
   * The object that will be the final result of a call to <code>match</code>.
   */
  protected R returnValue = null;

  /**
   * Performs tasks of this dispatcher on a FileElement. The expected result
   * depends on the purpose of the dispatcher.
   *
   * @param fileElement {@link FileElement} on which this dispatcher will
   * operate.
   * @return Result of performing the tasks of this dispatcher on this element.
   */
  public abstract R match(FileElement fileElement);

  /**
   * Performs tasks of this dispatcher on a DirectoryElement. The expected
   * result depends on the purpose of the dispatcher.
   *
   * @param directoryElement {@link DirectoryElement} on which this dispatcher
   * will operate.
   * @return Result of performing the tasks of this dispatcher on this element.
   */
  public abstract R match(DirectoryElement directoryElement);

  /**
   * Performs tasks of this dispatcher on the given element. This is the method
   * that must be called by the users of this dispatcher. The proper redirection
   * is handled by this dispatcher.
   *
   * @param element {@link Element} on which this dispatcher will operate.
   * @return Result of performing the tasks of this dispatcher on this element.
   */
  public R match(Element element) {
    element.accept(this);
    return returnValue;
  }

  @Override
  public void visitDirectoryElement(DirectoryElement element) {
    returnValue = match(element);
  }

  @Override
  public void visitFileElement(FileElement element) {
    returnValue = match(element);
  }
}
