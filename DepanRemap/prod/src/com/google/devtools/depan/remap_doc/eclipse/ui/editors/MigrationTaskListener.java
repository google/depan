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
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface MigrationTaskListener {

  /**
   * Called when the MigrationTask is modified. Those modification only include
   * simple fields, but not MigrationGroup lists or MigrationRules.
   *
   * @param source Object source of the callback.
   */
  public void dataUpdated(Object source);

  /**
   * Called when a group is created, on deleted.
   *
   * @param source Object source of the modification.
   */
  public void groupsListUpdated(Object source);

  /**
   * Called when the given group is modified by the given source. Modifications
   * only includes MigrationGroup fields, and exclude MigrationRules.
   *
   * @param source Object source of the modification.
   * @param group {@link MigrationGroup} being modified.
   */
  public void groupUpdated(Object source, MigrationGroup group);

  /**
   * Called when the given group's rule list have been modified by the given
   * source object. This include add and delete operations on the list.
   *
   * @param source Object source of the modification.
   * @param group {@link MigrationGroup} modified.
   */
  public void ruleListUpdated(Object source, MigrationGroup group);

  /**
   * Called when the given {@link MigrationRule}, in the given
   * {@link MigrationGroup} have been modified by the given source object.
   *
   * @param source Object source of the modification.
   * @param group {@link MigrationGroup} containing the modified
   *        {@link MigrationRule}.
   * @param rule {@link MigrationRule} modified.
   */
  public void ruleUpdated(
      Object source, MigrationGroup group, MigrationRule<?> rule);
}
