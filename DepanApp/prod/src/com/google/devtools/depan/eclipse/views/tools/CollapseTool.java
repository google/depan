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

import com.google.devtools.depan.eclipse.utils.DefaultRelationshipSet;
import com.google.devtools.depan.eclipse.utils.LabeledControl;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.RelationshipSetPickerControl;
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

  @Override
  public Control setupComposite(Composite parent) {
    // first expand bar containing collapsing operations
    Composite topComposite = new Composite(parent, SWT.NONE);

    GridLayout topGrid = new GridLayout();
    topGrid.verticalSpacing = 9;
    topComposite.setLayout(topGrid);

    // Setup the manual collapse controls
    setupManualCollapseGroup(topComposite);
    setupAutoCollapseGroup(topComposite);

    // content
    collapseMaster = new TableContentProvider<GraphNode>();
    collapseMaster.initViewer(masterViewer);

    //FIXME(yc): select first set
    //namedSet.selectSet(BuiltinRelationshipSets.CONTAINER);

    return topComposite;
  }

  private void setupManualCollapseGroup(Composite parent) {
    Group manualCollapse = new Group(parent, SWT.BORDER);
    manualCollapse.setText("Manual collapsing");
    manualCollapse.setLayoutData(
      new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout manualGrid = new GridLayout(2, true);
    manualGrid.marginWidth = 10;
    manualGrid.marginHeight = 10;
    manualCollapse.setLayout(manualGrid);

    Label labelCollapse = createLabel(manualCollapse, "Collapse under");

    masterViewer = new ComboViewer(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    masterViewer.getCombo().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 2));

    Button eraseCollapse = createPushButton(manualCollapse, "collapse / erase");
    Button collapseButton = createPushButton(manualCollapse, "collapse / add");

    Label labelUncollapse = createLabel(manualCollapse, "Uncollapse");

    final CCombo uncollapseOpts =
        new CCombo(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    for (String s : uncollapseOptions) {
      uncollapseOpts.add(s);
    }
    uncollapseOpts.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Button deleteCollapse = createPushButton(manualCollapse, "uncollapse / Delete");
    Button uncollapseButton = createPushButton(manualCollapse, "uncollapse");
    Button uncollapseAll = createPushButton(manualCollapse, "Uncollapse All Selected");

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
  }

  private void setupAutoCollapseGroup(Composite parent) {
    Group autoCollapse = new Group(parent, SWT.BORDER);
    autoCollapse.setText("Automatic collapsing based on a relationship set");
    autoCollapse.setLayoutData(
      new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout autoGrid = new GridLayout(3, false);
    autoGrid.marginWidth = 10;
    autoGrid.marginHeight = 10;
    autoCollapse.setLayout(autoGrid);

    Label pickerLabel = RelationshipSetPickerControl.createPickerLabel(autoCollapse);
    pickerLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    final RelationshipSetPickerControl autoSetPicker =
        new RelationshipSetPickerControl(autoCollapse);
    autoSetPicker.selectSet(DefaultRelationshipSet.SET);
    autoSetPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));

    Button doAutoGrouping = createPushButton(autoCollapse, "Collapse");

    // actions
    doAutoGrouping.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        autoCollapse(autoSetPicker.getSelection());
      }
    });
  }

  private Button createPushButton(Composite parent, String text) {
    Button result = new Button(parent, SWT.PUSH);
    result.setText(text);
    result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    return result;
  }

  private Label createLabel(Composite parent, String text) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(text);
    result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
    return result;
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
