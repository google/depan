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

import com.google.devtools.depan.relations.eclipse.ui.widgets.NewRelationSetWizard;

import org.eclipse.jface.wizard.WizardDialog;
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

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class VisibleRelationsViewPart extends AbstractViewDocViewPart {

  /**
   * The <code>RelationSetEditorPart</code> that controls the UX.
   */
  private RelationSetEditorPart relationSetEditor;

  private class ToolRelationRepo implements RelationCheckedRepository {
    @Override
    public boolean getRelationChecked(Relation relation) {
      if (!hasEditor()) {
        return false;
      }

      return getEditor().isVisibleRelation(relation);
    }

    @Override
      public void setRelationChecked(Relation relation, boolean isChecked) {
      if (!hasEditor()) {
        return;
      }
      getEditor().setVisibleRelation(relation, isChecked);
    }
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getName() {
    return Resources.NAME_RELATIONVISIBLETOOL;
  }

  @Override
  protected void clearControls() {
    updateView();
  }

  @Override
  protected void updateControls() {
    super.updateControls();

    // RelationSet picker first
    RelationSetDescriptor relationSet = getEditor().getDisplayRelationSet();
    List<RelationSetDescriptor> choices = getEditor().getRelationSetChoices();
    relationSetEditor.setRelationSetSelectorInput(relationSet, choices);

    updateView();
  }

  @Override
  protected void createGui(Composite parent) {
    this.relationSetEditor = new RelationSetEditorPart();
    return relationSetEditor.getControl(parent, new ToolRelationRepo());

    Composite saves = setupSaveButtons(topLevel);
    saves.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

  }

  private Composite setupSaveButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    result.setLayout(layout);

    Button saveRels = new Button(result, SWT.PUSH);
    saveRels.setText("Save selected relations as...");
    saveRels.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveRels.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    Button saveProps = new Button(result, SWT.PUSH);
    saveProps.setText("Save selected properties as...");
    saveProps.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    saveProps.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveSelection();
      }
    });

    return result;
  }

  /**
   * Open a dialog to save the current selection under a new name.
   */
  protected void saveSelection() {
    if (null == shell) {
      return;
    }

    NewRelationSetWizard wizard =
        new NewRelationSetWizard(buildRelationSet());
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  @Override
  protected void disposeGui() {
    // TODO Auto-generated method stub
    
  }
  /////////////////////////////////////

  /**
   * Update the view after a change in the model.
   */
  private void updateView() {
    if (!hasEditor()) {
      return;
    }

    ViewEditor editor = getEditor();
    relationSetEditor.updateTable(editor.getBuiltinAnalysisPlugins());
    relationSetEditor.selectRelations(editor.getDisplayRelations());
  }

  @Override
  protected void acquireResources() {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void releaseResources() {
    // TODO Auto-generated method stub
    
  }

}
