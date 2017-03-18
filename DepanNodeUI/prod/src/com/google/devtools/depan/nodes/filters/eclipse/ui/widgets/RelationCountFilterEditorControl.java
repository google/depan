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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.graph.registry.RelationRegistry;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicate;
import com.google.devtools.depan.nodes.filters.sequence.RelationCountFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetSaveLoadControl;
import com.google.devtools.depan.relations.models.RelationSetDescrRepo;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * Enhances {@link RelationCountFilter} editing with
 * a {@link RelationSetEditorControl}
 * and forward and reverse {@link RangeTool}s.
 * 
 * Allow the user to select nodes based on a count of the forward (departing)
 * or reverse (arriving) edges.
 * 
 * Includes save-load support for the embedded collection
 * of {@link RelationSet}s (e.g. {@link RelationSetDescriptor}).
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationCountFilterEditorControl
    extends FilterEditorControl<RelationCountFilter> {

  public static final RelationCount.Settings EMPTY_SETTINGS =
      new RelationCount.Settings();

  /////////////////////////////////////
  // UX Elements

  private RelationSetEditorControl relationSetEditor;

  private RangeTool forwardRange;

  private RangeTool reverseRange;

  private RelationSetDescrRepo filterRepo;

  /////////////////////////////////////
  // Public methods

  /**
   * Construct the UI for node selection.
   * @param parent container windows
   * @param style standard window style
   * @param settings initial set of choices
   */
  public RelationCountFilterEditorControl(Composite parent) {
    super(parent);
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

  @Override
  public void dispose() {
    reverseRange.dispose();
    forwardRange.dispose();
    super.dispose();
  }

  /////////////////////////////////////
  // Control management

  @Override
  protected void updateControls() {

    List<String> contribs = getModel().getRelationContribs();
    Collection<Relation> projectRelations =
        RelationRegistry.getRegistryRelations(contribs);
    filterRepo = new RelationSetDescrRepo(projectRelations);
    filterRepo.setRelationSet(getFilter().getRelationSet());

    if (null != filterRepo.getUpdateSet()) {
      RelationSet relationSet = filterRepo.getRelationSet();
      getFilter().setRelationSet(relationSet);
    }

    relationSetEditor.setRelationSetRepository(filterRepo);
    relationSetEditor.selectRelations(projectRelations);
    relationSetEditor.setRelationSetSelectorInput(
        RelationSetResources.EMPTY_REF, getProject());

    // TODO: Configure ranges, too.
  }

  @Override
  protected void setupControls(Composite parent) {
    Composite setEditor = setupRelationSetEditor(parent);
    setEditor.setLayoutData(Widgets.buildGrabFillData());

    Composite rangeArea = setupRanges(parent, EMPTY_SETTINGS);
    rangeArea.setLayoutData(Widgets.buildHorzFillData());
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupRelationSetEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Relation Set", 1);

    Composite saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());

    relationSetEditor = new RelationSetEditorControl(result);
    relationSetEditor.setLayoutData(Widgets.buildGrabFillData());

    return result;
  }

  private Composite setupRanges(
      Composite parent, RelationCount.Settings settings) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, true));
    forwardRange = new RangeTool(
        result, SWT.NONE, "Forward:", settings.forward);
    reverseRange = new RangeTool(
        result, SWT.NONE, "Reverse:", settings.reverse);

    return result;
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
    public void setLimits(RelationCount.RangeData limits) {
      rangeOp.select(getRangeIndex(limits.option));
      updateLabels(limits.option.loLabel, limits.option.hiLabel);
      loLimit.setMinimum(limits.loLimit);
      hiLimit.setMinimum(limits.hiLimit);
    }

    /**
     * Provide an edge count filter that corresponds to the UI's values.
     */
    public CountPredicate getIncludeTest() {
      RelationCount.RangeOption option =
          COMBO_DISPLAY[rangeOp.getSelectionIndex()];
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

  /////////////////////////////////////
  // Integration classes

  /**
   * Connect the save/load control to this type's data structures.
   */
  private class ControlSaveLoadControl extends
      RelationSetSaveLoadControl {

    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected IProject getProject() {
      return RelationCountFilterEditorControl.this.getProject();
    }

    @Override
    protected RelationSetDescriptor buildSaveResource() {
      RelationCountFilter filter = buildFilter();
      String label = MessageFormat.format("{0} filter", filter.getName());
      return new RelationSetDescriptor(
          label, getModel(), filter.getRelationSet());
    }

    @Override
    protected void installLoadResource(
        PropertyDocumentReference<RelationSetDescriptor> ref) {
      if (null != ref) {
        RelationSet relSet = ref.getDocument().getInfo();
        filterRepo.setRelationSet(relSet);
      }
    }
  }
}
