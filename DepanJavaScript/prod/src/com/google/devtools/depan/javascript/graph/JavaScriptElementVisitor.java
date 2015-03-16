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

package com.google.devtools.depan.javascript.graph;

import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.ElementVisitor;

/**
 * Define the {@code visit()} methods that implement the Visitor pattern for
 * JavaScript dependency graph elements.  This allows the basic Visitor pattern
 * to be extended over new types of graph nodes.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public interface JavaScriptElementVisitor extends ElementVisitor {

  public void visitClass(JavaScriptBuiltinElement javaScriptBuiltinElement);
  public void visitClass(JavaScriptClassElement javaScriptClassElement);
  public void visitEnum(JavaScriptEnumElement javaScriptClassElement);
  public void visitField(JavaScriptFieldElement fieldElement);
  public void visitFunction(JavaScriptFunctionElement functionElement);
  public void visitVariable(JavaScriptVariableElement element);
}
