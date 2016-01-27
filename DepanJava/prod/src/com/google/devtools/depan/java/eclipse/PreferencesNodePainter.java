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

import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.eclipse.utils.Tools;
import com.google.devtools.depan.java.JavaResources;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.java.integration.JavaElementDispatcher;
import com.google.devtools.depan.model.Element;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import java.awt.Color;

/**
 * A Node Painter that gives the colors to nodes by looking at the saved
 * preferences.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class PreferencesNodePainter extends JavaElementDispatcher<Color>
    implements ElementTransformer<Color> {

  IEclipsePreferences preferences =
      InstanceScope.INSTANCE.getNode(JavaResources.PLUGIN_ID);
  IEclipsePreferences defaultsPrefs =
      DefaultScope.INSTANCE.getNode(JavaResources.PLUGIN_ID);

  private static final PreferencesNodePainter instance =
      new PreferencesNodePainter();


  public static PreferencesNodePainter getInstance() {
    return instance;
  }

  private Color getValue(String key) {
    return Tools.getRgb(preferences.get(key, defaultsPrefs.get(key, "0,0,0")));
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.bytecodevisitor.impl.ElementDispatcher
   *      #match(TypeElement)
   */
  @Override
  public Color match(TypeElement arg0) {
    return getValue(ColorPreferencesIds.COLOR_TYPE);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.bytecodevisitor.impl.ElementDispatcher
   *      #match(MethodElement)
   */
  @Override
  public Color match(MethodElement arg0) {
    return getValue(ColorPreferencesIds.COLOR_METHOD);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.bytecodevisitor.impl.ElementDispatcher
   *      #match(FieldElement)
   */
  @Override
  public Color match(FieldElement arg0) {
    return getValue(ColorPreferencesIds.COLOR_FIELD);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.bytecodevisitor.impl.ElementDispatcher
   *      #match(InterfaceElement)
   */
  @Override
  public Color match(InterfaceElement arg0) {
    return getValue(ColorPreferencesIds.COLOR_INTERFACE);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.bytecodevisitor.impl.ElementDispatcher
   *      #match(PackageElement)
   */
  @Override
  public Color match(PackageElement arg0) {
    return getValue(ColorPreferencesIds.COLOR_PACKAGE);
  }

  @Override
  public Color transform(Element element) {
    return this.match(element);
  }

}
