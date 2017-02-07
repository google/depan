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

package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;

/**
 * @author Lee Carver
 */
public class AbstractExtensionData implements ExtensionData {

  private final ViewExtension extension;
  private final Object instance;

  public AbstractExtensionData(ViewExtension extension, Object instance) {
    this.extension = extension;
    this.instance = instance;
  }

  @Override
  public ViewExtension getExtension() {
    return extension;
  }

  @Override
  public Object getInstance() {
    return instance;
  }
}
