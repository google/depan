/*
 * Copyright 2008 The Depan Project Authors
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

import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.RelationSetEditorPart;
import com.google.devtools.depan.eclipse.utils.RelationSetRelationTableEditor.RelationCheckedRepository;
import com.google.devtools.depan.eclipse.views.tools.RelationCount.RangeData;
import com.google.devtools.depan.eclipse.views.tools.RelationCount.RangeOption;
import com.google.devtools.depan.eclipse.views.tools.RelationCount.Settings;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.filters.RelationCountMatcher;
import com.google.devtools.depan.filters.RelationCountMatcher.EdgeCountPredicate;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.RelationSetDescriptor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Allow the user to select nodes based on a count of the forward (departing)
 * or reverse (arriving) edges.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationCountNodeSelectorTool extends Composite {

  /**
   * The <code>RelationSetEditorPart</code> object where users can select
   * relations in the list.
   */
  private RelationSetEditorPart relationSetEditor;

  /**
   * Source of much data, and the store for any changes.
   */
  private ViewEditor editor;

  private RangeTool forwardRange;
  private RangeTool reverseRange;

  public static final RelationCount.Settings EMPTY_SETTINGS =
      new RelationCount.Settings();

  private class ToolRelationRepo implements RelationCheckedRepository {
    @Override
    public boolean getRelationChecked(Relation relation) {
      if (null == editor) {
        return false;
      }

      return editor.isVisibleRelation(relation);
    }

    @Override
      public void setRelationChecked(Relation relation, boolean isChecked) {
      if (null == editor) {
        return;
      }
      editor.setVisibleRelation(relation, isChecked);
    }
  }

  public static Settings getEditorSettings(ViewEditor editor) {
    if (null == editor) {
      return EMPTY_SETTINGS;
    }
    return editor.getRelationCountData();
  }

  /**
   * Construct the UI for node selection.
   * @param parent container windows
   * @param style standard window style
   * @param settings initial set of choices
   */
  public RelationCountNodeSelectorTool(
      Composite parent, int style,
      ViewEditor editor) {
    super(parent, style);
    this.editor = editor;

    setLayout(new GridLayout());

    // Top: Relation selection
    relationSetEditor = new RelationSetEditorPart();
    Control relationshipPickerControl =
        relationSetEditor.getControl(this, new ToolRelationRepo());
    relationshipPickerControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // Bottom: Count selection
    RelationCount.Settings settings = getEditorSettings(editor);
    Composite rangeArea = new Composite(this, SWT.NONE);
    rangeArea.setLayout(new GridLayout(2, true));
    forwardRange = 
        new RangeTool(rangeArea, style, "Forward:", settings.forward);
    reverseRange =
        new RangeTool(rangeArea, style, "Reverse:", settings.reverse);
  }

  /**
   * Update all the UI values to the current set of settings.
   * 
   * @param settings bundle of values used to configure the UI
   */
  public void updateControls(RelationCount.Settings settings) {
    if (null == settings) {
      return;
    }

    forwardRange.setLimits(settings.forward);
    reverseRange.setLimits(settings.reverse);
  }

  /**
   * Update all the UI values to the settings for the current 
   * {@code ViewEditor}.
   * 
   * @param editor source of settings for UI configuration
   */
  public void updateControls(ViewEditor editor) {
    this.editor = editor;
    updateControls(getEditorSettings(editor));

    relationSetEditor.updateTable(editor.getBuiltinAnalysisPlugins());
    RelationSetDescriptor relationSet = editor.getDisplayRelationSet();
    java.util.List<RelationSetDescriptor> choices =
        editor.getRelationSetChoices();
    relationSetEditor.setRelationSetSelectorInput(relationSet, choices );
  }

  @Override
  public void dispose() {
    reverseRange.dispose();
    forwardRange.dispose();
    super.dispose();
  }

  /**
   * Configure a UI to allow the user which type of range tests
   * is appropriate.
   */
  private static class RangeTool extends Composite
      implements SelectionListener {

    private Combo rangeOp;

    private Label loLabel;
    private Spinner loLimit;

    private Label hiLabel;
    private Spinner hiLimit;

    /**
     * Explicitly define the sequence of {@code RangOption}s available in
     * the UI.
     */
    private static final RelationCount.RangeOption COMBO_DISPLAY[] = {
      RelationCount.RangeOption.EXACTLY,
      RelationCount.RangeOption.MORE_THAN,
      RelationCount.RangeOption.LESS_THAN,
      RelationCount.RangeOption.BETWEEN,
      RelationCount.RangeOption.OUTSIDE,
      RelationCount.RangeOption.IGNORE
    };

    /**
     * Construct the {@code RangeTool} UI with the given set of options.
     * 
     * @param parent containing window for range tool
     * @param style basic presentation options
     */
    public RangeTool(Composite parent, int style,
        String label, RelationCount.RangeData setup) {
      super(parent, style);

      setLayout(new RowLayout());

      Label rangeLabel = new Label(this, SWT.LEFT);
      rangeLabel.setText(label);

      rangeOp = createRangeOp(setup.option);
      loLabel = new Label(this, SWT.LEFT);
      loLimit = new Spinner(this, style);
      hiLabel = new Label(this, SWT.LEFT);
      hiLimit = new Spinner(this, style);
      setLimits(setup);
    }

    /**
     * Configure the UI elements to match the defined {@code RangeData}.
     */
    public void setLimits(RangeData limits) {
      rangeOp.select(getRangeIndex(limits.option));
      updateLabels(limits.option.loLabel, limits.option.hiLabel);
      loLimit.setMinimum(limits.loLimit);
      hiLimit.setMinimum(limits.hiLimit);
    }

    /**
     * Provide an edge count filter that corresponds to the UI's values.
     */
    public EdgeCountPredicate getIncludeTest() {
      RangeOption option = COMBO_DISPLAY[rangeOp.getSelectionIndex()];
      int loValue = parseLimit(loLimit.getText(), 0);
      int hiValue = parseLimit(hiLimit.getText(), loValue + 1);
      return option.getIncludeTest(loValue, hiValue);
    }

    /**
     * Convert the control's content into a number.
     * 
     * @param text value from control
     * @param onError value if text is not a number
     * @return {@code int} to use as control's value.
     */
    private int parseLimit(String text, int onError) {
      try {
        return Integer.parseInt(text);
      } catch (NumberFormatException e) {
        return onError;
      }
    }

    /**
     * Create the combo-box that defines the types of node count ranges
     * that are available.
     * 
     * @param setup initial selection in combo
     */
    private Combo createRangeOp(RelationCount.RangeOption setup) {
      rangeOp = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
      for (RelationCount.RangeOption option : COMBO_DISPLAY) {
        rangeOp.add(option.getRangeLabel());
      }
      rangeOp.addSelectionListener(this);
      rangeOp.select(getRangeIndex(setup));
      return rangeOp;
    }

    /**
     * Configure the labels present in UI.
     *
     * @param loText label for low value, or {@code null} if it is not
     *     available.
     * @param hiText label for high value, or {@code null} if it is not
     *     available.
     */
    private void updateLabels(String loText, String hiText) {
      if (null == loText) {
        loLabel.setText(getDefaultOption().getLoLabel());
        loLimit.setEnabled(false);
      } else {
        loLabel.setText(loText);
        loLimit.setEnabled(true);
      }

      if (null == hiText) {
        hiLabel.setText(getDefaultOption().getLoLabel());
        hiLimit.setEnabled(false);
      } else {
        hiLabel.setText(hiText);
        hiLimit.setEnabled(true);
      }
    }

    private static RelationCount.RangeOption getDefaultOption() {
      return RelationCount.RangeOption.BETWEEN;
    }

    /**
     * Convert a combo-box selection index into the corresponding
     * {@code RangeOption}.
     * 
     * @param index selection index from combo box
     * @return {@code RangeOption} for that index, or {@code null} if the
     *     index does not match a known {@code RangeOption}
     */
    private RelationCount.RangeOption getRangeOption(int index) {
      try {
        return COMBO_DISPLAY[index];
      } catch (ArrayIndexOutOfBoundsException errIndex) {
        return null;
      }
    }

    /**
     * Convert a {@code RangeOption} into the corresponding combo-box
     * selection index.
     * 
     * @param {@code RangeOption} to convert
     * @return combo-box index for the {@code RangeOption},
     *     or {@code -1} if it is not in the list of known {@code RangeOption}s
     */
    private int getRangeIndex(RelationCount.RangeOption toFind) {
      for (int index = 0; index < COMBO_DISPLAY.length; index++) {
        if (toFind == COMBO_DISPLAY[index]) {
          return index;
        }
      }
      return -1;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      RelationCount.RangeOption choice =
          getRangeOption(rangeOp.getSelectionIndex());
      updateLabels(choice.getLoLabel(), choice.getHiLabel());
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      widgetSelected(e);
    }
  }

  /**
   * Adaptor class that provides a UI "Part" which contains a 
   * {@code RelationCountSelectorTool}.  This allows simple integration with
   * the generic {@code SelectionEditorTool}.
   */
  public static class SelectorPart
      implements SelectionEditorTool.NodeSelectorPart {

    private RelationCountNodeSelectorTool selectorTool;

    @Override
    public Composite createControl(
        Composite parent, int style, ViewEditor viewEditor) {
      selectorTool = new RelationCountNodeSelectorTool(
          parent, style, viewEditor);
      return selectorTool;
    }

    @Override
    public PathMatcher getNodeSelector() {
      return new RelationCountMatcher(
          selectorTool.relationSetEditor.getSelectedRelationSet(),
          selectorTool.forwardRange.getIncludeTest(),
          selectorTool.reverseRange.getIncludeTest());
    }

    @Override
    public void updateControl(ViewEditor viewEditor) {
      selectorTool.updateControls(viewEditor);
    }
  }
}
