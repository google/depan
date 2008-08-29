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

package com.google.devtools.depan.eclipse;

import com.google.devtools.depan.eclipse.natures.NewDepanProjectWizard;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.views.Tools;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class Perspective implements IPerspectiveFactory {

  public static final String PERSPECTIVE_ID =
      "com.google.devtools.depan.eclipse.perspective";

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IPerspectiveFactory
   *      #createInitialLayout(org.eclipse.ui.IPageLayout)
   */
  public void createInitialLayout(IPageLayout layout) {
    // Define shortcuts for DepAn perspective
    layout.addShowViewShortcut(Tools.VIEW_ID);
    layout.addNewWizardShortcut(NewDepanProjectWizard.WIZARD_ID);
    addSourceAnalysisWizards(layout);

    // Define screen regions for DepAn perspective
    IFolderLayout folder = layout.createFolder("views",
        IPageLayout.RIGHT, 0.7F, layout.getEditorArea());
    folder.addView(IPageLayout.ID_RES_NAV);
    folder.addView(Tools.VIEW_ID);
  }

  /**
   * Add all the new dependency analysis wizards from all the
   * source plugins.
   */
  private void addSourceAnalysisWizards(IPageLayout layout) {
    for (SourcePlugin sourcePlugin : SourcePluginRegistry.getInstances()) {
      for (String newAnalysisId : sourcePlugin.getNewAnalysisIds()) {
        layout.addNewWizardShortcut(newAnalysisId);
      }
    }
  }
}
