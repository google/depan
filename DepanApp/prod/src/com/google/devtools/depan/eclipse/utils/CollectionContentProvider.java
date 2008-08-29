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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.Collection;

/**
 * A {@link IStructuredContentProvider} that only reflect the content of
 * a given collection.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E> the type of elements in the collection.
 */
public class CollectionContentProvider<E>
    implements IStructuredContentProvider {

  private final Collection<E> collection;

  /**
   * Create a new {@link CollectionContentProvider} with the given collection.
   *
   * @param collection the collection this ContentProvider must expose.
   */
  public CollectionContentProvider(Collection<E> collection) {
    this.collection = collection;
  }

  /**
   * Helper to create a {@link CollectionContentProvider} using parameter type
   * inference.
   *
   * @param <F> Type of the collection.
   * @param collection the collection.
   * @return a new {@link CollectionContentProvider} reflecting the elements in
   *         the {@link Collection}.
   */
  public static <F> CollectionContentProvider<F>
      newProvider(Collection<F> collection) {
    return new CollectionContentProvider<F>(collection);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IStructuredContentProvider
   *      #getElements(java.lang.Object)
   */
  public Object[] getElements(Object inputElement) {
    return collection.toArray();
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.viewers.IContentProvider#dispose()
   */
  public void dispose() {
    // noop
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.jface.viewers.IContentProvider
   *      #inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
   *      java.lang.Object)
   */
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // noop
  }

}
