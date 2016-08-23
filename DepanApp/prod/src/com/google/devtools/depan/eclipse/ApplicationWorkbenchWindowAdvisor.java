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

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

  /**
   * @param configurer
   */
  public ApplicationWorkbenchWindowAdvisor(
      IWorkbenchWindowConfigurer configurer) {
    super(configurer);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.application.WorkbenchWindowAdvisor
   *      #createActionBarAdvisor(
   *      org.eclipse.ui.application.IActionBarConfigurer)
   */
  @Override
  public ActionBarAdvisor createActionBarAdvisor(
      IActionBarConfigurer configurer) {
    return new ApplicationActionBarAdvisor(configurer);
  }

  @Override
  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setInitialSize(new Point(750, 550));
    configurer.setShowMenuBar(true);
    configurer.setShowCoolBar(true);
    configurer.setShowStatusLine(true);
    configurer.setShowProgressIndicator(true);
    configurer.setTitle("DepAn");
  }
}
