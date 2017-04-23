/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Standard platform for reusable {@code Control}s that edit
 * {@link LayoutPlan}s.  It creates a control with only an
 * embedded {@link BasicLayoutPlanEditorControl}.  This provides standard
 * editing for {@link LayoutPlan}'s name and summary properties.
 * 
 * Derived classes should implement the hook methods
 * {@link #setupControls(Composite)} and {@link #updateControls()} to add
 * editing features for specific types of filters.
 * 
 * If the editing features update the embedded {@link #plan} field
 * continuously, this {@link #buildLayout()} implementation is sufficient.
 * 
 * If the editing features maintain separate data, the derived class should
 * override the {@link #buildLayoutPlan()} method to create a suitable result
 * object, and call {@link #updateBasicFields(LayoutPlan)} before returning
 * the result. The {@link #plan} member can be the result, assuming all
 * updates have been properly applied.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class LayoutPlanEditorControl
    <T extends LayoutPlanDocument<? extends LayoutPlan>>
    extends Composite {

  private T plan;

  private IProject project;

  /////////////////////////////////////
  // UX Elements

  private final BasicLayoutPlanEditorControl basicControl;

  /////////////////////////////////////
  // Public methods

  /**
   * Prepares the instance with a single columnn
   * {@link Widgets#buildContainerLayout(int)} and an embedded
   * {@link BasicLayoutPlanEditorControl}. Derived types should add editing
   * control by implementing the {@link #setupControls(Composite)} hook method.
   */
  public LayoutPlanEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(1));

    // Handle common stuff, like name and summary
    basicControl = new BasicLayoutPlanEditorControl(this);
    basicControl.setLayoutData(Widgets.buildHorzFillData());

    setupControls(this);
  }

  /**
   * The supplied {@link #filter} is modified in place.
   * However, use the {@link #buildFilter()} method to ensure
   * all updates have been applied.
   */
  public void setInput(T plan, IProject project) {
    this.plan = plan;
    this.project = project;

    basicControl.setInput(plan);

    updateControls();
  }

  /**
   * Add additional controls to the filter editor.
   * 
   * The parent already contains a {@link BasicLayoutPlanEditorControl},
   * and is configured with a one column grid layout.
   */
  protected abstract void setupControls(Composite parent);

  /**
   * Update the added UX elements to the current set of
   * {@link #setInput(BasicFilter, DependencyModel, IProject)} values.
   */
  protected abstract void updateControls();

  /**
   * Provide the current state of the {@link #filter}, with all
   * pending user interface changes applied. 
   * 
   * Derived classes should override or extend as necessary.
   * 
   * If the control maintains the other state of the internal filter,
   * update the name and summary is sufficient to realize the result.
   */
  public T buildLayoutPlan() {
    T result = getLayoutPlan();
    updateBasicFields(result);
    return result;
  }

  protected void updateBasicFields(T plan) {
    plan.setSummary(basicControl.getLayoutPlanSummary());
  }

  /**
   * {@link #filter} access for derived types.
   */
  protected T getLayoutPlan() {
    return plan;
  }

  /**
   * {@link #project} access for derived types.
   */
  protected IProject getProject() {
    return project;
  }
}
