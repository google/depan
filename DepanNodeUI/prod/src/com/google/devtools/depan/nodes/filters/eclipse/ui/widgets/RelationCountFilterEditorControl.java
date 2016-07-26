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
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicate;
import com.google.devtools.depan.nodes.filters.sequence.RelationCountFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetEditorControl;
import com.google.devtools.depan.relations.eclipse.ui.widgets.RelationSetSaveLoadControl;
import com.google.devtools.depan.relations.eclipse.ui.wizards.NewRelationSetWizard;
import com.google.devtools.depan.relations.models.RelationSetDescrRepo;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptors;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.analysis.AnalysisResources;

import com.google.common.collect.Lists;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * Allow the user to select nodes based on a count of the forward (departing)
 * or reverse (arriving) edges.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationCountFilterEditorControl extends Composite {

  public static final RelationCount.Settings EMPTY_SETTINGS =
      new RelationCount.Settings();

  private RelationCountFilter filterInfo;

  /////////////////////////////////////
  // UX Elements

  private BasicFilterEditorControl basicControl;

  private RelationSetEditorControl relationSetEditor;

  private RangeTool forwardRange;

  private RangeTool reverseRange;

  private RelationSetDescrRepo filterRepo;

  private DependencyModel model;

  /**
   * Connect the save/load control to this type's data structures.
   */
  private class ControlSaveLoadControl extends
      RelationSetSaveLoadControl {

    private ControlSaveLoadControl(Composite parent) {
      super(parent);
    }

    @Override
    protected Wizard getSaveWizard() {
      RelationSet relSet = filterRepo.getRelationSet();
      String label = MessageFormat.format(
          "{0} filter", basicControl.getFilterName());
      RelationSetDescriptor target = new RelationSetDescriptor(
          label, model, relSet);
      return new NewRelationSetWizard(target);
    }

    @Override
    protected void loadURI(URI uri) {
      RelationSetDescriptor loadDoc = loadRelationSetDescr(uri);
      filterRepo.setRelationSet(loadDoc.getInfo());
    }
  }

  /////////////////////////////////////
  // Public methods

  /**
   * Construct the UI for node selection.
   * @param parent container windows
   * @param style standard window style
   * @param settings initial set of choices
   */
  public RelationCountFilterEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Handle common stuff, like name and summary
    basicControl = new BasicFilterEditorControl(this);
    basicControl.setLayoutData(Widgets.buildHorzFillData());

    Composite setEditor = setupRelationSetEditor(this);
    setEditor.setLayoutData(Widgets.buildGrabFillData());

    Composite rangeArea = setupRanges(this, EMPTY_SETTINGS);
    rangeArea.setLayoutData(Widgets.buildHorzFillData());
  }

  public void setInput(RelationCountFilter filterInfo, DependencyModel model) {
    this.filterInfo = filterInfo;
    this.model = model;

    basicControl.setInput(filterInfo);

    Collection<Relation> projectRelations = 
        RelationRegistry.getRegistryRelations(model.getRelationContribs());
    filterRepo = new RelationSetDescrRepo(projectRelations);
    filterRepo.setRelationSet(filterInfo.getRelationSet());

    relationSetEditor.setRelationSetRepository(filterRepo);
    relationSetEditor.selectRelations(projectRelations);

    // TODO: Configure ranges, too.
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
  public void updateControls() {

    relationSetEditor.setRelationSetRepository(filterRepo);

    RelationSetDescriptor relationSet = RelationSetDescriptors.EMPTY;
    List<RelationSetDescriptor> choices = getRelationSetsChoices();
    relationSetEditor.setRelationSetSelectorInput(relationSet, choices);
  }

  public List<RelationSetDescriptor> getRelationSetsChoices() {
    ResourceContainer tree =
        AnalysisResources.getRoot().getChild(AnalysisResources.RELATION_SETS);

    List<RelationSetDescriptor> result = Lists.newArrayList();
    for (Object resource : tree.getResources()) {
        if (resource instanceof RelationSetDescriptor) {
          RelationSetDescriptor checkRes = (RelationSetDescriptor) resource;
          if (checkRes.forModel(model)) {
            result.add(checkRes);
          }
        }
      }

    return result;
  }

  @Override
  public void dispose() {
    reverseRange.dispose();
    forwardRange.dispose();
    super.dispose();
  }

  /////////////////////////////////////
  // UX Setup

  private Composite setupRelationSetEditor(Composite parent) {
    Composite result = Widgets.buildGridGroup(parent, "Relation Set", 1);

    relationSetEditor = new RelationSetEditorControl(result);
    relationSetEditor.setLayoutData(Widgets.buildGrabFillData());

    Composite saves = new ControlSaveLoadControl(result);
    saves.setLayoutData(Widgets.buildHorzFillData());

    return result;
  }

  public RelationCountFilter getFilter() {
    if (null != filterRepo.getUpdateSet()) {
      RelationSet relationSet = filterRepo.getRelationSet();
      filterInfo.setRelationSet(relationSet);
    }
    return filterInfo;
  }

  private Composite setupRanges(
      Composite parent, RelationCount.Settings settings) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, true));
    forwardRange = 
        new RangeTool(result, SWT.NONE, "Forward:", settings.forward);
    reverseRange =
        new RangeTool(result, SWT.NONE, "Reverse:", settings.reverse);

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
}
