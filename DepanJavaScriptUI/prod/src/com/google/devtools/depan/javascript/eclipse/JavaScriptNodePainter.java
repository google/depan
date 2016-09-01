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

import com.google.devtools.depan.javascript.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.javascript.graph.JavaScriptBuiltinElement;
import com.google.devtools.depan.javascript.graph.JavaScriptClassElement;
import com.google.devtools.depan.javascript.graph.JavaScriptElementDispatcher;
import com.google.devtools.depan.javascript.graph.JavaScriptEnumElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFieldElement;
import com.google.devtools.depan.javascript.graph.JavaScriptFunctionElement;
import com.google.devtools.depan.javascript.graph.JavaScriptVariableElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.platform.Colors;

import com.google.common.base.Strings;

import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * Used only when the node color scheme is role-based.
 * In this implementation, the node colors are "borrowed" from the
 * Java graph elements plugin.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptNodePainter extends JavaScriptElementDispatcher<Color>
    implements ElementTransformer<Color> {

  private static IPreferenceStore prefs =
      JavaScriptActivator.getDefault().getPreferenceStore();

  private Color getColor(String key) {
    String colorTxt = prefs.getString(key);
    if (Strings.isNullOrEmpty(colorTxt)) {
      return Color.BLACK;
    }

    return Colors.getRgb(colorTxt);
  }

  private static final JavaScriptNodePainter INSTANCE =
      new JavaScriptNodePainter();

  private JavaScriptNodePainter() {
    // Prevent instantiation by others.
  }

  public static JavaScriptNodePainter getInstance() {
    return INSTANCE;
  }

  @Override
  public Color match(JavaScriptBuiltinElement builtinElement) {
    return getColor(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(JavaScriptClassElement classElement) {
    return getColor(ColorPreferencesIds.COLOR_CLASS);
  }

  @Override
  public Color match(JavaScriptEnumElement enumElement) {
    return getColor(ColorPreferencesIds.COLOR_ENUM);
  }

  @Override
  public Color match(JavaScriptFieldElement fieldElement) {
    return getColor(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(JavaScriptFunctionElement functionElement) {
    return getColor(ColorPreferencesIds.COLOR_FUNCTION);
  }

  @Override
  public Color match(JavaScriptVariableElement variableElement) {
    return getColor(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color transform(Element element) {
    return match(element);
  }
}
