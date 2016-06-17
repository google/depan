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

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ViewDocResources;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.widgets.NodeDisplayTableControl;
import com.google.devtools.depan.view_doc.model.NodeDisplayProperty;
import com.google.devtools.depan.view_doc.model.NodeDisplayRepository;
import com.google.devtools.depan.view_doc.model.ViewPrefsListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.Collection;

/**
 * Tool to set display properties for individual nodes.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodeDisplayViewPart extends AbstractViewDocViewPart {

  public static final String PART_NAME = "Node Properties";

  /**
   * The <code>RelationSetEditorControl</code> that controls the UX.
   */
  private NodeDisplayTableControl propEditor;

  private NodeDisplayRepository propRepo;

  private static class PartEdgeDisplayRepo
      implements NodeDisplayRepository {

    private final ViewEditor editor;

    private PartPrefsListener prefsListener;

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
      prefsListener = new PartPrefsListener(listener);
      editor.addViewPrefsListener(prefsListener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
      // TODO: if multiple ChangeListener,
      // add Map<ChangeListener, EdgeDisplayListener>
      editor.removeViewPrefsListener(prefsListener);
    }
  }

  private static class PartPrefsListener extends ViewPrefsListener.Simple {

    private NodeDisplayRepository.ChangeListener listener;

    public PartPrefsListener(NodeDisplayRepository.ChangeListener listener) {
      this.listener = listener;
    }

    @Override
    public void nodePropertyChanged(
        GraphNode node, NodeDisplayProperty newProperty) {
      listener.nodeDisplayChanged(node, newProperty);
    }
  }

  @Override
  public Image getTitleImage() {
    return ViewDocResources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getTitle() {
    return PART_NAME;
  }

  @Override
  protected void createGui(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout());

    propEditor = new NodeDisplayTableControl(result);
    propEditor.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
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
    propEditor.setNodeDisplayRepository(editor, propRepo);

    // TODO: Should come from editor
    Collection<GraphNode> edges = editor.getExposedGraph().getNodes();
    propEditor.setInput(edges);
    propEditor.update();
  }

  @Override
  protected void releaseResources() {
    propEditor.removeNodeDisplayRepository(propRepo);
    propRepo = null;
  }
}
