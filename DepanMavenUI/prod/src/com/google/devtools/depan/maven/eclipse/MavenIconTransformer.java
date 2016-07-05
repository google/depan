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
import com.google.devtools.depan.maven.graph.MavenElementDispatcher;
import com.google.devtools.depan.maven.graph.PropertyElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Responsible for providing the correct {@link ImageDescriptor}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenIconTransformer
    extends MavenElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {
  /**
   * An instance of this class used by other classes.
   */
  private static final MavenIconTransformer INSTANCE =
      new MavenIconTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton  instance of this class.
   */
  public static MavenIconTransformer getInstance() {
    return INSTANCE;
  }

  private MavenIconTransformer() {
    // prevent instantiation by others
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with {@link FileElement}s.
   */
  @Override
  public ImageDescriptor match(ArtifactElement element) {
    return MavenActivator.IMAGE_DESC_MAVEN;
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with {@link FileElement}s.
   */
  @Override
  public ImageDescriptor match(PropertyElement element) {
    return MavenActivator.IMAGE_DESC_PROPERTY;
  }

  /**
   * Returns the {@link ImageDescriptor} for the given element.
   *
   * @param element The element whose associated {@link ImageDescriptor} is
   * requested.
   * @return {@link ImageDescriptor} associated with the given element.
   */
  @Override
  public ImageDescriptor transform(Element element) {
    return match(element);
  }
}
