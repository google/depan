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

package com.google.devtools.depan.matchers.eclipse.ui.widgets;

import com.google.devtools.depan.platform.ListenerManager;
import com.google.devtools.depan.platform.ListenerManager.Dispatcher;
import com.google.devtools.depan.edge_ui.EdgeUILogger;
import com.google.devtools.depan.graph_doc.model.DependencyModel;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class EdgeMatcherMenuCreator implements IMenuCreator {

  // Set to get contextual matchers.
  private DependencyModel model;

  // Cleanup on dispose().
  private Menu menu;

  /////////////////////////////////////
  // Listeners for matcher selection

  private final ListenerManager<EdgeMatcherSelectionListener>
      matcherListeners = new ListenerManager<EdgeMatcherSelectionListener>();

  public void addSelectionListener(EdgeMatcherSelectionListener listener) {
    matcherListeners.addListener(listener);
  }

  public void removeSelectionListener(EdgeMatcherSelectionListener listener) {
    matcherListeners.removeListener(listener);
  }

  private void fireEdgeMatcherSelected(
      final GraphEdgeMatcherDescriptor selection) {

    matcherListeners.fireEvent(new Dispatcher<EdgeMatcherSelectionListener>() {

      @Override
      public void dispatch(EdgeMatcherSelectionListener listener) {
        listener.selected(selection);
      }

      @Override
      public void captureException(RuntimeException errAny) {
        EdgeUILogger.logException("Failed matcher selection", errAny);
      }
    });
  }

  /////////////////////////////////////
  // A single listener for selection events from the menu items
  private class MatcherExec extends SelectionAdapter {

    @Override
    public void widgetSelected(SelectionEvent e) {
      MenuItem source = (MenuItem) e.getSource();
      fireEdgeMatcherSelected((GraphEdgeMatcherDescriptor) source.getData());
    }
  }

  private final MatcherExec exec = new MatcherExec();

  /////////////////////////////////////
  // Public methods

  @Override
  public Menu getMenu(Menu parent) {
    return buildMenu(new Menu(parent));
  }

  @Override
  public Menu getMenu(Control parent) {
    return buildMenu(new Menu(parent));
  }

  @Override
  public void dispose() {
    if (null != menu) {
      menu.dispose();
      menu = null;
    }
  }

  public void setDependencyModel(DependencyModel model) {
    this.model = model;
  }

  private Menu buildMenu(Menu newMenu) {
    // Clean up any leftovers
    dispose();

    menu = newMenu;

    List<GraphEdgeMatcherDescriptor> matchers = GraphEdgeMatcherResources.getMatchers(model);
    if (matchers.isEmpty()) {
      return null;
    }

    for (GraphEdgeMatcherDescriptor matcher : matchers) {
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText(matcher.getName());
      item.setData(matcher);
      item.addSelectionListener(exec);
    }

    return menu;
  }
}