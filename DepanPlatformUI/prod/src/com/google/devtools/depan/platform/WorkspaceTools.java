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

import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * A utility class that provides static methods for manipulating Eclipse
 * workspaces and related entities.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public final class WorkspaceTools {

  private WorkspaceTools() {
    // Prevent instantiation.
  }

  public static IFile buildResourceFile(IPath path) {
    return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
  }

  public static IResource buildWorkspaceResource(IPath path) {
    return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
  }

  public static IFile buildResourceFile(String saveFilename) {
    IPath savePath = Path.fromOSString(saveFilename);
    return buildResourceFile(savePath);
  }

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
    return buildResourceFile(savePath);
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
    return buildResourceFile(savePath);
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
   * Open a dialog box asking the user to select an existing project under the
   * current workspace.
   *
   * @param parentShell
   * @param title 
   */
  public static IResource selectFile(Shell parentShell, String title) {
    ElementTreeSelectionDialog dialog =
        new ElementTreeSelectionDialog(
            parentShell,
            new WorkbenchLabelProvider(),
            new WorkbenchContentProvider()
        );

    dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
    dialog.setTitle(title);
    dialog.setAllowMultiple(false);

    if(dialog.open() == ElementTreeSelectionDialog.OK) {
      return (IResource) dialog.getFirstResult();
    }
    return null;
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
    Object obj = Selections.getFirstObject(selection);
    if (obj == null) {
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
    if (Selections.getObjects(selection).size() > 1) {
      return null;
    }

    // A selected container is a good answer
    if (obj instanceof IContainer) {
      return (IContainer) obj;
    }

    // If the selected container is any other kind of resource,
    // guess that the user meant that resource's container.
    if (obj instanceof IResource) {
      return ((IResource) obj).getParent();
    }

    // No reasonable guess for initial container
    return null;
  }

  /**
   * Launch the runner with an event loop tied to the current display.
   */
  public static void asyncExec(Runnable runner) {
    PlatformUI.getWorkbench().getDisplay().asyncExec(runner);
  }
}
