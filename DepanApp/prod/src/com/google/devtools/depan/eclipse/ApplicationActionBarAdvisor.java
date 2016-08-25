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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

  private IWorkbenchAction exitAction;
  private IWorkbenchAction aboutAction;
  private IWorkbenchAction saveAction;
  private IWorkbenchAction saveAsAction;
  private IWorkbenchAction preferencesAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
  }

  @Override
  protected void makeActions(IWorkbenchWindow window) {
    exitAction = ActionFactory.QUIT.create(window);
    register(exitAction);
    aboutAction = ActionFactory.ABOUT.create(window);
    register(aboutAction);
    saveAction = ActionFactory.SAVE.create(window);
    register(saveAction);
    saveAsAction = ActionFactory.SAVE_AS.create(window);
    register(saveAsAction);
    preferencesAction = ActionFactory.PREFERENCES.create(window);
    register(preferencesAction);
  }

  @Override
  protected void fillMenuBar(IMenuManager menuBar) {
    //DepAn menu actions
    MenuManager newMenu = new MenuManager("New...", "New...");
    IContributionItem newWizards = ContributionItemFactory.NEW_WIZARD_SHORTLIST
        .create(getActionBarConfigurer().getWindowConfigurer().getWindow());
    newMenu.add(newWizards);

    // window menu actions
    MenuManager viewsMenu = new MenuManager("Views", "Views");
    IContributionItem views = ContributionItemFactory.VIEWS_SHORTLIST
        .create(getActionBarConfigurer().getWindowConfigurer().getWindow());
    viewsMenu.add(views);

    // top level menu
    MenuManager depanMenu = new MenuManager("&DepAn", "DepAn");
    depanMenu.add(newMenu);
    depanMenu.add(new Separator());
    depanMenu.add(saveAction);
    depanMenu.add(saveAsAction);
    depanMenu.add(new Separator());
    depanMenu.add(exitAction);

    MenuManager windowMenu = new MenuManager("&Window", "Window");
    windowMenu.add(viewsMenu);
    windowMenu.add(preferencesAction);

    MenuManager helpMenu = new MenuManager("&Help", "Help");
    helpMenu.add(aboutAction);

    menuBar.add(depanMenu);
    // Room for Edit, View, etc.
    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    menuBar.add(windowMenu);
    menuBar.add(helpMenu);
  }

  @Override
  protected void fillCoolBar(ICoolBarManager coolBar) {
    IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle());
    toolbar.add(saveAction);
    toolbar.add(ContributionItemFactory.NEW_WIZARD_SHORTLIST
        .create(getActionBarConfigurer().getWindowConfigurer().getWindow()));

    coolBar.add(toolbar);
    // allow contributions here with id "additions" (MB_ADDITIONS)
    coolBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
  }
}
