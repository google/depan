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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import java.util.Collection;

/**
 * @author Lee Carver
 */
public interface ViewExtension {

  void deriveDetails(ViewEditor editor);

  void prepareView(ViewEditor editor);

  Collection<? extends NodeColorMode> getNodeColorModes();

  Collection<? extends NodeShapeMode> getNodeShapeModes();

  Collection<? extends NodeSizeMode> getNodeSizeModes();

  Collection<? extends NodeRatioMode> getNodeRatioModes();
}
