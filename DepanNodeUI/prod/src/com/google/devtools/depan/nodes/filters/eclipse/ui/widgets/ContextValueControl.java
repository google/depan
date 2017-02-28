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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.platform.AlphabeticSorter;
import com.google.devtools.depan.platform.ViewerObjectToString;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Selections;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Allow the user to choice among several keys for context values.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ContextValueControl extends Composite {

  private final ComboViewer viewer;

  private ControlSelectionChangedListener listener;

  public ContextValueControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    viewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(CONTROL_LABEL_PROVIDER);
    viewer.setComparator(new AlphabeticSorter(new ViewerObjectToString() {

        @Override
        public String getString(Object object) {
          return CONTROL_LABEL_PROVIDER.getText(object);
        }
      }));

    viewer.setInput(ContextKey.Base.values());

    listener = new ControlSelectionChangedListener();
    viewer.addSelectionChangedListener(listener);
  }

  private static ControlLabelProvider CONTROL_LABEL_PROVIDER =
      new ControlLabelProvider();

  private static class ControlLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {
      if (element instanceof ContextKey) {
        ContextKey item = (ContextKey) element;
        return item.getLabel();
      }

      // Not what was expected.
      return null;
    }
  }

  public void setClosureContext(ContextKey key) {
    viewer.setSelection(new StructuredSelection(key));
  }

  public ContextKey getClosureContext() {
    return listener.getChoice();
  }

  private class ControlSelectionChangedListener
      implements ISelectionChangedListener {

    private ContextKey choice;

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
      ISelection selection = event.getSelection();
      choice = Selections.getFirstElement(selection, ContextKey.class);
    }

    public ContextKey getChoice() {
      return choice;
    }
  }
}
