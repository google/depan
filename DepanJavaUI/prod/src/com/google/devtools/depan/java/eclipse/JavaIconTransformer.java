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
import com.google.devtools.depan.java.graph.JavaElementDispatcher;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;

public class JavaIconTransformer extends JavaElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {

  private static final JavaIconTransformer instance = new JavaIconTransformer();

  public static JavaIconTransformer getInstance() {
    return instance;
  }

  private JavaIconTransformer() {
  }

  @Override
  public ImageDescriptor match(TypeElement e) {
    return JavaActivator.IMAGE_DESC_TYPE;
  }

  @Override
  public ImageDescriptor match(MethodElement e) {
    return JavaActivator.IMAGE_DESC_METHOD;
  }

  @Override
  public ImageDescriptor match(FieldElement e) {
    return JavaActivator.IMAGE_DESC_FIELD;
  }

  @Override
  public ImageDescriptor match(InterfaceElement e) {
    return JavaActivator.IMAGE_DESC_INTERFACE;
  }

  @Override
  public ImageDescriptor match(PackageElement e) {
    return JavaActivator.IMAGE_DESC_PACKAGE;
  }

  @Override
  public ImageDescriptor transform(Element element) {
    return this.match(element);
  }
}
