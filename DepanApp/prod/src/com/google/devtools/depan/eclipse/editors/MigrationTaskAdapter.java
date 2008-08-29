/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.tasks.MigrationGroup;
import com.google.devtools.depan.tasks.MigrationRule;

/**
 * A default empty implementation for {@link MigrationTaskListener}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MigrationTaskAdapter implements MigrationTaskListener {

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #dataUpdated()
   */
  public void dataUpdated(Object source) {
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #groupUpdated(com.google.devtools.depan.tasks.MigrationGroup)
   */
  public void groupUpdated(Object source, MigrationGroup group) {
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #groupsListUpdated()
   */
  public void groupsListUpdated(Object source) {
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #ruleListUpdated(com.google.devtools.depan.tasks.MigrationGroup)
   */
  public void ruleListUpdated(Object source, MigrationGroup group) {
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.editors.MigrationTaskListener
   *      #ruleUpdated(java.lang.Object,
   *      com.google.devtools.depan.tasks.MigrationGroup,
   *      com.google.devtools.depan.tasks.MigrationRule)
   */
  public void ruleUpdated(
      Object source, MigrationGroup group, MigrationRule<?> rule) {
  }

}
