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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.analysis_doc.model.FeatureMatcher;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.model.ContextualFilterDocument;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.GenericSaveLoadControl;
import com.google.devtools.depan.resources.PropertyDocumentReference;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;

/**
 * Implement a SaveLoadControl based on data from a
 * {@link ContextualFilterSaveLoadConfig} descriptor object.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class ContextualFilterSaveLoadControl<T extends BasicFilter>
    extends GenericSaveLoadControl<ContextualFilterDocument> {

  public ContextualFilterSaveLoadControl(Composite parent) {
    super(parent, ContextualFilterSaveLoadConfig.CONFIG);
  }

  /////////////////////////////////////
  // Provided hook methods

  @Override
  protected abstract IProject getProject();

  protected abstract ContextualFilter buildFilter();

  protected abstract DependencyModel getModel();

  protected abstract FilterEditorControl<T> getEditor();

  /////////////////////////////////////
  // Supplied hook implementations

  @Override
  protected ContextualFilterDocument buildSaveResource() {
    FeatureMatcher matcher = new FeatureMatcher(getModel());
    ContextualFilterDocument result =
        new ContextualFilterDocument(matcher, buildFilter());
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void installLoadResource(
      PropertyDocumentReference<ContextualFilterDocument> ref) {
    if (null != ref) {
      ContextualFilter filter = ref.getDocument().getInfo();
      getEditor().setInput((T) filter, getModel(), getProject());
    }
  }
}
