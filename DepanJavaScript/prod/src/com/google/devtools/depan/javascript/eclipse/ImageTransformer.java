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

import com.google.devtools.depan.java.JavaPluginActivator;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.javascript.integration.JavaScriptElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

/**
 * Responsible for returning the correct image for each given node.
 * In this implementation, all image resources are "borrowed" from the
 * Java graph elements plugin.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ImageTransformer extends JavaScriptElementDispatcher<Image>
    implements ElementTransformer<Image> {

  @Override
  public Image match(JavaScriptBuiltinElement builtinElement) {
    return JavaPluginActivator.IMAGE_FIELD;
  }

  @Override
  public Image match(JavaScriptClassElement classElement) {
    return JavaPluginActivator.IMAGE_TYPE;
  }

  @Override
  public Image match(JavaScriptEnumElement enumElement) {
    return JavaPluginActivator.IMAGE_TYPE;
  }

  @Override
  public Image match(JavaScriptFieldElement fieldElement) {
    return JavaPluginActivator.IMAGE_FIELD;
  }

  @Override
  public Image match(JavaScriptFunctionElement functionElement) {
    return JavaPluginActivator.IMAGE_METHOD;
  }

  @Override
  public Image match(JavaScriptVariableElement variableElement) {
    return JavaPluginActivator.IMAGE_FIELD;
  }

  @Override
  public Image transform(Element element) {
    return match(element);
  }
}
