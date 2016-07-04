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


import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.ruby.graph.ClassElement;
import com.google.devtools.depan.ruby.graph.ClassMethodElement;
import com.google.devtools.depan.ruby.graph.InstanceMethodElement;
import com.google.devtools.depan.ruby.graph.RubyElementDispatcher;
import com.google.devtools.depan.ruby.graph.SingletonMethodElement;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Responsible for providing the correct {@link ImageDescriptor}.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyIconTransformer
    extends RubyElementDispatcher<ImageDescriptor>
    implements ElementTransformer<ImageDescriptor> {
  /**
   * An instance of this class used by other classes.
   */
  private static final RubyIconTransformer INSTANCE =
      new RubyIconTransformer();

  /**
   * Returns the singleton instance of this class.
   *
   * @return The singleton  instance of this class.
   */
  public static RubyIconTransformer getInstance() {
    return INSTANCE;
  }

  private RubyIconTransformer() {
    // prevent instantiation by others
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

  @Override
  public ImageDescriptor match(ClassElement element) {
    return RubyActivator.IMAGE_DESC_CLASS;
  }

  @Override
  public ImageDescriptor match(ClassMethodElement element) {
     return RubyActivator.IMAGE_DESC_CLASS_METHOD;
  }

  @Override
  public ImageDescriptor match(InstanceMethodElement element) {
    return RubyActivator.IMAGE_DESC_INSTANCE_METHOD;
  }

  @Override
  public ImageDescriptor match(SingletonMethodElement element) {
    return RubyActivator.IMAGE_DESC_SINGLETON_METHOD;
  }
}
