/*
 * Copyright 2018 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.visualization.ogl;

/**
 * @author Lee Carver
 *
 */
public class MessageBuilder {

  private static final String VALUE_SEPARATOR = "; ";

  private static final String LABEL_SEPARATOR = ": ";

  private final StringBuilder builder = new StringBuilder();

  public void fmtValue(String value) {
    builder.append(value);
  }

  public void fmtValue(String label, String value) {
    fmtSeparator();
    fmtLabel(label);
    builder.append(value);
  }

  public void fmtBoolean(boolean value, String forTrue, String forFalse) {
    fmtSeparator();
    if (value) {
      builder.append(forTrue);
    } else {
      builder.append(forFalse);
    }
  }

  public String build() {
    return builder.toString();
  }

  private void fmtSeparator() {
    if (builder.length() > 0) {
      builder.append(VALUE_SEPARATOR);
    }
  }

  private void fmtLabel(String label) {
    builder.append(label);
    builder.append(LABEL_SEPARATOR);
  }
}
