/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.ResourceContainer;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class SaveLoadConfig<T extends PropertyDocument<?>> {

  /**
   * Provide the container for the intended kind of resource.
   */
  public abstract ResourceContainer getContainer();

  /**
   * Provide an the intended kind of resource.
   */
  public abstract AbstractDocXmlPersist<T>
      getDocXmlPersist(boolean readable);

  public abstract String getSaveLabel();

  public abstract String getLoadLabel();

  public abstract String getExension();
}
