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

package com.google.devtools.depan.view_doc.model;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Lee Carver
 */
public class OptionPreferences {
  public static final String OPTION_DESCRIPTION = "option/description";

  public static final String ONLY_SELECTED_NODE_EDGES_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.only_selected_node_edges";

  // rendering options
  public static final String STROKEHIGHLIGHT_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.StrokeHighlight";
  public static final String COLOR_MODE_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.ColorMode";

  public static final String ROOTHIGHLIGHT_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.RootHighlight";
  public static final String STRETCHRATIO_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.StretchRatio";
  public static final String SIZE_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.Size";
  public static final String SHAPE_ID =
      "com.google.devtools.depan.view_doc.eclipse.ui.option.Shape";

  /**
   * Various options where the default is not a {@code null} {@link String}
   * (or a {@code false} {@link boolean}.
   */
  public static final Map<String, String> DEFAULT_OPTIONS =
      buildDefaultOptions();

  private OptionPreferences() {
    // Prevent instantiation.
  }

  private static Map<String, String> buildDefaultOptions() {
    Map<String, String> result = Maps.newHashMap();
    result.put(OPTION_DESCRIPTION, "");
    result.put(ONLY_SELECTED_NODE_EDGES_ID, Boolean.FALSE.toString());
    result.put(STROKEHIGHLIGHT_ID, Boolean.TRUE.toString());
    result.put(SHAPE_ID, Boolean.TRUE.toString());

    return result ;
  }

  public static OptionPreference getDefaultOptions() {
    OptionPreference result = new OptionPreference();
    return result;
  }

  public static boolean isOptionChecked(String optionId, String value) {
    if (null != value) {
      return Boolean.parseBoolean(value);
    }
    String result = DEFAULT_OPTIONS.get(optionId);
    if (null != result) {
      return Boolean.parseBoolean(result);
    }
    return false;
  }

  public static String booleanValue(boolean value) {
    return value ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
  }

  public static String notValue(boolean value) {
    return booleanValue(!value);
  }
}
