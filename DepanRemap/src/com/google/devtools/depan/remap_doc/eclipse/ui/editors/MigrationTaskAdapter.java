/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.remap_doc.eclipse.ui.editors;

import com.google.devtools.depan.remap_doc.model.MigrationGroup;
import com.google.devtools.depan.remap_doc.model.MigrationRule;

/**
 * A default empty implementation for {@link MigrationTaskListener}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationTaskAdapter implements MigrationTaskListener {

  @Override
  public void dataUpdated(Object source) {
  }

  @Override
  public void groupUpdated(Object source, MigrationGroup group) {
  }

  @Override
  public void groupsListUpdated(Object source) {
  }

  @Override
  public void ruleListUpdated(Object source, MigrationGroup group) {
  }

  @Override
  public void ruleUpdated(
      Object source, MigrationGroup group, MigrationRule<?> rule) {
  }

}
