/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.remap_doc.eclipse.ui.editors;

import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * A very simple widget giving some informations on a given
 * {@link MigrationTask}.
 *
 * TODO(ycoppel): implements {@link MigrationTaskListener}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class MigrationTaskView {

  /**
   * Top level Widget.
   */
  private final Composite composite;

  /**
   * The displayed {@link MigrationTask}.
   */
  private final MigrationTask migrationTask;

  /**
   * shows {@link MigrationTask#getId()} value.
   */
  private Label id = null;

  /**
   * shows {@link MigrationTask#getName()} value.
   */
  private Label name = null;

  /**
   * shows {@link MigrationTask#getDescription()} value.
   */
  private Label description = null;

  /**
   * shows {@link MigrationTask#getOkrQuarter()} value.
   */
  private Label quarter = null;

  /**
   * shows {@link MigrationTask#getUpdatedBy()} value.
   */
  private Label updatedBy = null;

  /**
   * Create the {@link MigrationTaskView} widget.
   *
   * @param migrationTask the {@link MigrationTask} to show.
   * @param parent the parent Composite.
   */
  public MigrationTaskView(MigrationTask migrationTask, Composite parent) {
    this.migrationTask = migrationTask;
    this.composite = setupComposite(parent);
    fillContent();
  }

  /**
   * Construct the GUI under the given parent.
   *
   * @param parent the parent Composite.
   * @return the top level widget.
   */
  private Composite setupComposite(Composite parent) {
    // widgets
    Composite topLevel = new Composite(parent, SWT.NONE);

    Label labelId = new Label(topLevel, SWT.NONE);
    id = new Label(topLevel, SWT.NONE);
    Label labelName = new Label(topLevel, SWT.NONE);
    name = new Label(topLevel, SWT.NONE);
    Label labelDescription = new Label(topLevel, SWT.NONE);
    description = new Label(topLevel, SWT.NONE);
    Label labelQuarter = new Label(topLevel, SWT.NONE);
    quarter = new Label(topLevel, SWT.NONE);
    Label labelUpdatedBy = new Label(topLevel, SWT.NONE);
    updatedBy = new Label(topLevel, SWT.NONE);

    // content
    labelId.setText("ID");
    labelName.setText("Name");
    labelDescription.setText("Description");
    labelQuarter.setText("Quarter");
    labelUpdatedBy.setText("Updated by");

    // layout
    GridLayout layout = new GridLayout(2, false);
    layout.horizontalSpacing = 22;
    layout.verticalSpacing = 9;
    topLevel.setLayout(layout);
    id.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    name.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    quarter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    updatedBy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Font font = name.getFont();
    FontDescriptor bold = FontDescriptor.createFrom(font);
    bold = bold.setStyle(SWT.BOLD);
    FontDescriptor big = bold.setHeight(18);
    Font boldFont = bold.createFont(font.getDevice());

    name.setFont(big.createFont(font.getDevice()));
    id.setFont(boldFont);
    description.setFont(boldFont);
    quarter.setFont(boldFont);
    updatedBy.setFont(boldFont);

    return topLevel;
  }

  /**
   * Fill the fields with the MigrationTask values.
   */
  protected void fillContent() {
    id.setText(migrationTask.getId());
    name.setText(migrationTask.getName());
    description.setText(migrationTask.getDescription());
    quarter.setText(migrationTask.getOkrQuarter());
    updatedBy.setText(migrationTask.getUpdatedBy());
  }

  /**
   * @return return the top level Control.
   */
  public Control getControl() {
    return composite;
  }
}
