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

import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.java.JavaResources;
import com.google.devtools.depan.java.eclipse.ColorPreferencesIds;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.javascript.integration.JavaScriptElementDispatcher;
import com.google.devtools.depan.model.Element;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import java.awt.Color;

/**
 * Used only when the node color scheme is role-based.
 * In this implementation, the node colors are "borrowed" from the
 * Java graph elements plugin.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class NodePainter extends JavaScriptElementDispatcher<Color>
    implements ElementTransformer<Color> {

  // Steal the Java object preferences
  private final IEclipsePreferences preferences =
      new InstanceScope().getNode(JavaResources.PLUGIN_ID);
  private final IEclipsePreferences defaultsPrefs =
      new DefaultScope().getNode(JavaResources.PLUGIN_ID);

  private Color getValue(String key) {
    return Tools.getRgb(preferences.get(key, defaultsPrefs.get(key, "0,0,0")));
  }

  @Override
  public Color match(JavaScriptBuiltinElement builtinElement) {
    return getValue(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(JavaScriptClassElement classElement) {
    return getValue(ColorPreferencesIds.COLOR_TYPE);
  }

  @Override
  public Color match(JavaScriptEnumElement enumElement) {
    return getValue(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(JavaScriptFieldElement fieldElement) {
    return getValue(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(JavaScriptFunctionElement functionElement) {
    return getValue(ColorPreferencesIds.COLOR_METHOD);
  }

  @Override
  public Color match(JavaScriptVariableElement variableElement) {
    return getValue(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color transform(Element element) {
    return match(element);
  }
}
