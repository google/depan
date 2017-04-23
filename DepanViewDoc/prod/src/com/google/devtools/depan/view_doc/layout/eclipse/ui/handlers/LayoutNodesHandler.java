/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.eclipse.ui.handlers;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.handlers.AbstractViewEditorHandler;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;
import com.google.devtools.depan.view_doc.layout.persistence.LayoutResources;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutNodesHandler extends AbstractViewEditorHandler {

  public static final String LAYOUT_COMMAND =
      "com.google.devtools.depan.view_doc.eclipse.ui.command.LayoutNodes";

  public static final String TOP_NAME_KEY =
      "com.google.devtools.depan.view_doc.eclipse.ui.command.LayoutNodes.topName"
      + "";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ViewEditor editor = getViewEditor(event);
    String layoutName = event.getParameter(TOP_NAME_KEY);
    Object layoutRsrc = LayoutResources.getContainer().getResource(layoutName);
    LayoutPlanDocument<? extends LayoutPlan> layoutDoc =
        LayoutResources.getLayoutDocument(layoutRsrc);
    editor.applyLayout(layoutDoc.getInfo());
    return null;
  }

  public static Map<String, String> buildParameters(String planId) {
    return Collections.singletonMap(TOP_NAME_KEY, planId);
  }
}
