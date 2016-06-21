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

package com.google.devtools.depan.platform;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Lazy content provider. Do not automatically update its content when data is
 * removed or added to the content provider. You must call one of the refresh
 * methods on your Viewer to see the changes.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> Content of the table.
 */
public class TableContentProvider<E> implements IStructuredContentProvider {

  /**
   * Set of objects to display in the table.
   */
  private Set<E> objects = new HashSet<E>();

  /**
   * Helper to initialize a {@link StructuredViewer} with <code>this</code>
   * {@link IStructuredContentProvider}.
   *
   * @param viewer the {@link StructuredViewer} to initialize.
   */
  public void initViewer(StructuredViewer viewer) {
    viewer.setContentProvider(this);
    viewer.setInput(objects);
  }

  public void setInput(Collection<E> elements) {
    clear();
    objects.addAll(elements);
  }

  /**
   * Add the given object to the set of objects. If the object is already
   * present, or if another object equals to it (with
   * {@link Object#equals(Object)}), the given object replace the old one.
   *
   * @param e the new object.
   */
  public void add(E e) {
    if (!objects.contains(e)) {
      objects.add(e);
    }
  }

  /**
   * Remove the given object from the set if it is present.
   *
   * @param e object to remove.
   */
  public void remove(E e) {
    if (objects.contains(e)) {
      objects.remove(e);
    }
  }

  /**
   * Empty the set of objects.
   */
  public void clear() {
    objects.clear();
  }

  /**
   * @return the set of objects in this content provider.
   */
  public Set<E> getObjects() {
    return objects;
  }

  @SuppressWarnings("unchecked")
  public E getElementAtIndex(int n) {
    if (n < objects.size()) {
      return (E) objects.toArray()[n];
    }
    return null;
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return objects.toArray();
  }

  @Override
  public void dispose() {
    objects = null;
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }

}
