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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provide a ComboViewer dropdown control for a {@link Map} of values.
 * The keys from the map provide the labels, and 
 * 
 * @author Lee Carver
 */
public abstract class MapChoiceControl<T> extends Composite {

  private ComboViewer viewer;

  private ControlSelectionChangedListener listener;

  /**
   * Derived types must override this class to cast a proposed result to the
   * concrete type specified by {@code <T>}.  (Generic type erasure cannot
   * coerce instances to their concrete types.)
   */
  protected abstract T coerceResult(Object obj);

  public MapChoiceControl(
      Composite parent, Map<String, T> contributions) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    viewer = new ComboViewer(
        this, SWT.FLAT | SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE );
//      ListViewer flags: SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    viewer.setLabelProvider(new ControlLabelProvider());
    viewer.setContentProvider(new ControlContentProvider());
    viewer.setInput(contributions);

    listener = new ControlSelectionChangedListener();
    viewer.addSelectionChangedListener(listener);
  }

  @SuppressWarnings("unchecked")
  public T getChoice() {
    Map.Entry<String, T> choice = (Map.Entry<String, T>) listener.getChoice();
    if (null != choice) {
      return coerceResult(choice.getValue());
    }
    return null;
  }

  private class ControlLabelProvider extends LabelProvider {
    @SuppressWarnings("unchecked")
    @Override
    public String getText(Object element) {
      if (element instanceof Map.Entry<?, ?>) {
        Entry<String, T> item = (Map.Entry<String, T>) element;
        return item.getKey();
      }

      // Not what was expected.
      return null;
    }
  }

  private class ControlContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
      if (inputElement instanceof Map<?, ?>) {
        Map<String, T> input = (Map<String, T>) inputElement;
        List<Map.Entry<String, T>> result =
            Lists.newArrayListWithExpectedSize(input.size());
        result.addAll(input.entrySet());
        return result.toArray();
      }

      // Not what was expected.
      return null;
    }
  }

  private class ControlSelectionChangedListener
      implements ISelectionChangedListener {

    private Object choice;

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
      ISelection selection = event.getSelection();
      if (!(selection instanceof IStructuredSelection)) {
        choice = null;
      }

      IStructuredSelection select = (IStructuredSelection) selection;
      choice = select.getFirstElement();
    }

    public Object getChoice() {
      return choice;
    }
  }
}
