/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.persistence;

import com.google.devtools.depan.resources.ResourceContainer;

import com.thoughtworks.xstream.converters.UnmarshallingContext;

import org.eclipse.core.resources.IContainer;

/**
 * Define access methods for persistence data saved in the
 * {@link UnmarshallingContext}.
 * 
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class PropertyDocumentReferenceContext {

  private enum CONTEXT_KEY {
    PROJECT, RESOURCE_ROOT
  }

  private PropertyDocumentReferenceContext() {
    // Prevent instantiation.
  }

  public static void setProjectSource(
      UnmarshallingContext context, IContainer proj) {
    context.put(CONTEXT_KEY.PROJECT, proj);
  }

  public static IContainer getProjectSource(UnmarshallingContext context) {
    return (IContainer) context.get(CONTEXT_KEY.PROJECT);
  }

  public static void setResourceRoot(
      UnmarshallingContext context, ResourceContainer proj) {
    context.put(CONTEXT_KEY.RESOURCE_ROOT, proj);
  }

  public static ResourceContainer getResourceRoot(
      UnmarshallingContext context) {
    return (ResourceContainer) context.get(CONTEXT_KEY.RESOURCE_ROOT);
  }
}
