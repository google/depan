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
import com.google.devtools.depan.collapse.model.Collapser;
import com.google.devtools.depan.eclipse.ui.collapse.trees.CollapseTreeRoot;
import com.google.devtools.depan.eclipse.ui.nodes.trees.GraphData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeTreeProviders;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptors;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilders;
import com.google.devtools.depan.nodes.trees.TreeModel;
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

  private Collapser collapser = new Collapser();

  private List<GraphEdgeMatcherDescriptor> treeMatchers = Lists.newArrayList();

  public NodeCompactor(ViewEditor viewEditor) {
    editor = viewEditor;
  }

  /////////////////////////////////////
  // Mutators

  /**
   * @param matcher
   */
  public void addNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    treeMatchers.add(matcher);
  }

  /**
   * @param matcher
   */
  public void removeNodeTreeHierarchy(GraphEdgeMatcherDescriptor matcher) {
    treeMatchers.remove(matcher);
  }

  /////////////////////////////////////
  //

  public PlatformObject[] buildRoots(Collection<GraphNode> nodes) {
    return buildHierarchyRoots(editor.getViewGraph(), nodes);
  }

  public Collection<GraphNode> buildExposedNodes(
      Collection<GraphNode> nodes) {
    Collection<GraphNode> result = Sets.newHashSet(nodes);
    result.removeAll(collapser.computeNodes());
    result.addAll(collapser.getMasterNodeSet());
    return result;
  }

  public GraphModel buildExposedGraph(
      GraphModel master, Collection<GraphNode> nodes) {
    return GraphBuilders.buildFromNodes(master, nodes);
  }

  private PlatformObject[] buildHierarchyRoots(
      GraphModel master, Collection<GraphNode> nodes) {
    List<PlatformObject> staging = Lists.newArrayList();

    List<GraphNode> remains = Lists.newArrayList(nodes);

    Collection<GraphNode> collapseNodes = collapser.computeNodes();
    if (!collapseNodes.isEmpty()) {
      staging.add(buildCollapseRoot());
      remains.removeAll(collapseNodes);
    }

    for (GraphEdgeMatcherDescriptor matcher : treeMatchers) {
      staging.add(buildTreeRoot(master, remains, matcher));
    }

    staging.add(buildRemainsRoot(remains));
    PlatformObject[] result = new PlatformObject[staging.size()];
    return staging.toArray(result);
  }

  private PlatformObject buildCollapseRoot() {
    String label = MessageFormat.format(
        "{0} Collapse nodes", editor.getPartName());
    CollapseTreeModel tree = new CollapseTreeModel(collapser);
    return CollapseTreeRoot.build(
        tree, NodeTreeProviders.GRAPH_NODE_PROVIDER, label);
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
        treeGraph, matcher.getEdgeMatcher());

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
    String label = MessageFormat.format("Solitares [{0} nodes]", nodes.size());
    return new ActionRemainsRoot(data, label);
  }

  /////////////////////////////////////
  // Customize actions for tree roots

  private static class ActionTreeRoot extends ActionSolitaryRoot {

    private final GraphEdgeMatcherDescriptor matcher;

    public ActionTreeRoot(
        GraphData<GraphNode> nodes, String label,
        GraphEdgeMatcherDescriptor matcher) {
      super(nodes, label);
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
    }
  }

  /////////////////////////////////////
  // Customize actions for tree roots

  private static class ActionRemainsRoot extends ActionSolitaryRoot {

    public ActionRemainsRoot(GraphData<GraphNode> nodes, String label) {
      super(nodes, label);
    }

    @Override
    public void addMultiActions(IMenuManager manager, ViewEditor editor) {
    }

    @Override
    public void addItemActions(IMenuManager manager, final ViewEditor editor) {
      manager.add(new Action("Add hierarchy..", IAction.AS_PUSH_BUTTON) {
        @Override
        public void run() {
          // TODO: Run hierarchy picker ...
          GraphEdgeMatcherDescriptor matcher =
              GraphEdgeMatcherDescriptors.FORWARD;
          editor.addNodeTreeHierarchy(matcher);
        }
      });
    }
  }
}
