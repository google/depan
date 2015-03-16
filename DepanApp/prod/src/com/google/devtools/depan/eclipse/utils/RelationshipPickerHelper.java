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

package com.google.devtools.depan.eclipse.utils;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class RelationshipPickerHelper {

  /**
   * this is a namespace class. private constructor prevents instanciation.
   */
  private RelationshipPickerHelper() { }

  // some strings used in the UI.
  public static final String MODE_EXTENDS = "Extend graph";
  public static final String MODE_SELECTED = "Selected + New nodes";
  public static final String MODE_NEW = "Only new nodes";
  public static final String LAYOUT_KEEP = "Keep positions";

  public static final String COL_RELATION = "Relation";
  public static final String COL_FORWARD = "Forward";
  public static final String COL_BACKWARD = "Backward";

  public static final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_RELATION, false, COL_RELATION, 150),
    new EditColTableDef(COL_FORWARD, true, COL_FORWARD, 140),
    new EditColTableDef(COL_BACKWARD, true, COL_BACKWARD, 80)
  };

  public static final String[] CHANGING_COLS = new String[] {
      RelationshipPickerHelper.COL_BACKWARD,
      RelationshipPickerHelper.COL_FORWARD};

}
