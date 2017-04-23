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

import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.handlers.AbstractViewEditorHandler;
import com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets.LayoutPlanSaveLoadConfig;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class SelectLayoutNodes extends AbstractViewEditorHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ViewEditor editor = getViewEditor(event);

    PropertyDocumentReference<LayoutPlanDocument<LayoutPlan>> planDoc =
        LayoutPlanSaveLoadConfig.CONFIG.loadResource(
            editor.getEditorSite().getShell(), editor.getResourceProject());

    if (null != planDoc) {
       editor.applyLayout(planDoc.getDocument().getInfo());
     }

    return null;
  }
}
