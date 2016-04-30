/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.devtools.depan.eclipse.editors.NodeDisplayProperty;
import com.google.devtools.depan.eclipse.editors.NodeDisplayProperty.Size;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.ui.nodes.cache.HierarchyCache;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapperTreeSorter;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeEditorLabelProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProvider;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeView;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.HierarchyViewer.HierarchyChangeListener;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.tables.EditColTableDef;
import com.google.devtools.depan.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

/**
 * Tool for Node edition. Associate to each node a {@link NodeDisplayProperty}
 * and provide a GUI to edit them.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NodeEditorTool extends ViewSelectionListenerTool
    implements ICellModifier, NodeTreeProvider<NodeDisplayProperty> {

  /**
   * Node Tree View handling the TreeViewer, and the data inside.
   */
  private NodeTreeView<NodeDisplayProperty> nodeTreeView = null;

  /**
   * Selector for named relationships sets.
   */
  private HierarchyViewer<NodeDisplayProperty> hierarchyPicker = null;

  protected static final String COL_NAME = "Name";
  protected static final String COL_XPOS = "X";
  protected static final String COL_YPOS = "Y";
  protected static final String COL_VISIBLE = "Visible";
  protected static final String COL_SELECTED = "Selected";
  protected static final String COL_SIZE = "Size";
  protected static final String COL_COLOR = "Color (R,G,B - empty = default)";

  public static final int INDEX_NAME = 0;
  public static final int INDEX_XPOS = 1;
  public static final int INDEX_YPOS = 2;
  public static final int INDEX_VISIBLE = 3;
  public static final int INDEX_SELECTED = 4;
  public static final int INDEX_SIZE = 5;
  public static final int INDEX_COLOR = 6;

  private static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 180),
    new EditColTableDef(COL_XPOS, false, COL_XPOS, 50),
    new EditColTableDef(COL_YPOS, false, COL_YPOS, 50),
    new EditColTableDef(COL_VISIBLE, true, COL_VISIBLE, 20),
    new EditColTableDef(COL_SELECTED, true, COL_SELECTED, 20),
    new EditColTableDef(COL_SIZE, true, COL_SIZE, 80),
    new EditColTableDef(COL_COLOR, true, COL_COLOR, 40)
  };

  @Override
  public Image getIcon() {
    return Resources.IMAGE_NODEEDITOR;
  }

  @Override
  public String getName() {
    return Resources.NAME_NODEEDITOR;
  }

  @Override
  public Control setupComposite(Composite parent) {
    Composite baseComposite = new Composite(parent, SWT.NONE);
    GridLayout grid = new GridLayout(1, false);
    baseComposite.setLayout(grid);

    hierarchyPicker = createHierarchyPicker(baseComposite);
    hierarchyPicker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    nodeTreeView = new NodeTreeView<NodeDisplayProperty>(baseComposite,
        SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);
    nodeTreeView.getTreeViewer().getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    Tree tree = nodeTreeView.getTreeViewer().getTree();
    tree.setHeaderVisible(true);

    EditColTableDef.setupTree(TABLE_DEF, tree);

    CellEditor[] cellA = new CellEditor[TABLE_DEF.length];
    cellA[INDEX_NAME] = null;
    cellA[INDEX_XPOS] = null;
    cellA[INDEX_YPOS] = null;
    cellA[INDEX_VISIBLE] = new CheckboxCellEditor(tree);
    cellA[INDEX_SELECTED] = new CheckboxCellEditor(tree);
    cellA[INDEX_SIZE] = new ComboBoxCellEditor(tree,
        Tools.toString(Size.values(), true));
    cellA[INDEX_COLOR] = new TextCellEditor(tree);

    nodeTreeView.getTreeViewer().setCellEditors(cellA);
    nodeTreeView.getTreeViewer().setColumnProperties(
        EditColTableDef.getProperties(TABLE_DEF));

    // (re) set the label provider, for one which can handle multiple
    // columns.
    nodeTreeView.getTreeViewer().setLabelProvider(
        new NodeEditorLabelProvider(this));

    // set a cell modifier
    nodeTreeView.getTreeViewer().setCellModifier(this);
    nodeTreeView.getTreeViewer().setSorter(new NodeWrapperTreeSorter());

    return baseComposite;
  }

  private HierarchyViewer<NodeDisplayProperty> createHierarchyPicker(
      Composite parent) {

    HierarchyViewer<NodeDisplayProperty> result =
        new HierarchyViewer<NodeDisplayProperty>(parent, false);
    result.addChangeListener(new HierarchyChangeListener() {

      @Override
      public void hierarchyChanged() {
        handleHierarchyChanged();
      }
    });

    return result;
  }

  /////////////////////////////////////
  // Tool life-cycle methods

  @Override
  protected void acquireResources() {
    super.acquireResources();

    GraphData<NodeDisplayProperty> hierarchy = getDisplayHierarchy();
    nodeTreeView.updateData(hierarchy);
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // Update the hierarchy picker for the new editor.
    HierarchyCache<NodeDisplayProperty> hierarchies =
        getEditor().getHierarchies();
    GraphEdgeMatcherDescriptor edgeMatcher = getEditor().getTreeEdgeMatcher();
    List<GraphEdgeMatcherDescriptor> choices =
        getEditor().getTreeEdgeMatcherChoices();
    hierarchyPicker.setInput(hierarchies, edgeMatcher, choices);
  }

  @Override
  public void editorClosed(ViewEditor viewEditor) {
    if (hasEditor()) {
      GraphData<NodeDisplayProperty> hierarchy = getDisplayHierarchy();
      hierarchy.saveExpandState(
          nodeTreeView.getTreeViewer().getExpandedTreePaths());
    }
    super.editorClosed(viewEditor);
  }

  @Override
  public void emptySelection() {
    if (!hasEditor()) {
      return;
    }

    GraphData<NodeDisplayProperty> hierarchy = getDisplayHierarchy();
    nodeTreeView.updateData(hierarchy);
  }

  private GraphData<NodeDisplayProperty> getDisplayHierarchy() {
    return hierarchyPicker.getGraphData();
  }

  /////////////////////////////////////
  // Node selection methods

  @Override
  public boolean canModify(Object element, String property) {
    return EditColTableDef.get(TABLE_DEF, property).isEditable();
  }

  @Override
  @SuppressWarnings("rawtypes") // NodeWrapper
  public Object getValue(Object element, String property) {
    if (element instanceof NodeWrapper) {
      NodeWrapper wrapper = (NodeWrapper) element;
      if (COL_XPOS.equals(property)) {
        return getEditor().getXPos(wrapper.getNode());
      }
      if (COL_XPOS.equals(property)) {
        return getEditor().getYPos(wrapper.getNode());
      }
      if (property.equals(COL_SELECTED)) {
        return getEditor().isSelected(wrapper.getNode());
      } 

      NodeDisplayProperty p = getProp(wrapper);
      if (property.equals(COL_VISIBLE)) {
        return p.isVisible();
      }
      if (property.equals(COL_SIZE)) {
        return p.getSize().ordinal();
      }
      if (property.equals(COL_COLOR)) {
        return p.getColor() == null ? "" : p.getColor().toString();
      }
    }
    return null;
  }

  /**
   * return the {@link NodeDisplayProperty} associated with the given element.
   *
   * @param element
   * @return the property associated with the given {@link NodeWrapper}.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private NodeDisplayProperty getProp(NodeWrapper element) {
    return ((NodeWrapper<NodeDisplayProperty>) element).getContent();
  }

  /**
   * Change the element property to the given value.
   */
  // suppressWarning: we cast an Object to NodeWrapper, the parameter type is
  // unchecked.
  @Override
  @SuppressWarnings("unchecked")
  public void modify(Object element, String property, Object value) {
    if (!(element instanceof TreeItem)) {
      return;
    }
    Object o = ((TreeItem) element).getData();
    if (!(o instanceof NodeWrapper)) {
      return;
    }
    NodeWrapper<NodeDisplayProperty> prop =
        (NodeWrapper<NodeDisplayProperty>) o;
    NodeDisplayProperty p = prop.getContent();
    GraphNode node = prop.getNode();

//    System.out.println("" + p + " " + property + " - " + value);
    if (property.equals(COL_VISIBLE) && (value instanceof Boolean)) {
      p.setVisible((Boolean) value);
    } else if (property.equals(COL_SELECTED) && (value instanceof Boolean)) {
      selectNode(node, (Boolean) value);
    } else if (property.equals(COL_SIZE) && (value instanceof Integer)) {
      p.setSize(Size.values()[(Integer) value]);
    } else if (property.equals(COL_COLOR) && (value instanceof String)) {
      Color newColor = StringUtils.stringToColor((String) value);
      p.setColor(newColor);
    }
    // notify the listeners about this change
    getEditor().setNodeProperty(node, p);
    // update the column / line we just modified
    nodeTreeView.getTreeViewer().update(o, new String[] {property});
  }

  private void selectNode(GraphNode node, boolean extend) {
    Collection<GraphNode> selectGroup = Lists.newArrayList(node);
    if (extend) {
      getEditor().extendSelection(selectGroup, this);
      return;
    }
    getEditor().reduceSelection(selectGroup, this);
  }

  @Override
  public NodeDisplayProperty getObject(GraphNode node) {
    return getEditor().getNodeProperty(node);
  }

  /**
   * Change the selection state of the given node to the new value, in the list
   * of nodes properties.
   * @param node the node to change.
   * @param value the selection value.
   */
  private void setSelectedState(GraphNode node, boolean value) {
    NodeDisplayProperty prop = getEditor().getNodeProperty(node);
    if (null == prop) {
      return;
    }

    NodeWrapper<NodeDisplayProperty> nodeWrapper =
        nodeTreeView.getNodeWrapper(node);
    if (null == nodeWrapper) {
      return;
    }

    // update the value in the table. this might be faster than updating
    // all the list at the end. because a selection generally doesn't
    // change a lot.
    nodeTreeView.getTreeViewer().update(
        nodeWrapper, new String[] { COL_SELECTED });
  }

  /**
   * Set the given set of nodes as selected in the list.
   * @param selection set of nodes
   */
  @Override
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    for (GraphNode node : extension) {
      setSelectedState(node, true);
    }
  }

  /**
   * Set the given set of nodes as unselected in the list.
   *
   * @param selection set of nodes
   * @see com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool
   *      #updateSelectedRemove(com.google.devtools.depan.model.GraphNode[])
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    for (GraphNode node : reduction) {
      setSelectedState(node, false);
    }
  }

  /**
   * Set the nodes in the given set as the only ones selected. Which means
   * that the previously selected are first unselected, then nodes in the
   * <code>selection</code> argument are added.
   */
  @Override
  public void updateSelectionTo(Collection<GraphNode> selection) {
    for (GraphNode node : getEditor().getViewGraph().getNodes()) {
      NodeWrapper<NodeDisplayProperty> nodeWrapper =
          nodeTreeView.getNodeWrapper(node);
      if (null != nodeWrapper) {
        // update the value in the table. this might be faster than updating
        // all the list at the end. because a selection generally doesn't
        // change a lot.
        nodeTreeView.getTreeViewer().update(
            nodeWrapper, new String[] { COL_SELECTED });
      }
    }
    updateSelectedExtend(selection);
  }

  private void handleHierarchyChanged() {
    if (!hasEditor()) {
      return;
    }

    GraphData<NodeDisplayProperty> hierarchy = getDisplayHierarchy();
    nodeTreeView.updateData(hierarchy);
  }
}
