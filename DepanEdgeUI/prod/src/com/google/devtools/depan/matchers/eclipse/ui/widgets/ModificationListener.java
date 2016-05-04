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

package com.google.devtools.depan.matchers.eclipse.ui.widgets;


/**
 * A listener for modification made on a RelationshipPicker.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> type of elements in the list.
 * @param <V> type of elements that can change (might be Object if different
 *        objects types can change).
 */
public interface ModificationListener<E, V> {

  /**
   * Callback for a modification on the element E, when <code>property</code>,
   * is set to <code>value</code>.
   *
   * @param element the modified element
   * @param property the changed property
   * @param value the new value
   */
  public void modify(E element, String property, V value);
}
