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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.ViewDocument;

import com.google.common.base.Strings;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import java.text.MessageFormat;
import java.util.Collection;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class FromViewDocWizard extends Wizard {

  private ViewEditor editor;
  private Collection<GraphNode> nodes;
  private String detail;

  public void init(
      ViewEditor editor, Collection<GraphNode> nodes, String detail) {
    this.editor = editor;
    this.nodes = nodes;
    this.detail = detail;
  }

  protected IProject getResourceProject() {
    return editor.getResourceProject();
  }

  protected GraphResources getGraphResources() {
    return editor.getGraphResources();
  }

  protected ViewDocument buildNewViewDocument() {
    return editor.buildNewViewDocument(nodes);
  }

  protected String calcName() {
    if (Strings.isNullOrEmpty(detail)) {
      return MessageFormat.format("{0}_filtered", editor.getBaseName());
    }
    return MessageFormat.format("{0}_{1}", editor.getBaseName(), detail);
  }
}
