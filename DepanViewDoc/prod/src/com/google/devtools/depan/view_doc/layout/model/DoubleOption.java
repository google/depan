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

package com.google.devtools.depan.view_doc.layout.model;

public interface DoubleOption extends ValueOption {

  public abstract double getValue();

  public static class Unset implements DoubleOption {
    private Unset() {
      // Prevent external instantiations.
    }

    @Override
    public boolean isSet() {
      return false;
    }

    @Override
    public double getValue() {
      throw new UnsupportedOperationException();
    }
  }

  public static final DoubleOption UNSET_DOUBLE = new Unset();

  public static class Value implements DoubleOption {
    private final double value;

    public Value(double value) {
      this.value = value;
    }

    @Override
    public boolean isSet() {
      return true;
    }

    @Override
    public double getValue() {
      return value;
    }
  }
}
