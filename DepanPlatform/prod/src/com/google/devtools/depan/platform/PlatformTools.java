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

package com.google.devtools.depan.platform;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * Common platform services.  These are independent of Eclipse's notion
 * of a workspace.  Workspace dependent utility methods can be placed in the
 * {@code com.google.devtools.depan.platform.WorkspaceTools}.
 * 
 * Extracted from an earlier {@code WorkspaceTools}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PlatformTools {

  private PlatformTools() {
    // Prevent instantiation.
  }

  public static void throwCoreException(
      String message, String pluginId) throws CoreException {
    IStatus status = new Status(
        IStatus.ERROR, pluginId, IStatus.OK, message, null);
    throw new CoreException(status);
  }

  /**
   * Provide the text of the last resource segment,
   * with any extension removed.
   */
  public static String getBaseName(IResource resouce) {
    String name = resouce.getName();
    String ext = resouce.getFileExtension();
    // If null, no period is present
    if (null == ext) {
      return name;
    }
    // remove period and extension from end
    return name.substring(0, name.length() - 1 - ext.length());
  }

  /**
   * Do all string to path conversions here, to ensure consistency throughout
   * DepAn.
   */
  public static IPath buildPath(String pathText) {
    return Path.fromOSString(pathText);
  }

  /**
   * Do all path to string conversions here, to ensure consistency throughout
   * DepAn.
   */
  public static String fromPath(IPath path) {
    return path.toOSString();
  }

  public static IFile buildResourceFile(
      IContainer container, String fileName) {
    IFile result = container.getFile(buildPath(fileName));
    return result;
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
    IPath newPath = buildPath(newFilename);
    if (!container.exists(newPath)) {
      return newFilename;
    }
  
    // Try to find an unused numbered variant
    int trial = 1;
    String ext = newPath.getFileExtension();
    String base = fromPath(newPath.removeFileExtension());
    do {
      newPath = buildPath(base + " (" + trial + ")")
          .addFileExtension(ext);
      if (!container.exists(newPath)) {
        return fromPath(newPath);
      }
      ++trial;
    } while (trial < limit);
  
    // Fall back to the bare filename
    return newFilename;
  }

  public static String getBaseNameExt(String baseName, String ext) {
    return fromPath(getBaseNameExtPath(baseName, ext));
  }

  public static IPath getBaseNameExtPath(String baseName, String ext) {
    return new Path(baseName).addFileExtension(ext);
  }
}
