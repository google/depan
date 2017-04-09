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

package com.google.devtools.depan.javascript;

import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptRelation;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;


/**
 * Configure XStreams that include JavaScript graph elements.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class JavaScriptXStreamConfig implements XStreamConfig {

  @Override
  public void config(XStream xstream) {
    xstream.alias("js-builtin", JavaScriptBuiltinElement.class);
    xstream.alias("js-class", JavaScriptClassElement.class);
    xstream.alias("js-enum", JavaScriptEnumElement.class);
    xstream.alias("js-field", JavaScriptFieldElement.class);
    xstream.alias("js-function", JavaScriptFunctionElement.class);
    xstream.alias("js-variable", JavaScriptVariableElement.class);
    xstream.alias("js-relation", JavaScriptRelation.class);
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.emptyList();
  }
}
