/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.util;

import java.awt.Color;

/**
 * Some string utilities for joining list of strings.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class StringUtils {

  /**
   * private constructor to prevent instantiation.
   */
  private StringUtils() {}

  /**
   * join the list l of string with the given join.
   * if l = {"A","B"}, and join = ".", result will be "A.B"
   *
   * @param join string to insert between each element on the list
   * @param l array of {@link String}s to join
   * @return a {@link String} representing all element of l separated by join
   */
  public static String join(String join, String... l) {
    return join(join, 0, l.length, l);
  }

  /**
   * join the list l of {@link String} with the join String, starting at point
   * from.
   * if l = {"A","B","C"}, join = "." and from = 1, result will be "B.C"
   *
   * @param join {@link String} to insert between each element in the array.
   * @param from index of first {@link String} to join in the array.
   * @param l array of {@link String}s to join
   * @return the elements of l from from to l.length, separated by join.
   */
  public static String join(String join, int from, String... l) {
    return join(join, from, l.length - from, l);
  }

  /**
   * Join <code>length</code> elements starting from <code>from</code>, of
   * <code>l</code>, separated with <code>join</code>.
   *
   * @param join String separator
   * @param from first index to join
   * @param length number of elements to join.
   * @param l array of {@link String}s to join
   * @return <code>length</code> elements of <code>l</code> from
   *         <code>from</code> to l.length, separated by join.
   */
  public static String join(String join, int from, int length, String... l) {
    if (l.length < from) return "";
    if (l.length == 1) return l[0];

    int to = Math.min(length + from, l.length - 1);

    StringBuilder b = new StringBuilder();
    for (int i = from; i < to; ++i) {
      if (l[i].length() > 0) {
        b.append(l[i]);
        b.append(join);
      }
    }
    b.append(l[to]);
    return b.toString();
  }

  /**
   * Converts the <code>String</code> representation of a color to an actual
   * <code>Color</code> object.
   *
   * @param value String representation of the color in "r,g,b" format (e.g.
   * "100,255,0")
   * @return <code>Color</code> object that matches the red-green-blue values
   * provided by the parameter. Returns <code>null</code> for empty string.
   */
  public static Color stringToColor(String value) {
    try {
      if (!value.equals("")) {
        String[] s = value.split(",");
        if (s.length == 3) {
          int red = Integer.parseInt(s[0]);
          int green = Integer.parseInt(s[1]);
          int blue = Integer.parseInt(s[2]);
          return new Color(red, green, blue);
        }
      }
    } catch (NumberFormatException ex) {
      // ignore it, don't change anything.
      return null;
    } catch (IllegalArgumentException ex) {
      // if a user entered 548 as the red value....
      // ignore it, don't change anything.
      return null;
    }
    return null;
  }
}
