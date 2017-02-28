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

import com.google.devtools.depan.platform.CollectionContentProvider;
import com.google.devtools.depan.platform.TableContentProvider;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;
import com.google.devtools.depan.remap_doc.eclipse.ui.widgets.DoubleElementEditorChooser;
import com.google.devtools.depan.remap_doc.model.MigrationGroup;
import com.google.devtools.depan.remap_doc.model.MigrationRule;
import com.google.devtools.depan.remap_doc.model.MigrationTask;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.Collection;

/**
 * A widget for editing a {@link MigrationRule}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationRuleEditor extends MigrationTaskAdapter {

  /**
   * The {@link MigrationTask} containing the rules.
   */
  private final MigrationTask task;

  /**
   * Content provider for the list of rules.
   */
  private TableContentProvider<MigrationRule<?>> rulesProvider;

  /**
   * The selected {@link MigrationRule}.
   */
  private MigrationRule<?> selectedRule;

  /**
   * The selected {@link MigrationGroup} (containing {@link #selectedRule}).
   */
  private MigrationGroup selectedGroup;

  /**
   * Top level {@link Control}.
   */
  private Control control;

  /**
   * Selector for a {@link MigrationGroup}.
   */
  private ComboViewer groupSelect;

  /**
   * List of rules.
   */
  private ListViewer rules;

  /**
   * Editor for a MigrationRule. This editor is dynamically loaded to match the
   * type of the selected {@link MigrationRule}.
   */
  private DoubleElementEditorChooser editorChooser;

  /**
   * Construct the {@link MigrationRuleEditor}.
   *
   * @param parent the parent widget.
   * @param task the {@link MigrationTask} to edit.
   */
  public MigrationRuleEditor(Composite parent, MigrationTask task) {
    this.task = task;
    this.control = createControl(parent);
  }

  /**
   * Create the main widget under the given parent.
   *
   * @param parent parent for the widget.
   * @return the top level Composite for this widget.
   */
  private Control createControl(Composite parent) {
    // components
    Composite topLevel = new Composite(parent, SWT.NONE);

    Composite leftPanel = new Composite(topLevel, SWT.NONE);
    Label labelGroup = new Label(leftPanel, SWT.NONE);
    groupSelect = new ComboViewer(leftPanel, SWT.READ_ONLY | SWT.FLAT);
    Label labelRules = new Label(leftPanel, SWT.NONE);
    rules = new ListViewer(leftPanel, SWT.SINGLE | SWT.BORDER);
    Button addRule = new Button(leftPanel, SWT.PUSH);
    Button removeRule = new Button(leftPanel, SWT.PUSH);

    Composite rightPanel = new Composite(topLevel, SWT.NONE);
    Label labelEditor = new Label(rightPanel, SWT.NONE);
    editorChooser = new DoubleElementEditorChooser(rightPanel, SWT.NONE);

    // layout
    GridLayout layout = new GridLayout(2, true);
    @SuppressWarnings("unused")
    GridLayout topLayout = new GridLayout(2, false);
    GridLayout leftLayout = new GridLayout(2, false);
    GridLayout rightLayout = new GridLayout(3, false);
    topLevel.setLayout(layout);
    leftPanel.setLayout(leftLayout);
    rightPanel.setLayout(rightLayout);

    leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    rightPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    labelGroup.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    groupSelect.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    labelRules.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
    rules.getControl().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
    addRule.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    removeRule.setLayoutData(
        new GridData(SWT.FILL, SWT.TOP, false, false));
    labelEditor.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));

    @SuppressWarnings("unused")
    GridData iconData = new GridData(16, 16);

    // content
    labelEditor.setText("Rule editor");
    labelGroup.setText("Group");
    groupSelect.setContentProvider(
        new CollectionContentProvider<MigrationGroup>(
            task.getMigrationGroups()));
    groupSelect.setInput(task.getMigrationGroups());

    labelRules.setText("Rules");
    rulesProvider = new TableContentProvider<MigrationRule<?>>();
    rulesProvider.initViewer(rules);
    addRule.setText("Add");
    removeRule.setText("Remove");
    // initialize the content of fields and images
    newRuleSelection(null);

    // actions
    groupSelect.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = groupSelect.getSelection();
        MigrationGroup group =
            Selections.getFirstElement(selection, MigrationGroup.class);
        if (null != group) {
          newGroupSelection(group);
        } else {
          newGroupSelection(null);
          deselectRule();
        }
      }
    });

    rules.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = rules.getSelection();
        MigrationRule<?> rule =
            Selections.getFirstElement(selection, MigrationRule.class);
        if (rule != null) {
          newRuleSelection(rule);
        }
      }
    });

    return topLevel;
  }

  /**
   * Callback when the given {@link MigrationGroup} is selected.
   *
   * @param group the newly selected MigrationGroup, or <code>null</code> if
   *        no group is selected.
   */
  private void newGroupSelection(MigrationGroup group) {
    this.selectedGroup = group;
    rulesProvider.clear();
    if (null != group) {
      for (MigrationRule<?> rule : group.getMigrationRules()) {
        rulesProvider.add(rule);
      }
    }
    rules.refresh(false);
    deselectRule();
  }

  /**
   * Callback when a MigrationRule is selected. Set the corresponding editor.
   *
   * @param rule the selected rule, or <code>null</code> to display an empty
   *        editor.
   */
  private void newRuleSelection(MigrationRule<?> rule) {
    this.selectedRule = rule;
    if (null == rule) {
      editorChooser.setNoEditor();
      return;
    }
    editorChooser.setEditorFor(rule.getSource());
  }

  /**
   * Deselect any selected {@link MigrationGroup}.
   */
  private void deselectGroup() {
    groupSelect.setSelection(new StructuredSelection());
    newGroupSelection(null);
  }

  /**
   * Deselect any selected {@link MigrationRule}.
   */
  private void deselectRule() {
    rules.setSelection(new StructuredSelection());
    newRuleSelection(null);
  }

  /**
   * @return return the top level {@link Control} for this widget.
   */
  public Control getControl() {
    return control;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskAdapter
   *      #groupsListUpdated(java.lang.Object)
   */
  @Override
  public void groupsListUpdated(Object source) {
    groupSelect.refresh(false);
    Collection<MigrationGroup> groups = task.getMigrationGroups();
    if (!groups.contains(selectedGroup)) {
      // the currently selected group have been deleted
      if (groups.size() < 1) {
        // group list is now empty
        deselectGroup();
      } else {
        MigrationGroup group = task.getMigrationGroups().iterator().next();
        groupSelect.setSelection(new StructuredSelection(group));
        newGroupSelection(group);
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
    groupSelect.refresh(true);
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
    if (this == source) {
      return;
    }
    if (!group.getMigrationRules().contains(selectedRule)) {
      // selected rule have been deleted
      deselectRule();
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
    if (this == source) {
      return;
    }
    if (rule == selectedRule) {
      newRuleSelection(rule);
    }
  }
}
