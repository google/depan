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

import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.maven.graph.MavenElement;
import com.google.devtools.depan.maven.graph.MavenElementDispatcher;
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.swt.graphics.Image;

/**
 * Responsible for providing the correct {@link Image}.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class MavenCategoryTransformer
    extends MavenElementDispatcher<Integer>
    implements ElementTransformer<Integer> {

  public static final int CATEGORY_ARTIFACT = 3000;
  public static final int CATEGORY_PROPERTY = 3001;

  /**
   * Returns the lowest category value for all elements in Maven Plug-in.
   * This system must be replaced by a smarter system. Two plug-ins that use
   * overlapping constants would cause problems.
   *
   * @return The lowest value of category constants.
   */
  public static int getLowestCategory() {
    return CATEGORY_ARTIFACT;
  }

  /**
   * An instance of this class used by other classes.
   */
  private static final MavenCategoryTransformer INSTANCE =
      new MavenCategoryTransformer();

  /**
   * Returns the singleton instance of this class.
   */
  public static MavenCategoryTransformer getInstance() {
    return INSTANCE;
  }

  /**
   * Provides the category for the supplied {@link #element}.
   */
  public static int getCategory(MavenElement element) {
    return getInstance().match(element);
  }

  private MavenCategoryTransformer() {
    // Prevent instantiation by others.
  }

  @Override
  public Integer transform(Element element) {
    return match(element);
  }

  @Override
  public Integer match(ArtifactElement element) {
    return CATEGORY_ARTIFACT;
  }

  @Override
  public Integer match(PropertyElement element) {
    return CATEGORY_PROPERTY;
  }
}
