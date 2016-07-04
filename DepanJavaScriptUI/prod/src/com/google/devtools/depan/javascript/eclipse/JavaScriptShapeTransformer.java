/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.javascript.eclipse;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElementDispatcher;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

/**
 * Responsible for providing the correct shape for a given element type.
 * In this implementation, shapes for each node are "borrowed" from the
 * Java graph elements plugin.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptShapeTransformer
    extends JavaScriptElementDispatcher<GLEntity>
    implements ElementTransformer<GLEntity> {

  @Override
  public GLEntity match(JavaScriptBuiltinElement builtinElement) {
    return ShapeFactory.createEllipse();
  }

  @Override
  public GLEntity match(JavaScriptClassElement classElement) {
    return ShapeFactory.createRegularPolygon(4);
  }

  @Override
  public GLEntity match(JavaScriptEnumElement enumElement) {
    return ShapeFactory.createEllipse();
  }

  @Override
  public GLEntity match(JavaScriptFieldElement fieldElement) {
    return ShapeFactory.createEllipse();
  }

  @Override
  public GLEntity match(JavaScriptFunctionElement functionElement) {
    return ShapeFactory.createRoundedRectangle();
  }

  @Override
  public GLEntity match(JavaScriptVariableElement variableElement) {
    return ShapeFactory.createEllipse();
  }

  @Override
  public GLEntity transform(Element element) {
    return match(element);
  }
}
