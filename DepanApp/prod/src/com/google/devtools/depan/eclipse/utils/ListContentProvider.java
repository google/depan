/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.collect.Lists;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E>
 */
public class ListContentProvider<E> implements IStructuredContentProvider {

  private List<E> objects = Lists.newArrayList();
  private final AbstractListViewer viewer;

  public ListContentProvider(AbstractListViewer viewer) {
    this.viewer = viewer;
  }

  public void add(E e) {
    if (!objects.contains(e)) {
      objects.add(e);
      viewer.add(e);
    }
  }

  public void remove(E e) {
    if (objects.contains(e)) {
      objects.remove(e);
      viewer.remove(e);
    }
  }

  public void clear() {
    viewer.remove(objects.toArray());
    objects.clear();
  }

  public List<E> getObjects() {
    return objects;
  }

  public E getElementAtIndex(int index) {
    if (index >= objects.size()) {
      return null;
    }
    return objects.get(index);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider
   *      #getElements(java.lang.Object)
   */
  public Object[] getElements(Object inputElement) {
    return new Object[] { inputElement };
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  public void dispose() {
    objects = null;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider
   *      #inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   *      java.lang.Object)
   */
  public void inputChanged(
      Viewer structuredViewer, Object oldInput, Object newInput) {
  }

}
