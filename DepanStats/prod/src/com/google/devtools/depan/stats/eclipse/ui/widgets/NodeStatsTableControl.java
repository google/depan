/*
 * Copyright 2014 The Depan Project Authors
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

package com.google.devtools.depan.stats.eclipse.ui.widgets;

import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.GraphNodeViewer;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.InverseSorter;
import com.google.devtools.depan.platform.LabelProviderToString;
import com.google.devtools.depan.platform.PlatformResources;
import com.google.devtools.depan.platform.eclipse.ui.tables.EditColTableDef;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.stats.jung.JungStatistics;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.model.IWorkbenchAdapter;

import java.text.DecimalFormat;

/**
 * Show a table of the nodes, with their attributes.  The attributes
 * include node visibility and {@link NodeDisplayProperty}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeStatsTableControl extends Composite {

  public static final String COL_NAME = "Node";
  protected static final String COL_ROOT = "Root";
  protected static final String COL_RANK = "Rank";
  protected static final String COL_PREDS = "Predecessors";
  protected static final String COL_SUCCS = "Successors";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_ROOT = 1;
  public static final int INDEX_RANK = 2;
  public static final int INDEX_PREDS = 3;
  public static final int INDEX_SUCCS = 4;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 300),
    new EditColTableDef(COL_ROOT, false, COL_ROOT, 50),
    new EditColTableDef(COL_RANK, false, COL_RANK, 50),
    new EditColTableDef(COL_PREDS, false, COL_PREDS, 100),
    new EditColTableDef(COL_SUCCS, false, COL_SUCCS, 100)
  };

  private static final String NO_RANK = "no rank";
  private static final String NO_PREDS = "no preds";
  private static final String NO_SUCCS = "no succs";

  private class ControlGraphNodeViewer extends GraphNodeViewer {

    public ControlGraphNodeViewer(Composite parent) {
      super(parent);
    }

    @Override
    protected TreeViewer createTreeViewer(Composite parent) {
      TreeViewer result = super.createTreeViewer(parent);

      // Initialize the table.
      Tree tree = result.getTree();
      tree.setHeaderVisible(true);
      tree.setToolTipText("Node Display Properties");
      EditColTableDef.setupTree(TABLE_DEF, tree);

      result.setLabelProvider(new PartLabelProvider());
      result.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));

      return result;
    }

    public Tree getTree() {
      return (Tree) getTreeViewer().getControl();
    }

    public ITableLabelProvider getLabelProvider() {
      return (ITableLabelProvider) getTreeViewer().getLabelProvider();
    }

    public void setComparator(ViewerComparator sorter) {
      getTreeViewer().setComparator(sorter);
    }
  }

  /////////////////////////////////////
  // Table data

  private JungStatistics stats;

  /////////////////////////////////////
  // UX Elements

  private final ControlGraphNodeViewer propViewer;

  /////////////////////////////////////
  // Public methods

  public NodeStatsTableControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    propViewer = new ControlGraphNodeViewer(this);
    propViewer.setLayoutData(Widgets.buildGrabFillData());
    configSorters(propViewer.getTree());
  }

  public void setInput(NodeViewerProvider provider) {
    propViewer.setNvProvider(provider);
    propViewer.refresh();
  }

  public void setJungStatistics(JungStatistics stats) {
    this.stats = stats;
  }

  public void updateJungStatistics(JungStatistics stats) {
    setJungStatistics(stats);
    propViewer.refresh();
  }

  /////////////////////////////////////
  // Statistics extraction

  private boolean isRoot(GraphNode node) {
    if (null != stats) {
      return stats.isRoot(node);
    }

    return false;
  }

  private String getRank(GraphNode node) {
    if (null != stats) {
      Double result = stats.getRank(node);
      return new DecimalFormat("#.####").format(result);
    }

    return NO_RANK;
  }

  private String getPredecessorCount(GraphNode node) {
    if (null != stats) {
      int result = stats.getPredecessorCount(node);
      return Integer.toString(result);
    }

    return NO_PREDS;
  }

  private String getSuccessorCount(GraphNode node) {
    if (null != stats) {
      int result = stats.getSuccessorCount(node);
      return Integer.toString(result);
    }

    return NO_SUCCS;
  }

  /////////////////////////////////////
  // Column sorting

  private void configSorters(Tree tree) {
    int index = 0;
    for (TreeColumn column : tree.getColumns()) {
      final int colIndex = index++;

      column.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent event) {
          updateSortColumn((TreeColumn) event.widget, colIndex);
        }
      });
    }
  }

  private void updateSortColumn(TreeColumn column, int colIndex) {
    setSortColumn(column, colIndex, getSortDirection(column));
  }

  private int getSortDirection(TreeColumn column) {
    Tree tree = propViewer.getTree();
    if (column != tree.getSortColumn()) {
      return SWT.DOWN;
    }
    // If it is unsorted (SWT.NONE), assume down sort
    return (SWT.DOWN == tree.getSortDirection())
        ? SWT.UP : SWT.DOWN;
  }

  private void setSortColumn(
      TreeColumn column, int colIndex, int direction) {

    ViewerComparator sorter = buildColumnSorter(colIndex);
    if (SWT.UP == direction) {
      sorter = new InverseSorter(sorter);
    }

    Tree tree = propViewer.getTree();
    tree.setSortColumn(column);
    tree.setSortDirection(direction);

    propViewer.setComparator(sorter);
  }

  private ViewerComparator buildColumnSorter(int columnIndex) {
    switch (columnIndex) {
    case INDEX_ROOT:
      return new RootSorter();
    case INDEX_RANK:
      return new RankSorter();
    case INDEX_PREDS:
      return new PredecessorCountSorter();
    case INDEX_SUCCS:
      return new SuccessorCountSorter();
    }

    // By default, use an alphabetic sort over the column labels.
    ITableLabelProvider labelProvider =
        (ITableLabelProvider) propViewer.getLabelProvider();
    ViewerComparator result = new AlphabeticSorter(
        new LabelProviderToString(labelProvider, columnIndex));
    return result;
  }

  @SuppressWarnings("unchecked")
  private GraphNode getGraphNode(Object element) {
    if (element instanceof NodeWrapper<?>) {
      NodeWrapper<GraphNode> wrap = (NodeWrapper<GraphNode>) element;
      return wrap.getNode();
    }
    if (element instanceof CollapseDataWrapper<?>) {
      CollapseDataWrapper<GraphNode> wrap = (CollapseDataWrapper<GraphNode>) element;
      return wrap.getCollapseData().getMasterNode();
    }
    return null;
  }

  private IWorkbenchAdapter getWorkbenchAdapter(Object element) {
    if (element instanceof IAdaptable ) {
      Object result = ((IAdaptable) element).getAdapter(IWorkbenchAdapter.class);
      return (IWorkbenchAdapter) result;
    }
    return null;
  }

  private class RootSorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      boolean vis1 = isRoot(e1);
      boolean vis2 = isRoot(e2);
      return Boolean.compare(vis1, vis2);
    }

    private boolean isRoot(Object item) {
      GraphNode node = getGraphNode(item);
      if (null != node) {
        return NodeStatsTableControl.this.isRoot(node);
      }
      return false;
    }
  }

  private class RankSorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      return getRank(e1).compareTo(getRank(e2));
    }

    private Double getRank(Object item) {
      JungStatistics stats = NodeStatsTableControl.this.stats;
      if (null != stats) {
        GraphNode node = getGraphNode(item);
        return stats.getRank(node);
      }
      return null;
    }
  }

  private class PredecessorCountSorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      return Integer.compare(getPredecessorCount(e1), getPredecessorCount(e2));
    }

    private int getPredecessorCount(Object item) {
      JungStatistics stats = NodeStatsTableControl.this.stats;
      if (null != stats) {
        GraphNode node = getGraphNode(item);
        return stats.getPredecessorCount(node);
      }
      return 0;
    }
  }

  private class SuccessorCountSorter extends ViewerComparator {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
      return Integer.compare(getSuccessorCount(e1), getSuccessorCount(e2));
    }

    private int getSuccessorCount(Object item) {
      JungStatistics stats = NodeStatsTableControl.this.stats;
      if (null != stats) {
        GraphNode node = getGraphNode(item);
        return stats.getSuccessorCount(node);
      }
      return 0;
    }
  }

  /////////////////////////////////////
  // Label provider for table cell text

  private class PartLabelProvider extends LabelProvider
      implements ITableLabelProvider {

    @Override
    public String getColumnText(Object element, int columnIndex) {
      GraphNode node = getGraphNode(element);
      if (null != node) {
        switch (columnIndex) {
        case INDEX_NAME:
          return node.toString();
        case INDEX_RANK:
          return getRank(node);
        case INDEX_PREDS:
          return getPredecessorCount(node);
        case INDEX_SUCCS:
          return getSuccessorCount(node);
        }
      }
      IWorkbenchAdapter item = getWorkbenchAdapter(element);
      if (null != item) {
        switch (columnIndex) {
        case INDEX_NAME:
          return item.getLabel(element);
        }
      }
      return null;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      GraphNode node = getGraphNode(element);
      if (null != node) {
        switch (columnIndex) {
        case INDEX_ROOT:
          return PlatformResources.getOnOff(isRoot(node));
        }
      }

      IWorkbenchAdapter item = getWorkbenchAdapter(element);
      if (null != item) {
        switch (columnIndex) {
        case INDEX_NAME:
          return null; // TODO: versus - item.getImageDescriptor(element)...
        }
      }
      // Fall through and unknown type
      return null;
    }
  }
}
