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

import com.google.devtools.depan.java.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaElementDispatcher;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.platform.Colors;

import com.google.common.base.Strings;

import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * A Node Painter that gives the colors to nodes by looking at the saved
 * preferences.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class JavaNodePainter extends JavaElementDispatcher<Color>
    implements ElementTransformer<Color> {

  private static IPreferenceStore prefs =
      JavaActivator.getDefault().getPreferenceStore();

  private Color getColor(String key) {
    String colorTxt = prefs.getString(key);
    if (Strings.isNullOrEmpty(colorTxt)) {
      return Color.BLACK;
    }

    return Colors.getRgb(colorTxt);
  }

  private static final JavaNodePainter INSTANCE =
      new JavaNodePainter();

  public static JavaNodePainter getInstance() {
    return INSTANCE;
  }

  @Override
  public Color match(TypeElement arg0) {
    return getColor(ColorPreferencesIds.COLOR_TYPE);
  }

  @Override
  public Color match(MethodElement arg0) {
    return getColor(ColorPreferencesIds.COLOR_METHOD);
  }

  @Override
  public Color match(FieldElement arg0) {
    return getColor(ColorPreferencesIds.COLOR_FIELD);
  }

  @Override
  public Color match(InterfaceElement arg0) {
    return getColor(ColorPreferencesIds.COLOR_INTERFACE);
  }

  @Override
  public Color match(PackageElement arg0) {
    return getColor(ColorPreferencesIds.COLOR_PACKAGE);
  }

  @Override
  public Color transform(Element element) {
    return this.match(element);
  }

}
