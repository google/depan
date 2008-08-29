/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.eclipse.views.ViewSelectionListenerTool;
import com.google.devtools.depan.model.ElementVisitor;
import com.google.devtools.depan.model.GraphNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Tool displaying informations about a selected node.
 *
 * TODO: extends the informations to a selected link, and other information
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

    icon = new Label(topLevel, SWT.NONE);
    friendlyName = new Text(topLevel, SWT.READ_ONLY);
    Label nameTitle = new Label(topLevel, SWT.NONE);
    name = new Text(topLevel, SWT.READ_ONLY);
    Label descriptionTitle = new Label(topLevel, SWT.NONE);
    description = new Text(topLevel, SWT.READ_ONLY);
    Label containerTitle = new Label(topLevel, SWT.NONE);
    container = new Text(topLevel, SWT.READ_ONLY);


    nameTitle.setText("Name ");
    descriptionTitle.setText("Description ");
    containerTitle.setText("Container ");

    topLevel.setLayout(new GridLayout(2, false));
    GridData iconGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
    iconGridData.minimumWidth = 16;
    iconGridData.minimumHeight = 16;
    icon.setLayoutData(iconGridData);
    friendlyName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    name.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    return topLevel;
  }

  public void setNode(GraphNode node) {
    icon.setImage(Tools.getIcon(node));
    friendlyName.setText(node.friendlyString());
    name.setText("");
    description.setText("");
    container.setText("");

// FIXME(yc): this is a visitor. plugins should set data into icon, name
//            description...
//    if (node instanceof JavaElement) {
//      ((JavaElement) node).accept(this);
//    }
  }

  /**
   * Set this tool to display informations about the first selected node.
   */
  @Override
  public void updateSelectedAdd(GraphNode[] selection) {
    if (selection.length > 0) {
      setNode(selection[0]);
    } else {
      emptySelection();
    }
  }

  /**
   * Set this tool to display informations about the first unselected node.
   * So this tool reflect informations about the node that was last used
   * (the user clicked on it)
   */
  @Override
  public void updateSelectedRemove(GraphNode[] selection) {
    if (selection.length > 0) {
      setNode(selection[0]);
    } else {
      emptySelection();
    }
  }

  /**
   * Set this tool to display informations about the first selected node.
   * @see #updateSelectedAdd(GraphNode[])
   */
  @Override
  public void updateSelectionTo(GraphNode[] selection) {
    updateSelectedAdd(selection);
  }
}
