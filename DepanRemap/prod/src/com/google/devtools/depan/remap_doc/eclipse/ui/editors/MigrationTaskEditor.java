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

import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.TableContentProvider;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * An editor for a {@link MigrationTask}. Permit editing information fields,
 * but not the associated MigrationGroup or MigrationRule. To
 * edit those elements, see {@link MigrationGroupEditor} and
 * {@link MigrationRuleEditor}.
 * @see MigrationGroupEditor
 * @see MigrationRuleEditor
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationTaskEditor extends MigrationTaskAdapter {

  /**
   * This editor's {@link MigrationTask}.
   */
  private final MigrationTask migrationTask;

  /**
   * Content provider for the list of engineers.
   */
  private TableContentProvider<String> engineers;

  /**
   * Top level widget for this tool.
   */
  private Control control;

  /**
   * {@link MigrationTask}'s id editor.
   */
  private Text id = null;

  /**
   * {@link MigrationTask}'s name editor.
   */
  private Text name = null;

  /**
   * {@link MigrationTask}'s description editor.
   */
  private Text description = null;

  /**
   * {@link MigrationTask}'s quarter editor.
   */
  private Text quarter = null;

  /**
   * {@link MigrationTask}'s updated by editor.
   */
  private Text updatedBy = null;

  /**
   * {@link MigrationTask}'s update date editor.
   */
  private DateTime updateDate = null;

  /**
   * {@link MigrationTask}'s engineers table.
   */
  private TableViewer engineersTable = null;

  /**
   * Construct a {@link MigrationTaskEditor}.
   *
   * @param migrationTask the task we want to edit.
   * @param parent the parent Composite.
   */
  public MigrationTaskEditor(MigrationTask migrationTask, Composite parent) {
    this.migrationTask = migrationTask;
    this.engineers = new TableContentProvider<String>();

    control = createControl(parent);
  }

  /**
   * Create the editor's GUI.
   *
   * @param parent Parent Composite.
   * @return the top level Control for the GUI.
   */
  private Control createControl(Composite parent) {
    // controls
    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    topLevel.setLayout(layout);

    Label labelId = new Label(topLevel, SWT.NONE);
    id = new Text(topLevel, SWT.BORDER);
    Label labelName = new Label(topLevel, SWT.NONE);
    name = new Text(topLevel, SWT.BORDER);
    Label labelDescription = new Label(topLevel, SWT.NONE);
    description = new Text(
        topLevel, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
    Label labelQuarter = new Label(topLevel, SWT.NONE);
    quarter = new Text(topLevel, SWT.BORDER);
    Label labelUpdatedBy = new Label(topLevel, SWT.NONE);
    updatedBy = new Text(topLevel, SWT.BORDER);
    Label labelUpdateDate = new Label(topLevel, SWT.NONE);
    updateDate = new DateTime(topLevel, SWT.CALENDAR);
    Label labelEngineers = new Label(topLevel, SWT.None);

    Control engineersEdit = createEngineersEditor(topLevel);

    // content
    labelId.setText("ID");
    labelName.setText("Name");
    labelDescription.setText("Description");
    labelQuarter.setText("Quarter");
    labelUpdatedBy.setText("Updated by");
    labelUpdateDate.setText("Updated date");
    labelEngineers.setText("Engineers");

    // layout
    labelUpdateDate.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    labelDescription.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    labelEngineers.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    id.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    name.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    quarter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    updatedBy.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    GridData descriptionLayout = new GridData(SWT.FILL, SWT.FILL, true, false);
    descriptionLayout.heightHint = 150;
    description.setLayoutData(descriptionLayout);
    engineersEdit.setLayoutData(descriptionLayout);

    fillContent();

    return topLevel;
  }

  /**
   * Create the editor for engineers. Display a list, and add/remove buttons.
   *
   * @param parent Parent for this widget.
   * @return the top level Control for this widget.
   */
  private Control createEngineersEditor(Composite parent) {
    // widgets
    Composite topLevel = new Composite(parent, SWT.None);
    GridLayout layout = new GridLayout(3, false);
    topLevel.setLayout(layout);

    engineersTable =
        new TableViewer(topLevel, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
    final Text newEngineer = new Text(topLevel, SWT.BORDER);
    Button addEngineer = new Button(topLevel, SWT.PUSH);
    Button removeEngineer = new Button(topLevel, SWT.PUSH);

    // content
    engineers.initViewer(engineersTable);
    addEngineer.setText("Add");
    removeEngineer.setText("Remove selected");

    // layout
    engineersTable.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
    newEngineer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    addEngineer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));
    removeEngineer.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, false));

    engineersTable.setComparator(new AlphabeticSorter(true));

    // actions
    addEngineer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        addEngineer(newEngineer.getText());
        newEngineer.setText("");
      }
    });

    removeEngineer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        removeSelectedEngineers();
      }
    });

    newEngineer.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        addEngineer(newEngineer.getText());
        newEngineer.setText("");
      }
    });

    fillEngineers();
    engineersTable.refresh(false);

    return topLevel;
  }

  /**
   * Fill all fields according to the migrationTask.
   */
  private void fillContent() {
    id.setText(migrationTask.getId());
    name.setText(migrationTask.getName());
    description.setText(migrationTask.getDescription());
    quarter.setText(migrationTask.getOkrQuarter());
    updatedBy.setText(migrationTask.getUpdatedBy());

    ZonedDateTime date = migrationTask.getUpdatedDate();
    if (null == date) {
      date = ZonedDateTime.now();
    }
    updateDate.setYear(date.getYear());
    updateDate.setMonth(date.getMonthValue());
    updateDate.setDay(date.getDayOfMonth());
  }

  /**
   * Fill the engineers list.
   */
  private void fillEngineers() {
    for (String engineer : migrationTask.getEngineers()) {
      engineers.add(engineer);
    }
  }

  /**
   * Add the given engineerName in the engineer list (widget) and in the model.
   * If the name is an empty String, don't do anything.
   *
   * @param engineerName new engineer's name.
   */
  public void addEngineer(String engineerName) {
    if (engineerName.length() <= 0) {
      return;
    }
    engineers.add(engineerName);
    engineersTable.refresh(false);

    migrationTask.addEngineers(engineerName);
  }

  /**
   * Remove the selected engineers in the table from the list and the model.
   * Works with multiple names selected.
   */
  protected void removeSelectedEngineers() {
    ISelection selection = engineersTable.getSelection();
    Collection<String> names =
        Selections.getSelection(selection, String.class);
    for (String engineerName : names) {
      engineers.remove(engineerName);
      migrationTask.removeEngineer(engineerName);
    }
    engineersTable.refresh(false);
  }

  /**
   * @return return the top level widget for this editor.
   */
  public Control getControl() {
    return control;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #dataUpdated(java.lang.Object)
   */
  @Override
  public void dataUpdated(Object source) {
    if (this == source) {
      return;
    }
    fillContent();
  }
}
