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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.wizards.NewEdgeMatcherWizard;
import com.google.devtools.depan.filters.EdgeMatcherPathMatcher;
import com.google.devtools.depan.filters.PathExpression;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.filters.PathMatcherTerm;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.model.GraphEdgeMatcherDescriptor;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;
import java.util.List;

/**
 * A GUI tool to display an edge matcher, with a selector for forward
 * and backward directions. This is a editable table (directions are editable,
 * content and names are not).
 *
 * To use it, call {@link #getControl(Composite)} to retrieve the widget.
 *
 * @since 2015 Built from pieces of the legacy RelationshipPicker.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherEditorPart {

  /////////////////////////////////////
  // UX Elements

  /**
   * The actual table.
   */
  private TableViewer tableViewer = null;

  private SelectionEditorTableEditor editor;

  /**
   * The quick selector on top of this widget applying a selection to the list
   * of relationships.
   */
  private GraphEdgeMatcherSelectorControl edgeMatcherSelector = null;

  /**
   * A shell necessary to open dialogs.
   */
  private Shell shell = null;

  /////////////////////////////////////
  // Listeners

  /**
   * Listeners for changes in the model.
   */
  private Collection<ModificationListener<Relation, Boolean>>
      listeners = Lists.newArrayList();

  /////////////////////////////////////

  /**
   * the currently selected {@link GraphEdgeMatcherDescriptor}.
   */
  private GraphEdgeMatcherDescriptor selectedEdgeMatcher = null;

  /**
   * The Path Matcher Model used for cumulative filtering.
   */
  private PathMatcher pathMatcherModel;

  /**
   * return a {@link Control} for this widget, containing every useful buttons,
   * labels, table... necessary to use this component.
   *
   * @param parent the parent.
   * @return a {@link Control} containing this widget.
   */
  public Control getControl(Composite parent) {
    this.shell = parent.getShell();

    // component
    Composite panel = new Composite(parent, SWT.BORDER);
    panel.setLayout(new GridLayout());

    // components inside the panel
    Composite pickerRegion = setupEdgeMatcherSelector(panel);
    pickerRegion.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Composite allRels = setupAllRelsButtons(panel);
    allRels.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    editor = new SelectionEditorTableEditor(
        new ModificationListener<Relation, Boolean>() {

          @Override
          public void modify(Relation relation, String property, Boolean value) {
            handleRelationModify(relation, property, value);
          }
        });

    tableViewer = editor.setupTableViewer(panel);
    tableViewer.getTable().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    Composite toggles = setupRelationToggles(panel);
    toggles.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    return panel;
  }

  private Composite setupEdgeMatcherSelector(Composite parent) {
    Composite region = new Composite(parent, SWT.NONE);
    region.setLayout(new GridLayout(3, false));

    Label edgeMatcherLabel = GraphEdgeMatcherSelectorControl.createEdgeMatcherLabel(region);
    edgeMatcherLabel.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));

    edgeMatcherSelector = new GraphEdgeMatcherSelectorControl(region);
    edgeMatcherSelector.addChangeListener(
        new GraphEdgeMatcherSelectorControl.SelectorListener() {

          @Override
          public void selectedEdgeMatcherChanged(GraphEdgeMatcherDescriptor edgeMatcher) {
            handleEdgeMatcherChanged(edgeMatcher);
          }
        });
    edgeMatcherSelector.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, true, false));

    Button save = new Button(region, SWT.PUSH);
    save.setText("Save selection as");
    save.setLayoutData(
        new GridData(SWT.FILL, SWT.CENTER, false, false));

    save.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        saveAsAction();
      }
    });

    return region;
  }

  private Composite setupAllRelsButtons(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    result.setLayout(new GridLayout(2, false));

    Button reverseAll = new Button(result, SWT.PUSH);
    reverseAll.setText("Reverse all lines");
    reverseAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button invertAll = new Button(result, SWT.PUSH);
    invertAll.setText("Invert all lines");
    invertAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    reverseAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.reverseRelations();
      }
    });

    invertAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertRelations();
      }
    });

    return result;
  }

  private Composite setupRelationToggles(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);
    GridLayout togglesLayout = new GridLayout(1, false);
    togglesLayout.verticalSpacing = 0;
    result.setLayout(togglesLayout);

    Label optionsLabel = new Label(result, SWT.NONE);
    optionsLabel.setText("For selected lines:");
    optionsLabel.setLayoutData(
        new GridData(SWT.LEFT, SWT.FILL, false, false));

    // Relation operations
    Composite group = new Composite(result, SWT.NONE);
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupReverse = new Button(group, SWT.PUSH);
    groupReverse.setText("Reverse");
    groupReverse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button groupInvert = new Button(group, SWT.PUSH);
    groupInvert.setText("Invert");
    groupInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    groupReverse.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.reverseSelectedRelations();
      }
    });
    groupInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertSelectedRelations();
      }
    });

    // Toggle operations
    Composite toggles = new Composite(result, SWT.NONE);
    toggles.setLayout(new GridLayout(8, false));
    toggles.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    Label forward = new Label(toggles, SWT.NONE);
    forward.setText("Forward");
    forward.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

    Button forwardAll = new Button(toggles, SWT.PUSH);
    forwardAll.setImage(Resources.IMAGE_ON);
    forwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardNone = new Button(toggles, SWT.PUSH);
    forwardNone.setImage(Resources.IMAGE_OFF);
    forwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button forwardInvert = new Button(toggles, SWT.PUSH);
    forwardInvert.setText("Invert");
    forwardInvert.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Label backward = new Label(toggles, SWT.NONE);
    backward.setText("Backward");
    backward.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

    Button backwardAll = new Button(toggles, SWT.PUSH);
    backwardAll.setImage(Resources.IMAGE_ON);
    backwardAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardNone = new Button(toggles, SWT.PUSH);
    backwardNone.setImage(Resources.IMAGE_OFF);
    backwardNone.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    Button backwardInvert = new Button(toggles, SWT.PUSH);
    backwardInvert.setText("Invert");
    backwardInvert.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, false));

    // actions
    forwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setForwardSelectedRelations(true);
      }
    });

    forwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setForwardSelectedRelations(false);
      }
    });

    forwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertForwardSelectedRelations();
      }
    });

    backwardAll.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setReverseSelectedRelations(true);
      }
    });

    backwardNone.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.setReverseSelectedRelations(false);
      }
    });

    backwardInvert.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        editor.invertReverseSelectedRelations();
      }
    });
    return result;
  }

  /**
   * Fill the list with {@link Relation}s.
   */
  public void updateTable(List<SourcePlugin> plugins) {
    editor.updateTable(plugins);
    tableViewer.refresh(false);
  }

  /**
   * Update the RelSetPicker with the current set of choices.
   */
  public void updateEdgeMatcherSelector(
      GraphEdgeMatcherDescriptor edgeMatcher,
      List<GraphEdgeMatcherDescriptor> choices) {
    edgeMatcherSelector.setInput(edgeMatcher, choices);
  }

  /**
   * @return a {@link MultipleDirectedRelationFinder} representing the selected
   *         relationships and their direction.
   */
  public GraphEdgeMatcherDescriptor getGraphEdgeMatcherDescriptor() {
    // TODO: If using named edge matcher, just return that.
    // TODO: If there was a named edge matcher, try to use that for name prefix

    String name = "ad hoc";
    GraphEdgeMatcher edgeMatcher = editor.createEdgeMatcher();

    return new GraphEdgeMatcherDescriptor(name, edgeMatcher);
  }

  /**
   * register a {@link ModificationListener}.
   * @param listener the new listener
   */
  public void registerListener(
      ModificationListener<Relation, Boolean> listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  /**
   * Unregister the listener.
   * @param listener to un-register
   */
  public void unRegisterListener(
      ModificationListener<Relation, Boolean> listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener);
    }
  }

  /**
   * Select the given {@link RelationshipSet}. Its definition will be reflected
   * on the view: only its enabled directions will be selected.
   * @param set the new set.
   */
  public void selectEdgeMatcher(GraphEdgeMatcherDescriptor edgeMatcher, boolean notify) {
    editor.updateEdgeMatcher(edgeMatcher);
    selectedEdgeMatcher = edgeMatcher;
  }

  /**
   * {@inheritDoc}
   * 
   * If the selected set is the instanceSet, this method doesn't notify
   * of the changes.
   */
  private void handleEdgeMatcherChanged(GraphEdgeMatcherDescriptor edgeMatcher) {
    boolean notify = (edgeMatcher != selectedEdgeMatcher);
    selectEdgeMatcher(edgeMatcher, notify);
  }

  public void handleRelationModify(Relation relation, String property, Boolean value) {
    notifyListeners(relation, property, value);

    // If the matching has changed, it's no longer the matches shown in
    // the edge matcher selector.
    edgeMatcherSelector.setSelection(null);
  }

  /**
   * Notify the listener when a change is made in the selection.
   *
   * @param element the element which changed
   * @param property the property involved
   * @param value the new value
   */
  private void notifyListeners(
      Relation element, String property, Boolean value) {
    for (ModificationListener<Relation, Boolean> listener : listeners) {
      listener.modify(element, property, value);
    }
  }

  public void saveAsAction() {
    GraphEdgeMatcher edgeMatcher = editor.createEdgeMatcher();
    NewEdgeMatcherWizard wizard = new NewEdgeMatcherWizard(edgeMatcher);
    WizardDialog dialog = new WizardDialog(shell, wizard);
    dialog.open();
  }

  /**
   * Creates a Path Matcher Model that will be used in conjunction to this tool.
   *
   * @param isRecursive Shows whether the selected relations should be applied
   * recursively.
   * @return 
   */
  public PathMatcher createPathMatcherModel(boolean isRecursive) {
    GraphEdgeMatcher edgeMatcher = editor.createEdgeMatcher();
    EdgeMatcherPathMatcher pathPatcher =
        new EdgeMatcherPathMatcher("ad hoc", edgeMatcher);
    PathExpression pathExpressionModel = new PathExpression();
    pathExpressionModel.addPathMatcher(
        new PathMatcherTerm(pathPatcher, isRecursive, false));
    pathMatcherModel = pathExpressionModel;
    return pathMatcherModel;
  }

  /**
   * Accessor for the <code>PathMatcher</code>.
   *
   * @return The <code>PathMatcher</code> model associated with this tool.
   */
  public PathMatcher getPathMatcherModel() {
    return pathMatcherModel;
  }
}
