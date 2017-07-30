/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.trees;

import com.google.devtools.depan.collapse.model.CollapseTreeModel;
import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseDataWrapper;
import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseTreeRoot;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.trees.NodeWrapper;
import com.google.devtools.depan.eclipse.ui.nodes.trees.SolitaryRoot;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSaveLoadConfig;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphEdge;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.nodes.trees.TreeModel;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.trees.ActionableViewerObject.ActionSolitaryRoot;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * Handle the compaction of rendered nodes for a single {@link ViewEditor}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class NodeCompactor {

  private final ViewEditor editor;

  /**
   * The subset of {@link GraphNode}s (from {@link #viewGraph}) that are
   * currently exposed and therefore being rendered.  The {@link #exposedGraph}
   * is based on this collection of {@link GraphNode}s.
   */
  private Collection<GraphNode> exposedNodes;

  /**
   * The subset of {@link GraphNode}s and {@link GraphEdge}s
   * (from {@link #viewGraph}) that are currently exposed and therefore
   * being rendered.  The collapser is responsible for these transformations.
   */
  private GraphModel exposedGraph;

  private PlatformObject[] roots;

  public NodeCompactor(ViewEditor viewEditor) {
    editor = viewEditor;
  }

  public Collection<GraphNode> getExposedNodes() {
    return exposedNodes;
  }

  public GraphModel getExposedGraph() {
    return exposedGraph;
  }

  public void updateExposedNodes(Collection<GraphNode> nodes) {
    exposedNodes = buildExposedNodes(nodes);
    exposedGraph = buildExposedGraph(editor.getViewGraph(), exposedNodes);
  }

  /////////////////////////////////////
  //

  public PlatformObject[] buildRoots(Collection<GraphNode> nodes) {
    roots = buildHierarchyRoots(editor.getViewGraph(), nodes);
    return roots;
  }

  public Collection<GraphNode> buildExposedNodes(
      Collection<GraphNode> nodes) {
    Collection<GraphNode> result = Sets.newHashSet(nodes);
    CollapseTreeModel collapser = getCollapseTreeModel();
    result.removeAll(collapser.computeNodes());
    result.addAll(collapser.getMasterNodeSet());
    return result;
  }

  public GraphModel buildExposedGraph(
      GraphModel master, Collection<GraphNode> nodes) {
    return GraphBuilders.buildFromNodes(master, nodes);
  }

  @SuppressWarnings("unchecked")
  public Object findNodeObject(GraphNode node) {
    for (PlatformObject root : roots) {
      if (root instanceof SolitaryRoot<?>) {
        SolitaryRoot<GraphNode> check = (SolitaryRoot<GraphNode>) root;
        NodeWrapper<GraphNode> result =
            check.getGraphData().getNodeWrapper(node);
        return result;
      }
      if (root instanceof CollapseTreeRoot<?>) {
        CollapseTreeRoot<GraphNode> check = (CollapseTreeRoot<GraphNode>) root;
        CollapseDataWrapper<GraphNode> result =
            check.getCollapseNodeWrapper(node);
        return result;
      }
    }
    return null;
  }

  /////////////////////////////////////
  //

  private CollapseTreeModel getCollapseTreeModel() {
    return editor.getCollapseTreeModel();
  }

  private List<GraphEdgeMatcherDescriptor> getTreeDescriptors() {
    return editor.getTreeDescriptors();
  }

  private PlatformObject[] buildHierarchyRoots(
      GraphModel master, Collection<GraphNode> nodes) {
    List<PlatformObject> staging = Lists.newArrayList();

    List<GraphNode> remains = Lists.newArrayList(nodes);

    Collection<GraphNode> collapseNodes = getCollapseTreeModel().computeNodes();
    if (!collapseNodes.isEmpty()) {
      staging.add(buildCollapseRoot());
      remains.removeAll(collapseNodes);
    }

    for (GraphEdgeMatcherDescriptor matcher : getTreeDescriptors()) {
      staging.add(buildTreeRoot(master, remains, matcher));
    }

    staging.add(buildRemainsRoot(remains));
    PlatformObject[] result = new PlatformObject[staging.size()];
    return staging.toArray(result);
  }

  private PlatformObject buildCollapseRoot() {

    CollapseTreeModel treeModel = getCollapseTreeModel();
    int rootCnt = treeModel.computeRoots().size();
    int nodeCnt = treeModel.computeNodes().size();

    String label = MessageFormat.format(
        "Collapse nodes [{0} roots, {1} nodes]", rootCnt, nodeCnt);
    return CollapseTreeRoot.build(
        label, treeModel, NodeTreeProviders.GRAPH_NODE_PROVIDER);
  }

  /**
   * The argument for {@code remains} is updated on each call.
   */
  private PlatformObject buildTreeRoot(
      GraphModel master,
      Collection<GraphNode> remains,
      GraphEdgeMatcherDescriptor matcher) {

    // Build the hierarchy for this set of nodes and matcher
    GraphModel treeGraph = GraphBuilders.buildFromNodes(master, remains);
    GraphData<GraphNode> data = GraphData.createGraphData(
        NodeTreeProviders.GRAPH_NODE_PROVIDER,
        treeGraph, matcher.getInfo());

    // Update remains with info from hierarchy tree
    TreeModel tree = data.getTreeModel();
    remains.removeAll(tree.computeTreeNodes());

    // provide the root viewing object
    String label = MessageFormat.format("Tree of {0}", matcher.getName());
    return new ActionTreeRoot(data, label, matcher);
  }

  private PlatformObject buildRemainsRoot(Collection<GraphNode> nodes) {
    TreeModel.Flat model = new TreeModel.Flat(nodes);
    GraphData<GraphNode> data = new GraphData<GraphNode>(
        NodeTreeProviders.GRAPH_NODE_PROVIDER, model);
    String label = MessageFormat.format("Solitaires [{0} nodes]", nodes.size());
    return new ActionRemainsRoot(data, label);
  }

  /////////////////////////////////////
  // Customized actions for tree roots

  private static class ActionTreeRoot extends ActionSolitaryRoot {

    private final GraphEdgeMatcherDescriptor matcher;

    public ActionTreeRoot(
        GraphData<GraphNode> data, String label,
        GraphEdgeMatcherDescriptor matcher) {
      super(data, label);
      this.matcher = matcher;
    }

    @Override
    public void addMultiActions(IMenuManager manager, ViewEditor editor) {
    }

    @Override
    public void addItemActions(IMenuManager manager, final ViewEditor editor) {
      manager.add(new Action("Drop hierarchy..", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          editor.removeNodeTreeHierarchy(matcher);
        }
      });
      manager.add(new Action("Collapse hierarchy..", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          editor.collapseTreeHierarchy(getGraphData().getTreeModel());
        }
      });
    }
  }

  /////////////////////////////////////
  // Customized actions for tree roots

  private static class ActionRemainsRoot extends ActionSolitaryRoot {

    public ActionRemainsRoot(GraphData<GraphNode> nodes, String label) {
      super(nodes, label);
    }

    @Override
    public void addMultiActions(IMenuManager manager, ViewEditor editor) {
    }

    @Override
    public void addItemActions(IMenuManager manager, final ViewEditor editor) {
      manager.add(buildAddHierarchy(editor));
      manager.add(buildCollapseHierarchy(editor));
    }

    private Action buildAddHierarchy(final ViewEditor editor) {
      Action action = new Action("Add hierarchy..", IAction.AS_PUSH_BUTTON) {

        @Override
        public void run() {
          PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher =
              EdgeMatcherSaveLoadConfig.CONFIG.loadResource(
                  editor.getEditorSite().getShell(), editor.getResourceProject());

          if (null != matcher) {
            editor.addNodeTreeHierarchy(matcher.getDocument());
           }
        }
      };

      return action;
    }

    private Action buildCollapseHierarchy(final ViewEditor editor) {
      final Collection<GraphNode> nodes =
          getGraphData().getTreeModel().computeRoots();

      Action action = new Action("Collapse hierarchy..", IAction.AS_PUSH_BUTTON) {

        @Override
        public void run() {
          PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher =
              EdgeMatcherSaveLoadConfig.CONFIG.loadResource(
                  editor.getEditorSite().getShell(), editor.getResourceProject());

          if (null != matcher) {
             editor.collapseNodesByHierarchy(nodes, matcher.getDocument());
           }
        }
      };

      return action;
    }
  }
}
