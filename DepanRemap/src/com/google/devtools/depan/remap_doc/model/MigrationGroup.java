/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.remap_doc.model;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * A {@link MigrationGroup} is basically a set of {@link MigrationRule}s. A
 * {@link MigrationGroup} can have dependencies on other
 * {@link MigrationGroup}s.
 * 
 * @author leeca@google.com (Lee Carver)
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class MigrationGroup {
  
  /**
   * This {@link MigrationGroup}'s name.
   */
  private String name;
  
  /**
   * Set of {@link MigrationRule}s in this group.
   */
  private final Collection<MigrationRule<?>> rules =
      Lists.newArrayList();

  /**
   * Set of {@link MigrationGroup}s that must be completed before this
   * MigrationGroup. (if a remap rule already implies another remap to be done.)
   */
  private final Collection<MigrationGroup> mustBeCompletedBefore =
      Lists.newArrayList();
  
  /**
   * Add the given {@link MigrationRule} to the list of rules for this
   * {@link MigrationGroup}.
   * 
   * @param rule the {@link MigrationRule} to add.
   */
  public void addMigrationRule(MigrationRule<?> rule) {
    rules.add(rule);
  }

  /**
   * @return the set of {@link MigrationRule}s.
   */
  public Collection<MigrationRule<?>> getMigrationRules() {
    return rules;
  }

  public void addMigrationGroupBefore(MigrationGroup group) {
    if (mustBeCompletedBefore.contains(group)) {
      return;
    }
    mustBeCompletedBefore.add(group);
  }

  public void removeMigrationGoupBefore(MigrationGroup group) {
    if (!mustBeCompletedBefore.contains(group)) {
      return;
    }
    mustBeCompletedBefore.remove(group);
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return "MG " + name;
  }
}
