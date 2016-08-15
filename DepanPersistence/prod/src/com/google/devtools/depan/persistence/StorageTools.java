/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.persistence;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.net.URI;

/**
 * A utility class that provides static methods for manipulating Eclipse
 * files and resources.
 * 
 * Unlike the source type {@code WorkspaceTools}, this class should not
 * access any Eclipse workspace or UX components.  It can access the Eclipse
 * provided resource and file management capabilities.
 * 
 * Extracted from an earlier {@code WorkspaceTools}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public final class StorageTools {

  private StorageTools() {
    // Prevent instantiation.
  }

  /**
   * Guess a filename for a new file.  For example, if an initial filename
   * of {@code Tree.dgi} already exist, the first guess will be
   * {@code Tree (1).dgi}.
   * 
   * This is best-effort heuristic, and is not guaranteed to actually be an
   * unused filename.  If the application wishes to ensure that no existing
   * file is overwritten, additional checks are required in the application.
   * 
   * The implemented heuristic follows these lines:
   * <ol>
   * <li>if the initial proposal is unused, provide that filename.</li>
   * <li>for a limited number of trials, insert a sequence number before the
   *     filename's extension. If this modified filename is unused, provide it
   *     as the result.</li>
   * </ol>
   * In all other cases, return the initial proposal.
   * 
   * Since the filename guessing heuristic is non-atomic with the actual file
   * creation, the filename may exist by the time the application tries to
   * create the file.  Additionally, if the heuristic gives up, the returned
   * filename may exist anyway.
   * 
   * The numbered trials names begin with {@code start}, and end before the
   * {@code limit} is reached.  Thus, a start of 1 and limit of 10 will 
   * try the values 1 through 9.  If the limit is less then the start value,
   * no variants are checked.
   * 
   * @param container intended parent of new file
   * @param newFilename initial proposal for new filename
   * @param start lowest number to use for filename variants
   * @param limit stopping number for file variants
   * @return the recommended filename to use
   */
  public static String guessNewFilename(
      IContainer container, String newFilename, int start, int limit) {
    // Quick exit if container is no help
    if (null == container) {
      return newFilename;
    }

    // No point in testing if no variants are allowed
    if (limit < start) {
      return newFilename;
    }

    // Quick exit if proposed name does not exist
    IPath newPath = Path.fromOSString(newFilename);
    if (!container.exists(newPath)) {
      return newFilename;
    }

    // Try to find an unused numbered variant
    int trial = 1;
    String ext = newPath.getFileExtension();
    String base = newPath.removeFileExtension().toOSString();
    do {
      newPath = Path.fromOSString(base + " (" + trial + ")")
          .addFileExtension(ext);
      if (!container.exists(newPath)) {
        return newPath.toOSString();
      }
      ++trial;
    } while (trial < limit);

    // Fall back to the bare filename
    return newFilename;
  }

  /**
   * Can't be part of {@link AbstractDocXmlPersist}, due to UI elements
   * {@code monitor} and {@code file.refreshLocal()}.
   * 
   * Cancels the {@code monitor} if there is an exception,
   * but reports no worked steps on the supplied  {@code monitor}.
   */
  public static <T> void saveDocument(
      IFile file, T docInfo,
      AbstractDocXmlPersist<T> persist,
      IProgressMonitor monitor) {
    URI location = file.getLocationURI();
    try {
      persist.save(location, docInfo);
      file.refreshLocal(IResource.DEPTH_ZERO, monitor);
    } catch (Exception err) {
      if (null != monitor) {
        monitor.setCanceled(true);
      }
      persist.logSaveException(location, err);
    }
  }
}
