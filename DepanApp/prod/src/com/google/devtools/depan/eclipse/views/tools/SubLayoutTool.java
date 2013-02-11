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

import com.google.devtools.depan.eclipse.utils.LayoutPickerControl;
import com.google.devtools.depan.eclipse.utils.RelationshipSetPickerControl;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.relsets.RelSetDescriptor;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class SubLayoutTool extends ViewEditorTool {

  /** Drop down List of available layouts. */
  private LayoutPickerControl layoutPicker = null;

  /** Selector for named relationships sets. */
  private RelationshipSetPickerControl relationshipSetselector = null;

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
    Composite baseComposite = new Composite(parent, SWT.NONE);
    GridLayout grid = new GridLayout(2, false);
    baseComposite.setLayout(grid);

    // components
    new Label(baseComposite, SWT.NONE).setText("Sub layout : ");
    layoutPicker = new LayoutPickerControl(baseComposite, false);
    layoutPicker.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

    Label selectLabel = RelationshipSetPickerControl.createPickerLabel(baseComposite);

    relationshipSetselector = new RelationshipSetPickerControl(baseComposite);
    relationshipSetselector.setLayoutData(
      new GridData(SWT.FILL, SWT.CENTER, true, false));

    Button apply = new Button(baseComposite, SWT.PUSH);
    apply.setText("Apply");
    apply.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    Label help = new Label(baseComposite, SWT.WRAP);
    help.setText("The relationship set is used only in layouts requiring "
        + "a hierarchy. Basically Tree layouts.\n\n"
        + ""
        + "If \"Set size to\" is not selected, the size used is the bounding "
        + "box of all selected nodes. So if your nodes are in line, you will "
        + "most likely get all your nodes at the same position.\n\n"
        + ""
        + "If there are no selected nodes, apply the layout / [default]size "
        + "to the entire graph.");
    help.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

    // actions
    apply.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        apply();
      }
    });

    return baseComposite;
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // Update the RelSet picker for auto-collapse.
    RelationshipSet selectedRelSet = getEditor().getContainerRelSet();
    List<RelSetDescriptor> choices = getEditor().getRelSetChoices();
    relationshipSetselector.setInput(selectedRelSet, choices );
  }

  protected void apply() {
    if (!hasEditor()) {
      return;
    }

    try {
      LayoutGenerator layout = layoutPicker.getLayoutChoice();
      getEditor().applyLayout(layout, relationshipSetselector.getSelection());
    } catch (IllegalArgumentException ex) {
      // bad layout. don't do anything for the layout, but still finish the
      // creation of the view.
      Logger logger = Logger.getLogger(SubLayoutTool.class.getName());
      logger.warning(
          "Bad layout.  Selected " + layoutPicker.getLayoutName());
    }
  }
}
