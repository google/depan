/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.tasks.MigrationGroup;
import com.google.devtools.depan.tasks.MigrationRule;
import com.google.devtools.depan.tasks.MigrationTask;
import com.google.devtools.depan.util.XmlPersist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Editor for Remap (.dpanr) files.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RemapEditor extends MultiPageEditorPart
    implements MigrationTaskListener {

  public static final String ID =
      "com.google.devtools.depan.eclipse.editors.RemapEditor";

  /**
   * Persistence object for the {@link MigrationTask}.
   */
  private XmlPersist<MigrationTask> xmlMigrationTask;
  private MigrationTask migrationTask;

  /**
   * Whether the edited file is dirty (modifications unsaved) or not.
   */
  private boolean isDirty;

  /**
   * Listeners for modifications on the MigrationTask.
   */
  private Collection<MigrationTaskListener> taskListeners =
      new ArrayList<MigrationTaskListener>();

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
   */
  @Override
  protected void createPages() {
    createOverviewPage();
    createMigrationTaskPage();
    createMigrationGroupPage();
    createMigrationRulePage();

    // recursively set a white background.
    Color color = new Color(
        getContainer().getDisplay(), new RGB(255, 255, 255));
    for (Control c : getContainer().getChildren()) {
      Tools.setBackgroundRecursively(color, c);
    }
  }

  /**
   * Create the overview page showing informations about the MigrationTask.
   */
  private void createOverviewPage() {
    RemapOverview overview = new RemapOverview(
        migrationTask, getContainer());

    int index = addPage(overview.getControl());
    setPageText(index, "Overview");
    taskListeners.add(overview);
  }

  /**
   * Create the basic editor.
   */
  private void createMigrationTaskPage() {
    MigrationTaskEditor migrationTaskEditor =
        new MigrationTaskEditor(migrationTask, getContainer());

    int index = addPage(migrationTaskEditor.getControl());
    setPageText(index, "Task");
    taskListeners.add(migrationTaskEditor);
  }

  /**
   * Create the migration group editor.
   */
  private void createMigrationGroupPage() {
    MigrationGroupEditor migrationGroupEditor = new MigrationGroupEditor(
        migrationTask, getContainer(), this);
    int index = addPage(migrationGroupEditor.getControl());
    setPageText(index, "Group");
    taskListeners.add(migrationGroupEditor);
  }

  /**
   * Create the migration rule editor.
   */
  private void createMigrationRulePage() {
    MigrationRuleEditor migrationRuleEditor = new MigrationRuleEditor(
        getContainer(), migrationTask);
    int index = addPage(migrationRuleEditor.getControl());
    setPageText(index, "Rule");
    taskListeners.add(migrationRuleEditor);
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    super.init(site, input);

    if (input instanceof IFileEditorInput) {
      URI uri = ((IFileEditorInput) input).getFile().getLocationURI();
      xmlMigrationTask = XmlPersist.load(uri);
      migrationTask = xmlMigrationTask.getObject();
      this.setPartName(migrationTask.getName());
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart
   *      #doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {
    xmlMigrationTask.save();
    setDirtyState(false);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return true;
  }

  public void setDirtyState(boolean dirty) {
    this.isDirty = dirty;
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.MultiPageEditorPart#isDirty()
   */
  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #dataUpdated()
   */
  public void dataUpdated(Object source) {
    setDirtyState(true);
    for (MigrationTaskListener listener : taskListeners) {
      listener.dataUpdated(source);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #groupUpdated(com.google.devtools.depan.tasks.MigrationGroup)
   */
  public void groupUpdated(Object source, MigrationGroup group) {
    setDirtyState(true);
    for (MigrationTaskListener listener : taskListeners) {
      listener.groupUpdated(source, group);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #groupsListUpdated()
   */
  public void groupsListUpdated(Object source) {
    setDirtyState(true);
    for (MigrationTaskListener listener : taskListeners) {
      listener.groupsListUpdated(source);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #ruleListUpdated(com.google.devtools.depan.tasks.MigrationGroup)
   */
  public void ruleListUpdated(Object source, MigrationGroup group) {
    setDirtyState(true);
    for (MigrationTaskListener listener : taskListeners) {
      listener.ruleListUpdated(source, group);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #ruleUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup,
   *      com.google.devtools.depan.tasks.MigrationRule)
   */
  public void ruleUpdated(Object source, MigrationGroup group,
      MigrationRule<?> rule) {
    setDirtyState(true);
    for (MigrationTaskListener listener : taskListeners) {
      listener.ruleUpdated(source, group, rule);
    }

  }
}
