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

package com.google.devtools.depan.nodes.filters.eclipse.ui.plugins;

import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.eclipse.ui.widgets.FilterEditorDialog;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;
import java.util.List;

/**
 * Provides labels, {@link Form}s, factories, and dialog editors
 * for {@link ContextualFilter}s.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface ContextualFilterContributor <T extends ContextualFilter> {

  /**
   * Generalized model for composable {@link ContextualFilter}s.
   * This could be inferred from the derivation of the concrete class
   * {@link #getContributionClass()}, this effectively captures the common
   * UX question of whether there is delegate, group, sequence,
   * or no internal {@link ContextualFilter}.
   */
  public enum Form {
    ELEMENT,
    WRAPPER,
    GROUP,
    STEPS
  }

  String getLabel();

  Form getForm();

  T createElementFilter();

  T createWrapperFilter(ContextualFilter filter);

  T createGroupFilter(Collection<ContextualFilter> filters);

  T createStepsFilter(List<ContextualFilter> filters);

  boolean handlesFilterInstance(ContextualFilter filter);

  FilterEditorDialog<T> buildEditorDialog(
      Shell shell, ContextualFilter filter,
      DependencyModel model, IProject project);
}
