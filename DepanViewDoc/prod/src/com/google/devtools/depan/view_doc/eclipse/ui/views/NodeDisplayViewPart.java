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

package com.google.devtools.depan.view_doc.eclipse.ui.views;

import com.google.devtools.depan.collapse.model.CollapseData;
import com.google.devtools.depan.eclipse.ui.nodes.viewers.NodeViewerProvider;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.SelectionChangeListener;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.NodeDisplayTableControl;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeDisplayRepository;
import com.google.devtools.depan.view_doc.model.NodeLocationRepository;
import com.google.devtools.depan.view_doc.model.NodeSelectedRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Tool to view nodes and set their display properties.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodeDisplayViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Node Properties";

  /////////////////////////////////////
  // UX Elements

  private NodeDisplayTableControl propEditor;

  private CompactPrefsListener compactListener;

  /////////////////////////////////////
  // Node selected integration

  private NodeSelectedRepository selectedRepo;

  private static class PartNodeSelectedRepo
      implements NodeSelectedRepository {

    private final ViewEditor editor;

    public PartNodeSelectedRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public boolean isSelected(GraphNode node) {
      return editor.getSelectedNodes().contains(node);
    }

    @Override
    public void setSelected(GraphNode node, boolean selected) {
      Collection<GraphNode> change = Collections.singletonList(node);
      if (selected) {
        editor.extendSelection(change, null);
      } else {
        editor.reduceSelection(change, null);
      }
    }

    @Override
    public void addChangeListener(SelectionChangeListener listener) {
      editor.addSelectionChangeListener(listener);
    }

    @Override
    public void removeChangeListener(SelectionChangeListener listener) {
      editor.removeSelectionChangeListener(listener);
    }
  }

  /////////////////////////////////////
  // Display attribute integration

  private NodeDisplayRepository propRepo;

  private static class PartEdgeDisplayRepo
      implements NodeDisplayRepository {

    private final ViewEditor editor;

    private DisplayPrefsListener prefsListener;

    public PartEdgeDisplayRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public NodeDisplayProperty getDisplayProperty(GraphNode node) {
      return editor.getNodeProperty(node);
    }

    @Override
    public void setDisplayProperty(GraphNode node, NodeDisplayProperty props) {
      editor.setNodeProperty(node, props);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
      prefsListener = new DisplayPrefsListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
      // TODO: if multiple ChangeListener,
      // add Map<ChangeListener, EdgeDisplayListener>
      editor.removeViewPrefsListener(prefsListener);
    }
  }

  private static class DisplayPrefsListener extends ViewPrefsListener.Simple {

    private NodeDisplayRepository.ChangeListener listener;

    public DisplayPrefsListener(NodeDisplayRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void nodePropertyChanged(
        GraphNode node, NodeDisplayProperty newProperty) {
      listener.nodeDisplayChanged(node, newProperty);
    }
  }

  /////////////////////////////////////
  // Location integration

  private NodeLocationRepository posRepo;

  private static class PartNodeLocationRepo
      implements NodeLocationRepository {

    private final ViewEditor editor;

    private PosPrefsListener posListener;

    public PartNodeLocationRepo(ViewEditor editor) {
      this.editor = editor;
    }

    @Override
    public Point2D getLocation(GraphNode node) {
      return editor.getPosition(node);
    }

    @Override
    public void setLocation(GraphNode node, Point2D location) {
      Map<GraphNode, Point2D> update = Collections.singletonMap(node, location);
      editor.editNodeLocations(update, this);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
      posListener = new PosPrefsListener(editor, listener);
      editor.addViewPrefsListener(posListener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
      // TODO: if multiple ChangeListener,
      // add Map<ChangeListener, EdgeDisplayListener>
      editor.removeViewPrefsListener(posListener);
    }
  }

  private static class PosPrefsListener extends ViewPrefsListener.Simple {

    private final ViewEditor editor;

    private final NodeLocationRepository.ChangeListener listener;

    public PosPrefsListener(
        ViewEditor editor, NodeLocationRepository.ChangeListener listener) {
      this.editor = editor;
      this.listener = listener;
    }

    @Override
    public void nodeLocationsChanged(
        Map<GraphNode, Point2D> locations, Object author) {
      if (this == author) {
        return;
      }
      updateLocations(locations);
    }

    @Override
    public void nodeLocationsSet(final Map<GraphNode, Point2D> newLocations) {
      // All locations changed, regardless of their inclusion in new locations
      updateLocations(editor.getNodeLocations());
    }

    private void updateLocations(final Map<GraphNode, Point2D> locations) {
      // All locations changed, regardless of their inclusion in new locations
      for (Entry<GraphNode, Point2D> entry : locations.entrySet()) {
        listener.nodeLocationChanged(entry.getKey(), entry.getValue());
      }
    }
  }

  /////////////////////////////////////
  // ViewPart integration

  @Override
  public Image getTitleImage() {
    return ViewDocResources.IMAGE_NODEEDITOR;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  @Override
  protected void createGui(Composite parent) {
    Composite result = Widgets.buildGridContainer(parent, 1);

    propEditor = new NodeDisplayTableControl(result);
    propEditor.setLayoutData(Widgets.buildGrabFillData());
  }

  @Override
  protected void disposeGui() {
    releaseResources();
  }

  /////////////////////////////////////
  // ViewDoc/Editor integration

  @Override
  protected void acquireResources() {

    ViewEditor editor = getEditor();

    propRepo = new PartEdgeDisplayRepo(editor);
    posRepo = new PartNodeLocationRepo(editor);
    selectedRepo = new PartNodeSelectedRepo(editor);
    propEditor.setNodeRepository(posRepo, propRepo, selectedRepo);

    updateInput();

    compactListener = new CompactPrefsListener();
    editor.addViewPrefsListener(compactListener);
  }

  @Override
  protected void releaseResources() {
    propEditor.removeNodeRepository();
    propRepo = null;

    if (null != compactListener) {
      getEditor().removeViewPrefsListener(compactListener);
      compactListener = null;
    }
  }

  private void updateInput() {
    NodeViewerProvider provider = getEditor().getNodeViewProvider();
    propEditor.setInput(provider);
  }

  private class CompactPrefsListener extends ViewPrefsListener.Simple {

    @Override
    public void collapseChanged(
        Collection<CollapseData> created,
        Collection<CollapseData> removed,
        Object author) {
      updateInput();
    }

    @Override
    public void nodeTreeChanged() {
      updateInput();
    }
  }
}
