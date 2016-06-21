/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.RelationSets;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicate;
import com.google.devtools.depan.nodes.filters.sequence.CountPredicates;

/**
 * Define the various UI options and data structures for a relation count
 * selection test, and how the UI options map to the underlying selection
 * process.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class RelationCount {

  /** The relation count exactly equals a single value. */
  public static enum RangeOption {
    EXACTLY("Exactly", "equals", null) {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return new CountPredicates.IncludeEquals(loLimit);
      }
    },

    /** The relation count is strictly greater then a single value. */
    MORE_THAN("More than", null, "above") {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return new CountPredicates.IncludeAbove(hiLimit);
      }
    },

    /** The relation count is strictly less then a single value. */
    LESS_THAN("Less than", "below", null) {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return new CountPredicates.IncludeBelow(loLimit);
      }
    },

    /** The relation count is within the [closed, open) range. */
    BETWEEN("Between", "from", "to") {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return new CountPredicates.IncludeInRange(loLimit, hiLimit + 1);
      }
    },

    /** The relation count is outside the closed range [lo, hi]. */
    OUTSIDE("Outside", "below", "above") {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return new CountPredicates.IncludeOutside(loLimit, hiLimit);
      }
    },

    /** Regardless of relation count, don't include the node. */
    IGNORE("Ignore", null, null) {
      @Override
      public CountPredicate getIncludeTest(int loLimit, int hiLimit) {
        return null;
      }
    };

    private final String rangeLabel;
    final String loLabel;
    final String hiLabel;

    /**
     * @param label
     */
    private RangeOption(String rangeLabel, String loLabel, String hiLabel) {
      this.rangeLabel = rangeLabel;
      this.loLabel = loLabel;
      this.hiLabel = hiLabel;
    }

    public String getRangeLabel() {
      return rangeLabel;
    }

    public String getLoLabel() {
      return loLabel;
    }

    public String getHiLabel() {
      return hiLabel;
    }

    public abstract CountPredicate getIncludeTest(
        int loLimit, int hiLimit);
  }

  /**
   * Define a single range choice from the UI: the kind of range test, and
   * its parameters.
   * 
   * This implements a "public record" style of data encapsulation, and
   * users are permitted to directly manipulate the public fields.
   */
  public static class RangeData {
    public RangeOption option;
    public int loLimit;
    public int hiLimit;

    public RangeData(RangeOption option, int loLimit, int hiLimit) {
      this.option = option;
      this.loLimit = loLimit;
      this.hiLimit = hiLimit;
    }
  }

  /**
   * Define the full set of options for a counted relation selection.
   * This includes the relation set of interest, and forward and reverse
   * count ranges for those edges.
   */
  public static class Settings {
    public RelationSet relationSet;
    public RangeData forward;
    public RangeData reverse;

    public Settings() {
      relationSet = RelationSets.EMPTY;
      forward = new RangeData(RangeOption.EXACTLY, 0, 0);
      reverse = new RangeData(RangeOption.IGNORE, 0, 0);
    }
  }
}
