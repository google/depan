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

package com.google.devtools.depan.java.eclipse;

import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.eclipse.visualization.ogl.ShapeFactory;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaElementDispatcher;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

public class JavaShapeTransformer extends JavaElementDispatcher<GLEntity>
    implements ElementTransformer<GLEntity> {
  private static final JavaShapeTransformer instance =
      new JavaShapeTransformer();

  public static JavaShapeTransformer getInstance() {
    return instance;
  }

  private JavaShapeTransformer() {
  }

  @Override
  public GLEntity transform(Element element) {
    return this.match(element);
  }

  @Override
  public GLEntity match(TypeElement e) {
    return ShapeFactory.createRegularPolygon(4);
  }

  @Override
  public GLEntity match(MethodElement e) {
    return ShapeFactory.createRoundedRectangle();
  }

  @Override
  public GLEntity match(FieldElement e) {
    return ShapeFactory.createEllipse();
  }

  @Override
  public GLEntity match(InterfaceElement e) {
    return ShapeFactory.createRegularPolygon(5);
  }

  @Override
  public GLEntity match(PackageElement e) {
    return ShapeFactory.createRegularStar(5);
  }
}
