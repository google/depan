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

import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.ViewerObjectToString;

import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class MapChoiceControl<T> extends Composite {

  private static ControlLabelProvider CONTROL_LABEL_PROVIDER =
      new ControlLabelProvider();

  private final ComboViewer viewer;

  private ControlSelectionChangedListener listener;

  /**
   * Derived types must override this class to cast a proposed result to the
   * concrete type specified by {@code <T>}.  (Generic type erasure cannot
   * coerce instances to their concrete types.)
   */
  protected abstract T coerceResult(Object obj);

  public MapChoiceControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    viewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    viewer.setContentProvider(new ControlContentProvider());
    viewer.setLabelProvider(CONTROL_LABEL_PROVIDER);
    viewer.setComparator(new AlphabeticSorter(new ViewerObjectToString() {

        @Override
        public String getString(Object object) {
          return CONTROL_LABEL_PROVIDER.getText(object);
        }
      }));

    listener = new ControlSelectionChangedListener();
    viewer.addSelectionChangedListener(listener);
  }

  public void setInput(T selection, Map<String, T> contributions) {
    viewer.setInput(contributions);
    StructuredSelection selector = buildSelection(selection, contributions);
    setSelection(selector);
  }

  /**
   * Derived classes use this to implement type-specific selection.
   */
  protected void setSelection(StructuredSelection selector) {
    viewer.setSelection(selector);
  }

  protected StructuredSelection buildSelection(
      T selection, Map<String, ? extends T> contributions) {
    if (null == selection) {
      return null;
    }
    for (Entry<String, ? extends T> item : contributions.entrySet()) {
      if (selection == item.getValue()) {
        return(new StructuredSelection(item));
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public T getChoice() {
    Map.Entry<String, T> choice = (Map.Entry<String, T>) listener.getChoice();
    if (null != choice) {
      return coerceResult(choice.getValue());
    }
    return null;
  }

  private static class ControlLabelProvider extends LabelProvider {
    @SuppressWarnings("unchecked")
    @Override
    public String getText(Object element) {
      if (element instanceof Map.Entry<?, ?>) {
        Entry<String, ?> item = (Map.Entry<String, ?>) element;
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
      choice = Selections.getFirstElement(selection, Object.class);
    }

    public Object getChoice() {
      return choice;
    }
  }
}
