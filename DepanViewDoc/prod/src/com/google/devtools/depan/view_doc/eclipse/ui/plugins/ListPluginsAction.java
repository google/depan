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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * A simple action that opens a popup with a list of registered plugins.
 *
 * @author Yohann Coppel
 *
 */
public class ListPluginsAction implements IWorkbenchWindowActionDelegate {
  private IWorkbenchWindow window;

  @Override
  public void dispose() {
  }

  @Override
  public void init(IWorkbenchWindow window) {
    this.window = window;
  }

  @Override
  public void run(IAction action) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("List of installed source plugins:\n\n");

    IExtensionRegistry reg = Platform.getExtensionRegistry();
    // get the list of elements (plugin entries)
    IConfigurationElement[] extensions = reg
        .getConfigurationElementsFor(JoglPluginRegistry.EXTENTION_POINT);
    for (int i = 0; i < extensions.length; i++) {
      IConfigurationElement element = extensions[i];
      buffer.append(element.getAttribute("class"));
      buffer.append("\n");
    }
    MessageDialog.openInformation(window.getShell(),
        "Installed source plugins", buffer.toString());
  }

  @Override
  public void selectionChanged(IAction action, ISelection selection) {
  }
}
