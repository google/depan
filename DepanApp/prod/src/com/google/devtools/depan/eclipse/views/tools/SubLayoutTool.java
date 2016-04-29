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

import com.google.devtools.depan.eclipse.utils.LayoutChoicesControl;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerators;

import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class SubLayoutTool extends ViewEditorTool {

  /** Main tool form */
  private Composite toolPanel;

  /** Drop down List of available layouts. */
  private LayoutChoicesControl layoutChoices = null;

  @Override
  public Image getIcon() {
    return Resources.IMAGE_SUBLAYOUT;
  }

  @Override
  public String getName() {
    return Resources.NAME_SUBLAYOUT;
  }

  @Override
  public Control setupComposite(Composite parent) {
    toolPanel = new Composite(parent, SWT.NONE);
    toolPanel.setLayout(new GridLayout(1, false));

    layoutChoices = new LayoutChoicesControl(toolPanel,
        LayoutChoicesControl.Style.LINEAR);
    layoutChoices.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
    layoutChoices.setLayoutChoices(LayoutGenerators.getLayoutNames(false));

    Button apply = new Button(toolPanel, SWT.PUSH);
    apply.setText("Apply");
    apply.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
    apply.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        apply();
      }
    });

    return toolPanel;
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // Update the RelSet picker for auto-collapse.
    GraphEdgeMatcherDescriptor edgeMatcher = getEditor().getTreeEdgeMatcher();
    List<GraphEdgeMatcherDescriptor> choices =
        getEditor().getTreeEdgeMatcherChoices();
    layoutChoices.setEdgeMatcherInput(edgeMatcher, choices);
    toolPanel.layout();
  }

  protected void apply() {
    if (!hasEditor()) {
      return;
    }

    try {
      LayoutGenerator layout = layoutChoices.getLayoutGenerator();
      getEditor().applyLayout(layout, layoutChoices.getEdgeMatcher());
    } catch (IllegalArgumentException ex) {
      // bad layout. don't do anything for the layout, but still finish the
      // creation of the view.
      Logger logger = Logger.getLogger(SubLayoutTool.class.getName());
      logger.warning(
          "Bad layout.  Selected " + layoutChoices.getLayoutName());
    }
  }
}
