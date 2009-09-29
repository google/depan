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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.RelationshipSetSelector;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;

/**
 * Collapse Tool. Provide a GUI to collapse and uncollapse nodes under another
 * node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTool extends ViewSelectionListenerTool
    implements RelationshipSelectorListener {

  /**
   * Content provider for selected nodes. Fill the list of selected nodes used
   * to choose the master node for a new collapse group.
   */
  private TableContentProvider<GraphNode> collapseMaster = null;

  /**
   * TODO: use those options for uncollapsing.
   */
  private static final String[] uncollapseOptions =
      new String[] {"normal", "in a new view"};

  private ComboViewer masterViewer;

  /* (non-Javadoc)
   * @see com.google.devtools.depan.eclipse.views.Tool#getComposite()
   */
  public Control setupComposite(Composite parent) {
    // first expand bar containing collapsing operations
    Composite topComposite = new Composite(parent, SWT.NONE);

    // components
    Group manualCollapse = new Group(topComposite, SWT.BORDER);
    Label labelCollapse = new Label(manualCollapse, SWT.NONE);
    masterViewer = new ComboViewer(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    Button eraseCollapse = new Button(manualCollapse, SWT.PUSH);
    Button collapseButton = new Button(manualCollapse, SWT.PUSH);

    Label labelUncollapse = new Label(manualCollapse, SWT.NONE);
    final CCombo uncollapseOpts =
      new CCombo(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    Button deleteCollapse = new Button(manualCollapse, SWT.PUSH);
    Button uncollapseButton = new Button(manualCollapse, SWT.PUSH);
    Button uncollapseAll = new Button(manualCollapse, SWT.PUSH);

    Group autoCollapse = new Group(topComposite, SWT.BORDER);
    final RelationshipSetSelector namedSet =
        new RelationshipSetSelector(autoCollapse);
    Button doAutoGrouping = new Button(autoCollapse, SWT.PUSH);

    // text
    manualCollapse.setText("Manual collapsing");
    labelCollapse.setText("Collapse under");
    eraseCollapse.setText("collapse / erase");
    collapseButton.setText("collapse / add");

    labelUncollapse.setText("Uncollapse");
    deleteCollapse.setText("uncollapse / Delete");
    uncollapseButton.setText("uncollapse");
    uncollapseAll.setText("Uncollapse All Selected");

    autoCollapse.setText("Automatic collapsing based on a relationship set");
    doAutoGrouping.setText("Collapse");

    // layout
    GridLayout topGrid = new GridLayout(1, false);
    GridLayout manualGrid = new GridLayout(2, true);
    GridLayout autoGrid = new GridLayout(3, false);
    topGrid.verticalSpacing = 9;
    manualGrid.marginWidth = 10;
    manualGrid.marginHeight = 10;
    autoGrid.marginWidth = 10;
    autoGrid.marginHeight = 10;
    topComposite.setLayout(topGrid);
    manualCollapse.setLayout(manualGrid);
    autoCollapse.setLayout(autoGrid);

    manualCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    labelCollapse.setLayoutData(
        new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
    masterViewer.getCombo().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));
    eraseCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));
    collapseButton.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));

    labelUncollapse.setLayoutData(
        new GridData(SWT.LEFT, SWT.FILL, true, false, 2, 1));
    uncollapseOpts.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    deleteCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));
    uncollapseButton.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));
    uncollapseAll.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

    autoCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    namedSet.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    // actions
    eraseCollapse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapse(true);
      }
    });
    collapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapse(false);
      }
    });
    deleteCollapse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseButton(uncollapseOpts.getSelectionIndex(), true);
      }
    });
    uncollapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseButton(uncollapseOpts.getSelectionIndex(), false);
      }
    });
    uncollapseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseAllSelected();
      }
    });
    doAutoGrouping.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        autoCollapse(namedSet.getSelection());
      }
    });

    // content
    collapseMaster = new TableContentProvider<GraphNode>();
    collapseMaster.initViewer(masterViewer);

    for (String s : uncollapseOptions) {
      uncollapseOpts.add(s);
    }
    //FIXME(yc): select first set
    //namedSet.selectSet(BuiltinRelationshipSets.CONTAINER);

    return topComposite;
  }

  /**
   * Autocollapse the current graph.
   * Redraws are triggered by event propogation.
   *
   * @param finder
   */
  protected void autoCollapse(DirectedRelationFinder finder) {
    // TODO(leeca): How can this be active if there is no ViewModel
    if (!hasEditor()) {
      return;
    }
    getEditor().autoCollapse(finder, null);
  }

  /**
   * uncollapse operation.
   *
   * @param selectionIndex index selected in the uncollapse option list.
   * @param deleteGroup if true, delete the group after uncollapsing.
   */
  protected void uncollapseButton(int selectionIndex, boolean deleteGroup) {
    GraphNode master = collapseMaster.getElementAtIndex(
        masterViewer.getCombo().getSelectionIndex());
    getEditor().uncollapse(master, deleteGroup, null);
  }
  
  /**
   * Uncollapses <b>all</b> selected nodes.
   */
  protected void uncollapseAllSelected() {
    int selectionNumber = masterViewer.getCombo().getItemCount();
    for (int i = 0; i < selectionNumber; i++) {
      GraphNode master = collapseMaster.getElementAtIndex(i);
      getEditor().uncollapse(master, false, null);
    }
  }

  /**
   * Collapsing operation.
   *
   * @param erase if true, if there is an existing group with the same master,
   *        it will be erased. if not, the selected nodes will be added to the
   *        existing group.
   */
  protected void collapse(boolean erase) {
    GraphNode master = collapseMaster.getElementAtIndex(
        masterViewer.getCombo().getSelectionIndex());
    Collection<GraphNode> objects = collapseMaster.getObjects();
    getEditor().collapse(master, objects, erase, null);
  }

  @Override
  public void emptySelection() {
    if (null != collapseMaster) {
      collapseMaster.clear();
    }
    if (null != masterViewer) {
      masterViewer.refresh(false);
    }
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_COLLAPSE;
  }

  @Override
  public String getName() {
    return Resources.NAME_COLLAPSE;
  }

  @Override
  public void selectedSetChanged(RelationshipSet set) {
    // nothing to do here, use the RelationshipSet only when clicking a button.
  }

  /**
   * Add newly selected nodes to the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedAdd(GraphNode[] selection) {
    for (GraphNode node : selection) {
      collapseMaster.add(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }

  /**
   * Remove unselected nodes from the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedRemove(GraphNode[] selection) {
    for (GraphNode node : selection) {
      collapseMaster.remove(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }

  /**
   * Refresh the content of the collapseMaster combo with selected nodes.
   */
  @Override
  public void updateSelectionTo(GraphNode[] selection) {
    emptySelection();
    updateSelectedAdd(selection);
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }
}
