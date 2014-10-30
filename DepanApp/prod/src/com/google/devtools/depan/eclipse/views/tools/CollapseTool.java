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

import com.google.devtools.depan.eclipse.editors.HierarchyCache;
import com.google.devtools.depan.eclipse.editors.ViewPrefsListener;
import com.google.devtools.depan.eclipse.trees.GraphData;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeProvider;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeView.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.trees.collapse_tree.CollapseTreeWrapperSorter;
import com.google.devtools.depan.eclipse.utils.HierarchyViewer;
import com.google.devtools.depan.eclipse.utils.RelationshipSelectorListener;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.view.CollapseData;
import com.google.devtools.depan.view.CollapseTreeModel;
import com.google.devtools.depan.view.NodeDisplayProperty;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.util.Collection;
import java.util.List;

/**
 * Collapse Tool. Provide a GUI to collapse and uncollapse nodes under another
 * node.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class CollapseTool extends ViewSelectionListenerTool
    implements RelationshipSelectorListener {
  
  private static final CollapseTreeWrapperSorter<NodeDisplayProperty> SORTER =
      new CollapseTreeWrapperSorter<NodeDisplayProperty>();

  private CollapseTreeProvider<NodeDisplayProperty> provider =
      new CollapseTreeProvider<NodeDisplayProperty>() {

    @Override
    public NodeDisplayProperty getObject(CollapseData collapseData) {
      if (hasEditor()) {
        return null;
      }
      GraphNode node = collapseData.getMasterNode();
      return getEditor().getNodeProperty(node);
    }
  };

  /**
   * Content provider for selected nodes. Fill the list of selected nodes used
   * to choose the master node for a new collapse group.
   */
  private TableContentProvider<GraphNode> collapseMaster = null;

  private ComboViewer masterViewer;

  /** Provides hierarchy to use for autoCollapse operations. */
  private HierarchyViewer<NodeDisplayProperty> autoHierarchyPicker;

  /** Provides a tree view of the collapseData. */
  private CollapseTreeView<NodeDisplayProperty> collapseView = null;

  private ViewPrefsListener prefsListener;

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
    setupCollapseHierarchy(topComposite);

    // content
    collapseMaster = new TableContentProvider<GraphNode>();
    collapseMaster.initViewer(masterViewer);

    //FIXME(yc): select first set
    //namedSet.selectSet(BuiltinRelationshipSets.CONTAINER);

    return topComposite;
  }

  private Composite setupCollapseHierarchy(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    baseComposite.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    baseComposite.setLayout(new GridLayout(1, false));

    Composite optionsSection = new Composite(baseComposite, SWT.NONE);
    optionsSection.setLayout(new GridLayout(1, false));
    optionsSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    ToolBar rightOptions = new ToolBar(optionsSection, SWT.NONE | SWT.FLAT | SWT.RIGHT);
    rightOptions.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));

    ToolItem collapseButton = setupCollapseAllPushIcon(rightOptions);
    collapseButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapseView.collapseAll();
      }
    });

    ToolItem expandButton = setupExpandAllPushIcon(rightOptions);
    expandButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        collapseView.expandAll();
      }
    });

    collapseView = CollapseTreeView.buildCollapseTreeView(
        baseComposite,
        SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER
        | SWT.H_SCROLL | SWT.V_SCROLL,
        provider);

    TreeViewer viewer = collapseView.getTreeViewer();
    setupHierarchyMenu(viewer);
    viewer.setSorter(SORTER);
    viewer.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    return baseComposite;
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

        if (selection instanceof IStructuredSelection) {
          IStructuredSelection items = (IStructuredSelection) selection;
          @SuppressWarnings("unchecked")
          final CollapseDataWrapper<NodeDisplayProperty> data =
              (CollapseDataWrapper<NodeDisplayProperty>) items.getFirstElement();

          if (null != data.getParent()) {
            return;
          }

          manager.add(new Action("uncollapse", IAction.AS_PUSH_BUTTON) {
            @Override
            public void run() {
              getEditor().uncollapse(
                  data.getCollapseData().getMasterNode(), null);
            }
          });
        }
      }
    });
    menuMgr.setRemoveAllWhenShown(true);
    viewer.getControl().setMenu(menu);
  }

  private void setupManualCollapseGroup(Composite parent) {
    Group manualCollapse = new Group(parent, SWT.NONE);
    manualCollapse.setText("Manual collapsing");
    manualCollapse.setLayoutData(
      new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout manualGrid = new GridLayout(2, true);
    manualGrid.marginWidth = 10;
    manualGrid.marginHeight = 10;
    manualCollapse.setLayout(manualGrid);

    Label collapseLabel = setupLabel(manualCollapse, "Collapse under");
    collapseLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

    masterViewer = new ComboViewer(manualCollapse, SWT.READ_ONLY | SWT.FLAT);
    masterViewer.getCombo().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Button eraseCollapse = setupPushButton(manualCollapse, "collapse / erase");
    eraseCollapse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button collapseButton = setupPushButton(manualCollapse, "collapse / add");
    collapseButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    Button uncollapseAll = setupPushButton(manualCollapse, "Uncollapse All Selected");
    uncollapseAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

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
    uncollapseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        uncollapseAllSelected();
      }
    });
  }

  private void setupAutoCollapseGroup(Composite parent) {
    Group autoCollapse = new Group(parent, SWT.NONE);
    autoCollapse.setText("Automatic collapsing based on a relation set");

    autoCollapse.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    GridLayout autoGrid = new GridLayout(2, false);
    autoGrid.marginWidth = 10;
    autoGrid.marginHeight = 10;
    autoCollapse.setLayout(autoGrid);

    autoHierarchyPicker =
        new HierarchyViewer<NodeDisplayProperty>(autoCollapse, false);
    autoHierarchyPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));
    Button doAutoGrouping = setupPushButton(autoCollapse, "Collapse");
    doAutoGrouping.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

    // actions
    doAutoGrouping.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        autoCollapse();
      }
    });
  }

  private Label setupLabel(Group manualCollapse, String text) {
    Label result = new Label(manualCollapse, SWT.NONE);
    result.setText(text);
    return result;
  }

  private Button setupPushButton(Composite parent, String text) {
    Button result = new Button(parent, SWT.PUSH);
    result.setText(text);
    return result;
  }


  private ToolItem setupCollapseAllPushIcon(ToolBar parent) {
    ToolItem result = new ToolItem(parent, SWT.PUSH | SWT.FLAT);
    Image icon = PlatformUI.getWorkbench().getSharedImages().getImage(
        ISharedImages.IMG_ELCL_COLLAPSEALL);
    result.setImage(icon);
    return result;
  }

  private ToolItem setupExpandAllPushIcon(ToolBar parent) {
    ToolItem result = new ToolItem(parent, SWT.PUSH | SWT.FLAT);
    result.setImage(Resources.IMAGE_EXPANDALL);
    return result;
  }

  /////////////////////////////////////
  // Tool life-cycle methods

  @Override
  protected void acquireResources() {
    super.acquireResources();

    updateCollapseView();

    prefsListener = new ViewPrefsListener.Simple()  {

      @Override
      public void collapseChanged(
          Collection<CollapseData> created,
          Collection<CollapseData> removed, Object author) {
        updateCollapseView();
      }
    };
    getEditor().addViewPrefsListener(prefsListener);
  }

  @Override
  protected void releaseResources() {
    if (hasEditor()) {
      getEditor().removeSelectionChangeListener(this);
      if (null != prefsListener) {
        getEditor().removeViewPrefsListener(prefsListener);
      }
    }
    super.releaseResources();
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // Update the RelSet picker for auto-collapse.
    HierarchyCache<NodeDisplayProperty> hierarchies = getEditor().getHierarchies();
    RelationshipSet selectedRelSet = getEditor().getContainerRelSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    autoHierarchyPicker.setInput(hierarchies, selectedRelSet, choices);

    updateCollapseView();
  }

  /**
   * Autocollapse the current graph.
   * Redraws are triggered by event propagation.
   *
   * @param finder
   */
  private void autoCollapse() {
    // TODO(leeca): How can this be active if there is no ViewModel
    if (!hasEditor()) {
      return;
    }

    GraphData<NodeDisplayProperty> graphData =
        autoHierarchyPicker.getGraphData();
    getEditor().collapseTree(graphData.getTreeModel(), null);
  }

  /**
   * Uncollapses <b>all</b> selected nodes.
   */
  private void uncollapseAllSelected() {
    int selectionNumber = masterViewer.getCombo().getItemCount();
    for (int i = 0; i < selectionNumber; i++) {
      GraphNode master = collapseMaster.getElementAtIndex(i);
      getEditor().uncollapse(master, null);
    }
  }

  /**
   * Collapsing operation.
   *
   * @param erase if true, if there is an existing group with the same master,
   *        it will be erased. if not, the selected nodes will be added to the
   *        existing group.
   */
  private void collapse(boolean erase) {
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
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    for (GraphNode node : extension) {
      collapseMaster.add(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }

  /**
   * Remove unselected nodes from the content of the collapseMaster combo.
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    for (GraphNode node : reduction) {
      collapseMaster.remove(node);
    }
    masterViewer.refresh(false);
    masterViewer.getCombo().select(0);
  }

  /**
   * Refresh the content of the collapseMaster combo with selected nodes.
   */
  @Override
  public void updateSelectionTo(Collection<GraphNode> selection) {
    emptySelection();
    updateSelectedExtend(selection);
  }

  private void updateCollapseView() {
    if (!hasEditor()) {
      return;
    }

    CollapseTreeModel collapseTreeModel = getEditor().getCollapseTreeModel();
    collapseView.updateData(collapseTreeModel);
  }
}
