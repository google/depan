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
package com.google.devtools.depan.eclipse.preferences;

import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeColors;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeShape;
import com.google.devtools.depan.eclipse.preferences.NodePreferencesIds.NodeSize;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference page for node rendering options.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NodePreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  private ScopedPreferenceStore preferences;

  public NodePreferencesPage() {
    super(GRID);
    preferences = PreferencesIds.getInstanceStore();
    setPreferenceStore(preferences);
  }

  @Override
  protected void createFieldEditors() {
    Composite parent = getFieldEditorParent();

    // colors: on/off
    BooleanFieldEditor nodeColors = new BooleanFieldEditor(
        NodePreferencesIds.NODE_COLOR_ON, "Node coloring...", parent);
    // color: mode
    RadioGroupFieldEditor colorSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_COLOR, "...shows...", 3, new String[][] {
            {"Degree", NodeColors.DEGREE.toString()},
            {"Role", NodeColors.ROLE.toString()},
            {"Voltage", NodeColors.VOLTAGE.toString()}
        }, parent, true);

    // shape: on/off
    BooleanFieldEditor nodeShapes = new BooleanFieldEditor(
        NodePreferencesIds.NODE_SHAPE_ON, "Node shapes...", parent);
    // shape: mode
    RadioGroupFieldEditor shapeSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_SHAPE, "...shows...", 2, new String[][] {
            {"Degree", NodeShape.DEGREE.toString()},
            {"Role", NodeShape.ROLE.toString()},
        }, parent, true);

    // size: on/off
    BooleanFieldEditor nodeSizes = new BooleanFieldEditor(
        NodePreferencesIds.NODE_SIZE_ON, "Node sizes...", parent);
    // size: mode
    RadioGroupFieldEditor sizeSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_SIZE, "...shows...", 2, new String[][] {
            {"Degree", NodeSize.DEGREE.toString()},
            {"Voltage", NodeSize.VOLTAGE.toString()},
        }, parent, true);

    // ratio: on/off
    BooleanFieldEditor nodeRatio = new BooleanFieldEditor(
        NodePreferencesIds.NODE_RATIO_ON, "Enable node ratio", parent);

    // highlight root nodes: on/off
    BooleanFieldEditor rootHighlight = new BooleanFieldEditor(
        NodePreferencesIds.NODE_ROOT_HIGHLIGHT_ON, "Enable root highlight", parent);

    // highlight selected nodes: on/off
    BooleanFieldEditor strokeHighlight = new BooleanFieldEditor(
        NodePreferencesIds.NODE_STROKE_HIGHLIGHT_ON, "Enable stroke highlight", parent);

    // add fields to the page
    addField(nodeColors);
    addField(colorSelect);
    addField(nodeShapes);
    addField(shapeSelect);
    addField(nodeSizes);
    addField(sizeSelect);
    addField(nodeRatio);
    addField(rootHighlight);
    addField(strokeHighlight);
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}
