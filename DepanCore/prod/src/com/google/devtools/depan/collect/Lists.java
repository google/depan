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
package com.google.devtools.depan.collect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Lists {

  /**
   * Create a new empty ArrayList.
   * 
   * @param <V> type of items permitted in list
   * @return list for designated type
   */
  public static <V> List<V> newArrayList() {
    return new ArrayList<V>();
  }

  /**
   * Create a new ArrayList that is populated with items
   * from the source iterable.
   * 
   * @param <V> type of items permitted in list
   * @param source an Iterable source of initial items for list
   * @return list for designated type
   */
  public static <V> List<V> newArrayList(Iterable<V> source) {
    List<V> result = newArrayList();
    for (V item : source) {
      result.add(item);
    }
    return result;
  }

  /**
   * Create a new ArrayList that is populated with items
   * from the iterator.
   * Not every Iterator is bundled inside an Iterable.
   * 
   * @param <V> type of items permitted in list
   * @param iterator an iterator that supplies the initial items for list
   * @return list for designated type
   */
  public static <V> List<V> newArrayList(Iterator<V> iterator) {
    List<V> result = newArrayList();
    while (iterator.hasNext()) {
      result.add(iterator.next());
    }
    return result;
  }

}
