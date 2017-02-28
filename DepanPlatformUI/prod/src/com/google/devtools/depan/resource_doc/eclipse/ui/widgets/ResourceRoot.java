/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.resource_doc.eclipse.ui.widgets;

import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.core.resources.IContainer;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ResourceRoot {
  public static final String DEFAULT_LABEL = "Resources";

  private final String label;
  private final ResourceContainer container;
  private final IContainer folder;

  public ResourceRoot(
      String label, ResourceContainer container, IContainer folder) {
    this.label = label;
    this.container = container;
    this.folder = folder;
  }

  public String getLabel() {
    return label;
  }

  public ResourceContainer getContainer() {
    return container;
  }

  public IContainer getFolder() {
    return folder;
  }
}
