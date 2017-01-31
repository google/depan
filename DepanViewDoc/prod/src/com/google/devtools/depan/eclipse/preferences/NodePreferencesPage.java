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
package com.google.devtools.depan.eclipse.preferences;

import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtensionRegistry;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

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

    // color: mode
    RadioGroupFieldEditor colorSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_COLOR, "...shows...", 3,
        buildNodeColorModeOptions(), parent, true);

    // shape: mode
    RadioGroupFieldEditor shapeSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_SHAPE, "...shows...", 2, 
        buildNodeShapeModeOptions(), parent, true);

    // size: mode
    RadioGroupFieldEditor sizeSelect = new RadioGroupFieldEditor(
        NodePreferencesIds.NODE_SIZE, "...shows...", 2,
        buildNodeSizeModeOptions(), parent, true);

    // add fields to the page
    addField(colorSelect);
    addField(shapeSelect);
    addField(sizeSelect);
  }

  private static Comparator<String[]> LABEL_ORDER =
      new Comparator<String[]>() {

    @Override
    public int compare(String[] arg0, String[] arg1) {
      return (arg0[0].compareTo(arg1[0]));
    }
  };

  private String[][] buildNodeColorModeOptions() {
    Collection<NodeColorMode> modes =
        ViewExtensionRegistry.getRegistryNodeColorModes();
    String[][] result = new String[modes.size()][];
    int curr = 0;
    for (NodeColorMode mode : modes) {
      result[curr] = new String[] { mode.getLabel(), mode.getLabel() };
      curr++;
    }
    Arrays.sort(result, LABEL_ORDER);
    return result;
  }

  private String[][] buildNodeShapeModeOptions() {
    Collection<NodeShapeMode> modes =
        ViewExtensionRegistry.getRegistryNodeShapeModes();
    String[][] result = new String[modes.size()][];
    int curr = 0;
    for (NodeShapeMode mode : modes) {
      result[curr] = new String[] { mode.getLabel(), mode.getLabel() };
      curr++;
    }
    Arrays.sort(result, LABEL_ORDER);
    return result;
  }

  private String[][] buildNodeSizeModeOptions() {
    Collection<NodeSizeMode> modes =
        ViewExtensionRegistry.getRegistryNodeSizeModes();
    String[][] result = new String[modes.size()][];
    int curr = 0;
    for (NodeSizeMode mode : modes) {
      result[curr] = new String[] { mode.getLabel(), mode.getLabel() };
      curr++;
    }
    Arrays.sort(result, LABEL_ORDER);
    return result;
  }

  @Override
  public void init(IWorkbench workbench) {
  }
}
