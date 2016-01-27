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

package com.google.devtools.depan.maven.eclipse;

import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.model.Element;

import java.awt.Color;

/**
 * Responsible for providing <code>Color</code>s for
 * {@link com.google.devtools.depan.filesystem.elements.FileSystemElement}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenNodePainter extends MavenElementDispatcher<Color>
    implements ElementTransformer<Color> {
  /**
   * An instance of this class used by other classes.
   */
  private static final MavenNodePainter INSTANCE =
      new MavenNodePainter();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton instance of this class.
   */
  public static MavenNodePainter getInstance() {
    return INSTANCE;
  }

  private MavenNodePainter() {
    // prevent instantiation by others
  }

  /**
   * Returns the <code>Color</code> for the given element.
   */
  @Override
  public Color match(ArtifactElement element) {
    // TODO: read from preferences
    return Color.BLUE;
  }

  /**
   * Returns the <code>Color</code> for the given element.
   */
  @Override
  public Color match(PropertyElement element) {
    // TODO: read from preferences
    return Color.GREEN;
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
