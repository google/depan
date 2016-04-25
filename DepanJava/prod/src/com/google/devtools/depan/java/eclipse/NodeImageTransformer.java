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

import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.java.integration.JavaElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

public class NodeImageTransformer extends JavaElementDispatcher<Image> implements
    ElementTransformer<Image> {
  private static final NodeImageTransformer instance =
      new NodeImageTransformer();

  public static NodeImageTransformer getInstance() {
    return instance;
  }

  private NodeImageTransformer() {
  }

  @Override
  public Image match(TypeElement e) {
    return JavaActivator.IMAGE_TYPE;
  }

  @Override
  public Image match(MethodElement e) {
    return JavaActivator.IMAGE_METHOD;
  }

  @Override
  public Image match(FieldElement e) {
    return JavaActivator.IMAGE_FIELD;
  }

  @Override
  public Image match(InterfaceElement e) {
    return com.google.devtools.depan.eclipse.utils.Resources.IMAGE_DEFAULT;
  }

  @Override
  public Image match(PackageElement e) {
    return com.google.devtools.depan.eclipse.utils.Resources.IMAGE_DEFAULT;
  }

  @Override
  public Image transform(Element element) {
    return this.match(element);
  }

}
