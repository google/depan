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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for dealing with Eclipse selection entities.
 * 
 * These methods allow the type {@link IStructuredSelection} to be hidden
 * in many UX support methods.
 * 
 * @author Lee Carver
 */
public class Selections {

  /**
   * Provide a type correct collection based on the supplied selection
   * and type. Selection objects of a non-assignable type are silently
   * ignored.
   */
  public static <T> Collection<T> getSelection(
      ISelection selection, Class<T> type) {
    // TODO: Randomize the order ..
    return getSelectionList(selection, type);
  }

  /**
   * Use only when it is essential to have the elements in order.
   */
  public static <T> List<T> getSelectionList(
      ISelection selection, Class<T> type) {
    List<?> choices = getObjects(selection);
    if (choices.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> result =
        Lists.newArrayListWithExpectedSize(choices.size());
    for (Object item : choices) {
      if (type.isAssignableFrom(item.getClass())) {
        result.add(type.cast(item));
      }
    }
    return result;
  }

  /**
   * Use when type-coercion occurs late in the process flow.
   */
  public static List<?> getObjects(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return Collections.emptyList();
    }
    return ((IStructuredSelection) selection).toList();
  }

  /**
   * Provide a type correct instance based on the supplied selection
   * and type. Selection objects of a non-assignable type are silently
   * ignored.
   */
  public static <T> T getFirstElement(
      ISelection selection, Class<T> type) {
    Object result = getFirstObject(selection);
    if ((result != null) && type.isAssignableFrom(result.getClass())) {
      return type.cast(result);
    }
    return null;
  }

  /**
   * Use when type-coercion occurs late in the process flow.
   */
  public static Object getFirstObject(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return null;
    }
    IStructuredSelection choices = ((IStructuredSelection) selection);
    if (choices.isEmpty()) {
      return null;
    }
    return choices.getFirstElement();
  }
}
