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

package com.google.devtools.depan.nodes.filters.sequence;


/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CountPredicates {

  private CountPredicates() {
    // Prevent instantiation.
  }

  /**
   * Provides an [closed, open) range-inclusion test for node counts.
   */
  public static class IncludeInRange implements CountPredicate {
    private final int loLimit;
    private final int hiLimit;

    public IncludeInRange(int loLimit, int hiLimit) {
      this.loLimit = loLimit;
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return (value >= loLimit) && (value < hiLimit);
    }
  }

  /**
   * Provides a strictly outside test for defined range. 
   */
  public static class IncludeOutside implements CountPredicate {
    private final int loLimit;
    private final int hiLimit;

    public IncludeOutside(int loLimit, int hiLimit) {
      this.loLimit = loLimit;
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return (value < loLimit) || (value > hiLimit);
    }
  }

  /**
   * Provides a strictly above test for the defined value.
   */
  public static class IncludeAbove implements CountPredicate {
    private final int loLimit;

    public IncludeAbove(int loLimit) {
      this.loLimit = loLimit;
    }

    @Override
    public boolean include(Integer value) {
      return value > loLimit;
    }
  }

  /**
   * Provides a strictly below test for the defined value.
   */
  public static class IncludeBelow implements CountPredicate {
    private final int hiLimit;

    public IncludeBelow(int hiLimit) {
      this.hiLimit = hiLimit;
    }

    @Override
    public boolean include(Integer value) {
      return value < hiLimit;
    }
  }

  /**
   * Provides a strict equals test for the defined value.
   */
  public static class IncludeEquals implements CountPredicate {
    private final int target;

    public IncludeEquals(int target) {
      this.target = target;
    }

    @Override
    public boolean include(Integer value) {
      return value == target;
    }
  }
}
