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

import com.google.devtools.depan.eclipse.utils.RelationPropertyEditorPart;
import com.google.devtools.depan.eclipse.utils.RelationPropertyRelationTableEditor.RelPropRepository;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.views.ViewEditorTool;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.relations.RelationSetDescriptor;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.EdgeDisplayProperty;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.List;

/**
 * Tool for selecting relations that have to be shown.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelationPropertyTool extends ViewEditorTool {

  /**
   * The {@link RelationPropertyEditorPart} that controls the UX.
   */
  private RelationPropertyEditorPart propertyEditor;

  private class ToolPropRepo implements RelPropRepository {
    @Override
    public EdgeDisplayProperty getDisplayProperty(Relation relation) {
      if (!hasEditor()) {
        return null;
      }
      return getEditor().getRelationProperty(relation);
    }

    @Override
    public void setDisplayProperty(Relation relation, EdgeDisplayProperty prop) {
      if (!hasEditor()) {
        return;
      }
      getEditor().setRelationProperty(relation, prop);
    }
  }

  @Override
  public Image getIcon() {
    return Resources.IMAGE_RELATIONPICKER;
  }

  @Override
  public String getName() {
    return Resources.NAME_RELATIONPROPERTYTOOL;
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
    propertyEditor.setRelationSetSelectorInput(relationSet, choices);

    updateView();
  }

  @Override
  public Control setupComposite(Composite parent) {
    this.propertyEditor = new RelationPropertyEditorPart();
    return propertyEditor.getControl(parent, new ToolPropRepo());
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
    propertyEditor.updateTable(editor.getBuiltinAnalysisPlugins());
    propertyEditor.selectRelations(editor.getDisplayRelations());
  }
}
