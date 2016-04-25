/*
 * Copyright 2015 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.Resources;
import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Provide tree structured viewer for all node lists.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 *
 * @param <T>
 */
public class GraphNodeViewer<T> {

  NodeViewerProvider nvProvider;

  // UX Elements
  /** Provides a tree view of the collapseData. */
  private TreeViewer treeViewer;

  public void setNvProvider(NodeViewerProvider nvProvider) {
    this.nvProvider = nvProvider;
  }

  public void refresh() {
    ViewerRoot treeRoots = nvProvider.buildViewerRoots();
    treeViewer.setInput(treeRoots);
  }

  public Composite createNodeViewer(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(1, false));

    Composite optionsSection = new Composite(result, SWT.NONE);
    optionsSection.setLayout(new GridLayout(1, false));
    optionsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    ToolBar rightOptions = createToolBar(optionsSection);
    rightOptions.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

    ViewerSorter sorter = nvProvider.getViewSorter();
    treeViewer = createTreeViewer(result, sorter);
    treeViewer.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    return result;
  }

  private TreeViewer createTreeViewer(Composite parent, ViewerSorter sorter) {
    int style = SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL;
    TreeViewer result = new TreeViewer(parent, style);
    result.setLabelProvider(new WorkbenchLabelProvider());
    result.setContentProvider(new BaseWorkbenchContentProvider());
    result.setSorter(sorter);

    setupHierarchyMenu(result);

    return result;
  }

  private void setupHierarchyMenu(final TreeViewer viewer) {
    MenuManager menuMgr = new MenuManager();

    Menu menu = menuMgr.createContextMenu(viewer.getControl());

    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(IMenuManager manager) {
        ISelection selection = viewer.getSelection();
        if (selection.isEmpty()) {
          return;
        }

        if (!(selection instanceof IStructuredSelection)) {
          return;
        }
        IStructuredSelection items = (IStructuredSelection) selection;
        if (items.size() > 1) {
          addMultiActions(manager);
          return;
        }

        Object menuElement = items.getFirstElement();
        addItemActions(manager, menuElement);
      }});

    menuMgr.setRemoveAllWhenShown(true);
    viewer.getControl().setMenu(menu);
  }

  private void addMultiActions(IMenuManager manager) {
    nvProvider.addMultiActions(manager);
  }

  private void addItemActions(IMenuManager manager, Object menuElement) {
    nvProvider.addItemActions(manager, menuElement);
  }

  // Tree View Toolbar

  private ToolBar createToolBar(Composite parent) {

    ToolBar result = new ToolBar(parent, SWT.NONE | SWT.FLAT | SWT.RIGHT);
    result.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

    ToolItem collapseButton = createCollapseAllPushIcon(result);
    collapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapseAll();
      }
    });

    ToolItem expandButton = createExpandAllPushIcon(result);
    expandButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        expandAll();
      }
    });

    return result;
  }

  private ToolItem createCollapseAllPushIcon(ToolBar parent) {
    ToolItem result = new ToolItem(parent, SWT.PUSH | SWT.FLAT);
    Image icon = PlatformUI.getWorkbench().getSharedImages().getImage(
        ISharedImages.IMG_ELCL_COLLAPSEALL);
    result.setImage(icon);
    return result;
  }

  private ToolItem createExpandAllPushIcon(ToolBar parent) {
    ToolItem result = new ToolItem(parent, SWT.PUSH | SWT.FLAT);
    result.setImage(Resources.IMAGE_EXPANDALL);
    return result;
  }

  private void collapseAll() {
    treeViewer.collapseAll();
  }

  private void expandAll() {
    treeViewer.expandAll();
  }
}
