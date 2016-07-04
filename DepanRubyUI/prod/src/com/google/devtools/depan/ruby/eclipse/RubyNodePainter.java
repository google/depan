/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.ruby.eclipse;

import com.google.devtools.depan.platform.Colors;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.ruby.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyElementDispatcher;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;

import com.google.common.base.Strings;

import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * Responsible for providing <code>Color</code>s for
 * {@link com.google.devtools.depan.filesystem.elements.FileSystemElement}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyNodePainter extends RubyElementDispatcher<Color>
    implements ElementTransformer<Color> {

  private static IPreferenceStore prefs =
      RubyActivator.getDefault().getPreferenceStore();

  private Color getColor(String key) {
    String colorTxt = prefs.getString(key);
    if (Strings.isNullOrEmpty(colorTxt)) {
      return Color.BLACK;
    }

    return Colors.getRgb(colorTxt);
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final RubyNodePainter INSTANCE =
      new RubyNodePainter();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static RubyNodePainter getInstance() {
    return INSTANCE;
  }

  private RubyNodePainter() {
    // prevent instantiation by others
  }

  /**
   * Returns the <code>Color</code> for the given element.
   */
  @Override
  public Color match(ClassElement element) {
    return getColor(ColorPreferencesIds.COLOR_CLASS);
  }


  @Override
  public Color match(ClassMethodElement element) {
    return getColor(ColorPreferencesIds.COLOR_CLASS_METHOD);
  }

  @Override
  public Color match(InstanceMethodElement element) {
    return getColor(ColorPreferencesIds.COLOR_INSTANCE_METHOD);
  }

  @Override
  public Color match(SingletonMethodElement element) {
    return getColor(ColorPreferencesIds.COLOR_SINGLETON_METHOD);
  }

  /**
   * Returns the <code>Color</code> for the given element.
   *
   * @param element The element whose associated <code>Color</code> is
   * requested.
   * @return <code>Color</code> associated with given {@link Element}.
   */
  @Override
  public Color transform(Element element) {
    return match(element);
  }
}
