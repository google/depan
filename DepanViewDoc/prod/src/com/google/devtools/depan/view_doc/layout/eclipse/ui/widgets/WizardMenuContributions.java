/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.view_doc.layout.eclipse.ui.handlers.LayoutNodesHandler;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class WizardMenuContributions extends CompoundContributionItem {

  public static final String MENU_ROOT = "eclipse.ui.main.menu.Edit.Layout.Wizards";

  @Override
  protected IContributionItem[] getContributionItems() {
    IWorkbenchWindow frame =
        PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    List<LayoutPlanDocument<? extends LayoutPlan>> topLayouts =
        LayoutResources.getTopLayouts();
    List<IContributionItem> result = 
        Lists.newArrayListWithExpectedSize(topLayouts.size());
    for (LayoutPlanDocument<? extends LayoutPlan> layoutDoc : topLayouts) {
      buildWizardItem(frame, layoutDoc.getName(), layoutDoc);
    }
    return Iterables.toArray(result, IContributionItem.class);
  }

  private IContributionItem buildWizardItem(
      IServiceLocator srvcLocator, String planId,
      LayoutPlanDocument<? extends LayoutPlan> planDoc) {
    String name = planDoc.getName();
    String id = MessageFormat.format("{0}.{1}", MENU_ROOT, name); 
    int style = CommandContributionItem.STYLE_PUSH;

    Map<String, String> parameters = LayoutNodesHandler.buildParameters(planId);
    IContributionItem result = new CommandContributionItem(
        new CommandContributionItemParameter(srvcLocator, id,
            LayoutNodesHandler.LAYOUT_COMMAND, parameters, null, null, null, name,
            null, null, style, null, false));

    return result;
  }
}
