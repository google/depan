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

import com.google.devtools.depan.eclipse.stats.ElementKindStats;
import com.google.devtools.depan.eclipse.stats.ElementKindStatsViewer;
import com.google.devtools.depan.eclipse.ui.nodes.api.NodeProperties;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Collection;
import java.util.Collections;

/**
 * A Tool displaying information about a selected node.
 *
 * TODO: extends the information to a selected link, and other information
 * about the graph itself.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class InformationsTool extends ViewSelectionListenerTool
    implements ElementVisitor {

  private Label icon;
  private Text friendlyName;
  private Text name;
  private Text description;
  private Text container;
  private ElementKindStatsViewer statsViewer;

  @Override
  public void emptySelection() {
    friendlyName.setText("");
    name.setText("");
    description.setText("");
    container.setText("");
    icon.setImage(null);
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_INFOS;
  }

  @Override
  public String getName() {
    return Resources.NAME_INFORMATIONS;
  }

  @Override
  public Control setupComposite(Composite parent) {
    Composite topLevel = new Composite(parent, SWT.NONE);
    topLevel.setLayout(new GridLayout(1, false));

    Control activeGroup = setupActiveNode(topLevel);
    activeGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

    Control statsGroup =  setupStatsViewer(topLevel);
    statsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    return topLevel;
  }

  private Control setupActiveNode(Composite parent) {
    Group result = new Group(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, false));
    result.setText("Active Node");

    icon = new Label(result, SWT.NONE);
    GridData iconGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
    iconGridData.minimumWidth = 16;
    iconGridData.minimumHeight = 16;
    icon.setLayoutData(iconGridData);

    friendlyName = new Text(result, SWT.READ_ONLY);
    friendlyName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label nameTitle = new Label(result, SWT.NONE);
    nameTitle.setText("Name ");

    name = new Text(result, SWT.READ_ONLY);
    name.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label descriptionTitle = new Label(result, SWT.NONE);
    descriptionTitle.setText("Description ");

    description = new Text(result, SWT.READ_ONLY);
    description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label containerTitle = new Label(result, SWT.NONE);
    containerTitle.setText("Container ");

    container = new Text(result, SWT.READ_ONLY);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    return result;
  }

  private Control setupStatsViewer(Composite parent) {
    Group result = new Group(parent, SWT.NONE);
    result.setLayout(new FillLayout(SWT.VERTICAL));
    result.setText("Node Statistics");

    statsViewer = new ElementKindStatsViewer(result, SWT.NONE);

    return result;
  }

  public void setNode(GraphNode node) {
    icon.setImage(NodeProperties.getImage(node));
    friendlyName.setText(node.friendlyString());
    name.setText(node.toString());
    description.setText("");
    container.setText("");
  }

  @Override
  protected void clearControls() {
    super.clearControls();
    statsViewer.setInput(Collections.<ElementKindStats.Info>emptyList());
  }

  @Override
  protected void updateControls() {
    super.updateControls();
    statsViewer.setInput(getEditor().getElementKindStats());
  }

  /**
   * Set this tool to display information about the first selected node.
   */
  @Override
  public void updateSelectedExtend(Collection<GraphNode> extension) {
    if (extension.size() == 0) {
      emptySelection();
      return;
    }
    setNode(extension.iterator().next());
  }

  /**
   * Set this tool to display information about the first unselected node.
   * So this tool reflect information about the node that was last used
   * (the user clicked on it)
   */
  @Override
  public void updateSelectedReduce(Collection<GraphNode> reduction) {
    if (reduction.size() == 0) {
      emptySelection();
    }
    setNode(reduction.iterator().next());
  }

  /**
   * Set this tool to display information about the first selected node.
   * @see #updateSelectedAdd(GraphNode[])
   */
  @Override
  public void updateSelectionTo(Collection<GraphNode> selection) {
    updateSelectedExtend(selection);
  }
}
