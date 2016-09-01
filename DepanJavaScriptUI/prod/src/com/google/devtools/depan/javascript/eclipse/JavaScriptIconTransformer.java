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

import com.google.devtools.depan.java.eclipse.JavaActivator;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElementDispatcher;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Responsible for returning correct icon for a given element.
 * 
 * In this implementation, all image descriptors are "borrowed" from the
 * Java graph elements plugin.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptIconTransformer
    extends JavaScriptElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {

  private static final JavaScriptIconTransformer INSTANCE =
      new JavaScriptIconTransformer();

  private JavaScriptIconTransformer() {
    // Prevent instantiation by others.
  }

  public static JavaScriptIconTransformer getInstance() {
    return INSTANCE;
  }

  @Override
  public ImageDescriptor match(JavaScriptBuiltinElement builtinElement) {
    return JavaActivator.IMAGE_DESC_FIELD;
  }

  @Override
  public ImageDescriptor match(JavaScriptClassElement classElement) {
    return JavaActivator.IMAGE_DESC_TYPE;
  }

  @Override
  public ImageDescriptor match(JavaScriptEnumElement enumElement) {
    return JavaActivator.IMAGE_DESC_TYPE;
  }

  @Override
  public ImageDescriptor match(JavaScriptFieldElement fieldElement) {
    return JavaActivator.IMAGE_DESC_FIELD;
  }

  @Override
  public ImageDescriptor match(JavaScriptFunctionElement functionElement) {
    return JavaActivator.IMAGE_DESC_METHOD;
  }

  @Override
  public ImageDescriptor match(JavaScriptVariableElement variableElement) {
    return JavaActivator.IMAGE_DESC_FIELD;
  }

  @Override
  public ImageDescriptor transform(Element element) {
    return match(element);
  }
}
