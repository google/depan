/*
 * Copyright 2007 Google Inc.
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

import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.java.JavaElementDispatcher;
import com.google.devtools.depan.java.elements.FieldElement;
import com.google.devtools.depan.java.elements.InterfaceElement;
import com.google.devtools.depan.java.elements.MethodElement;
import com.google.devtools.depan.java.elements.PackageElement;
import com.google.devtools.depan.java.elements.TypeElement;
import com.google.devtools.depan.model.Element;

import org.eclipse.jface.resource.ImageDescriptor;

public class NodeIconTransformer extends JavaElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {

  private static final NodeIconTransformer instance = new NodeIconTransformer();

  public static NodeIconTransformer getInstance() {
    return instance;
  }

  private NodeIconTransformer() {
  }

  @Override
  public ImageDescriptor match(TypeElement e) {
    return Resources.IMAGE_DESC_TYPE;
  }

  @Override
  public ImageDescriptor match(MethodElement e) {
    return Resources.IMAGE_DESC_METHOD;
  }

  @Override
  public ImageDescriptor match(FieldElement e) {
    return Resources.IMAGE_DESC_FIELD;
  }

  @Override
  public ImageDescriptor match(InterfaceElement e) {
    return com.google.devtools.depan.eclipse.utils.Resources.IMAGE_DESC_DEFAULT;
  }

  @Override
  public ImageDescriptor match(PackageElement e) {
    return com.google.devtools.depan.eclipse.utils.Resources.IMAGE_DESC_DEFAULT;
  }

  @Override
  public ImageDescriptor transform(Element element) {
    return this.match(element);
  }

}
