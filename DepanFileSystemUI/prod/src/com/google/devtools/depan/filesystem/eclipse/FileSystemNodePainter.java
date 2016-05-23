/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.filesystem.eclipse;

import com.google.devtools.depan.filesystem.eclipse.preferences.ColorPreferencesIds;
import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemElementDispatcher;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.platform.Colors;

import com.google.common.base.Strings;

import org.eclipse.jface.preference.IPreferenceStore;

import java.awt.Color;

/**
 * Responsible for providing <code>Color</code>s for
 * {@link com.google.devtools.depan.filesystem.elements.FileSystemElement}s.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemNodePainter extends FileSystemElementDispatcher<Color>
    implements ElementTransformer<Color> {

  private static final IPreferenceStore prefs =
      FileSystemActivator.getDefault().getPreferenceStore();

  private static final Color getColor(String key) {
    String colorTxt = prefs.getString(key);
    if (Strings.isNullOrEmpty(colorTxt)) {
      return Color.BLACK;
    }

    return Colors.getRgb(colorTxt);
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final FileSystemNodePainter INSTANCE =
      new FileSystemNodePainter();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static FileSystemNodePainter getInstance() {
    return INSTANCE;
  }

  private FileSystemNodePainter() {
    // prevent instantiation by others
  }

  /**
   * Returns the <code>Color</code> for the given element.
   *
   * @param element The element whose associated <code>Color</code> is
   * requested.
   * @return <code>Color</code> associated with 
   * {@link com.google.devtools.depan.filesystem.elements.FileSystemElement}s.
   */
  @Override
  public Color match(FileElement element) {
    return getColor(ColorPreferencesIds.COLOR_FILE);
  }

  /**
   * Returns the <code>Color</code> for the given element.
   *
   * @param element The element whose associated <code>Color</code> is
   * requested.
   * @return <code>Color</code> associated with {@link DirectoryElement}s.
   */
  @Override
  public Color match(DirectoryElement element) {
    return getColor(ColorPreferencesIds.COLOR_DIRECTORY);
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
