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

package com.google.devtools.depan.eclipse;

import com.google.devtools.depan.eclipse.natures.NewDepanProjectWizard;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class Perspective implements IPerspectiveFactory {

  public static final String PERSPECTIVE_ID =
      "com.google.devtools.depan.eclipse.perspective";

  @Override
  public void createInitialLayout(IPageLayout layout) {
    // Define shortcuts for DepAn perspective
    // TODO: layout.addShowViewShortcut(Tools.VIEW_ID);
    layout.addNewWizardShortcut(NewDepanProjectWizard.WIZARD_ID);

    // Define screen regions for DepAn perspective
    IFolderLayout folder = layout.createFolder("views",
        IPageLayout.RIGHT, 0.7F, layout.getEditorArea());
    folder.addView(IPageLayout.ID_RES_NAV);
    // TODO: folder.addView(Tools.VIEW_ID);
  }
}
