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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.model.GraphNode;

/**
 * Provide the transformer that delivers the {@link GLEntity} shape to use
 * for an {@link Element}. Other {@link Element} features for
 * {@link GraphNode}s (e.g. color) are obtained from the {@code DepanNodeUI}
 * plugins.
 *
 * @author Yohann Coppel
 */
public class ElementTransformers {
  public final ElementTransformer<GLEntity> shape;

  public ElementTransformers(ElementTransformer<GLEntity> shape) {
    this.shape = shape;
  }
}
