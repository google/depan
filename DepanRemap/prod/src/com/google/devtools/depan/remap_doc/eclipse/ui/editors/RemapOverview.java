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


import com.google.devtools.depan.platform.eclipse.ui.widgets.Sasher;
import com.google.devtools.depan.remap_doc.model.MigrationGroup;
import com.google.devtools.depan.remap_doc.model.MigrationRule;
import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.ArrayList;

/**
 * Overview panel, showing informations about a {@link MigrationTask}. Doens't
 * support editing those informations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RemapOverview extends MigrationTaskAdapter
    implements SelectionListener {

  /**
   * The task to show.
   */
  private MigrationTask task;

  /**
   * List for {@link MigrationGroup}s.
   */
  private MigrationGroupListViewer migrationGroupList;

  /**
   * Table listing all {@link MigrationRule}s.
   */
  private RemapTable remapTable;

  /**
   * Top level control.
   */
  private Control control;

  private MigrationTaskView migrationTaskView;

  /**
   * Currently selected {@link MigrationGroup}, for showing it's
   * {@link MigrationRule}s.
   */
  private MigrationGroup selectedGroup;

  /**
   *
   * @param task
   * @param parent
   */
  public RemapOverview(MigrationTask task, Composite parent) {
    this.task = task;

    control = createControl(parent);
  }

  /**
   * Create the GUI control.
   *
   * @param parent Composite parent.
   * @return the top level control.
   */
  private Control createControl(Composite parent) {
    Composite topLevel = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, true);
    layout.verticalSpacing = 12;
    topLevel.setLayout(layout);

    migrationTaskView = new MigrationTaskView(task, topLevel);
    Sasher sasher = new Sasher(topLevel, SWT.NONE);
    migrationGroupList =
        new MigrationGroupListViewer(sasher, this, task);
    remapTable = new RemapTable(sasher);

    sasher.init(migrationGroupList.getControl(), remapTable.getControl(),
        SWT.HORIZONTAL, 40);

    migrationTaskView.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));
    sasher.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    return topLevel;
  }

  /**
   * @return the top level Control for this GUI.
   */
  public Control getControl() {
    return control;
  }

  @Override
  public void widgetSelected(SelectionEvent e) {
    selectGroup(migrationGroupList.getSelected());
  }

  /**
   * Called when the given {@link MigrationGroup}, is sellected in the list.
   * Update the remapTable.
   *
   * @param group the newly selected MigrationGroup.
   */
  private void selectGroup(MigrationGroup group) {
    selectedGroup = group;
    remapTable.setData(selectedGroup.getMigrationRules());
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent e) {
    // noop
  }

  @Override
  public void dataUpdated(Object source) {
    migrationTaskView.fillContent();
  }

  @Override
  public void groupsListUpdated(Object source) {
    migrationGroupList.refresh();
    if (!task.getMigrationGroups().contains(selectedGroup)) {
      // if the new migration group's list doesn't contains anymore the
      // currently selected group, we arbitrary select the first in the list.
      if (task.getMigrationGroups().size() < 1) {
        // no more groups in the list.
        remapTable.setData(new ArrayList<MigrationRule<?>>());
      } else {
        MigrationGroup group = task.getMigrationGroups().iterator().next();
        migrationGroupList.setSelected(group);
        selectGroup(group);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #groupUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup)
   */
  @Override
  public void groupUpdated(Object source, MigrationGroup group) {
    migrationGroupList.refresh();
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #ruleListUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup)
   */
  @Override
  public void ruleListUpdated(Object source, MigrationGroup group) {
    if (group == selectedGroup) {
      remapTable.refresh(true);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #ruleUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup,
   *      com.google.devtools.depan.tasks.MigrationRule)
   */
  @Override
  public void ruleUpdated(Object source, MigrationGroup group,
      MigrationRule<?> rule) {
    if (group == selectedGroup) {
      remapTable.refresh(true);
    }
  }
}
