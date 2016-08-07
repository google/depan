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

package com.google.devtools.depan.platform;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import java.net.URI;

/**
 * A utility class that provides static methods for manipulating Eclipse
 * workspaces and related entities.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public final class WorkspaceTools {

  /**
   * Prevent instantiation of this namespace class.
   */
  private WorkspaceTools() { }

  /**
   * Ensure that we have a file extension on the file name.
   * 
   * @param savePath Initial save path from user
   * @return valid IFile with an extension.
   */
  public static IFile calcFileWithExt(IFile saveFile, String ext) {
    IPath savePath = saveFile.getFullPath();
    String saveExt = savePath.getFileExtension();
    if (null == saveExt) {
      savePath = savePath.addFileExtension(ext);
    } else if (!saveExt.equals(ext)) {
      savePath = savePath.addFileExtension(ext);
    }
    return ResourcesPlugin.getWorkspace().getRoot().getFile(savePath);
  }

  /**
   * Ensure that we have a file extension on the file name.
   * 
   * @param savePath Initial save path from user
   * @return valid IFile with an extension.
   */
  public static IFile calcViewFile(IPath savePath, String ext) {
    String saveExt = savePath.getFileExtension();
    if (null == saveExt) {
      savePath = savePath.addFileExtension(ext);
    } else if (!saveExt.equals(ext)) {
      savePath = savePath.addFileExtension(ext);
    }
    return ResourcesPlugin.getWorkspace().getRoot().getFile(savePath);
  }

  /**
   * Open a dialog box asking the user to select an existing project under the
   * current workspace.
   *
   * @param parentShell
   * @param defaultValue
   * @return a String representing the name of the chosen project, or
   *         defaultValue if nothing was selected (or "cancel" button was
   *         pressed...)
   */
  public static String selectProject(Shell parentShell, String defaultValue) {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    ContainerSelectionDialog dialog = new ContainerSelectionDialog(
        parentShell, workspaceRoot, false, "Select new file container");
    if (dialog.open() == ContainerSelectionDialog.OK) {
      Object[] result = dialog.getResult();
      if (result.length == 1) {
        return ((Path) result[0]).toString();
      }
    }
    return defaultValue;
  }

  /**
   * Guess a likely container resource given the user's current selection.
   * 
   * The implemented heuristic follows these lines:
   * <ol>
   * <li>If there is no selection and the workspace has only one project.
   *     guess the project.</li>
   * <li>If the selection is a container, use that object.</li>
   * <li>If the selection is a non-container resource,
   *     guess that resource's container.</li>
   * </ol>
   * In all other cases, return {@code null}.
   *
   * @param selection current user selection, or {@code null} for something
   * based on the workspace context (e.g. a singleton project root in the
   * workspace).
   *
   * @return best guess of user's intended container,
   *     or {@code null} if no reasonable guess can be made.
   */
  public static IContainer guessContainer(ISelection selection) {
    // If the selection is no help, and the workspace has only one
    // project, use that.
    if ((null == selection) || selection.isEmpty() ||
        !(selection instanceof IStructuredSelection)) {
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      IProject[] projects = workspace.getRoot().getProjects();
      if (null == projects) {
        return null;
      }
      if (projects.length != 1) {
        return null;
      }
      return projects[0];
    }

    // Multiple selections make it hard to guess the user's intent.
    IStructuredSelection ssel = (IStructuredSelection) selection;
    if (ssel.size() != 1) {
      return null;
    }
    Object obj = ssel.getFirstElement();

    // A selected container is a good answer
    if (obj instanceof IContainer) {
      return (IContainer) obj;
    }

    // If the selected container is any other kind of resource,
    // guess that the user meant that resoure's container.
    if (obj instanceof IResource) {
      return ((IResource) obj).getParent();
    }

    // No reasonable guess for initial container
    return null;
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
   * {@code monitor} and {@code file.touch()}.
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
