/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * A utility class returning a String for a project selected in a dialog box.
 * Primarily Used in wizards.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class WorkspaceProjectSelection {

  /**
   * Private constructor to avoid class instantiation: it's a utility class.
   */
  private WorkspaceProjectSelection() { }

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
    ContainerSelectionDialog dialog = new ContainerSelectionDialog(parentShell,
        ResourcesPlugin.getWorkspace().getRoot(), false,
        "Select new file container");
    if (dialog.open() == ContainerSelectionDialog.OK) {
      Object[] result = dialog.getResult();
      if (result.length == 1) {
        return ((Path) result[0]).toString();
      }
    }
    return defaultValue;
  }
}
