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

package com.google.devtools.depan.eclipse.views;

import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.utils.ListeningViewViewPart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import java.util.Map;

/**
 * A ViewPart for embedding different tools.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class Tools extends ListeningViewViewPart<ViewEditor> {
  public static final String VIEW_ID =
      "com.google.devtools.depan.eclipse.views.Tools";

  private Composite panel;
  private StackLayout stackLayout;
  private Tool activeTool = null;

  private Map<Tool, Control> guis = Maps.newHashMap();

  private class ToolSelection extends SelectionAdapter {
    private final Tool tool;

    ToolSelection(Tool tool) {
      this.tool = tool;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
      setTool(tool);
    }
  }

  public Tools() {
    super(ViewEditor.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.utils.ListeningViewViewPart
   *      #createGui()
   */
  @Override
  public void createGui(Composite parent) {
    GridLayout gridLayout = new GridLayout();
    parent.setLayout(gridLayout);

    // top composite with buttons
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    RowLayout layout = new RowLayout();
    layout.fill = true;
    layout.pack = false;
    layout.wrap = true;
    layout.type = SWT.HORIZONTAL;
    composite.setLayout(layout);

    // bottom composite with options for each tool.
    panel = new Composite(parent, SWT.NONE);
    panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    stackLayout = new StackLayout();
    panel.setLayout(stackLayout);

    guis.put(null, new Composite(panel, SWT.NONE));

    for (Tool tool : ToolList.tools) {
      // Create the tool activation button
      Button toolBtn = new Button(composite, SWT.PUSH);
      toolBtn.setToolTipText(tool.getName());
      toolBtn.addSelectionListener(new ToolSelection(tool));
      toolBtn.setImage(tool.getIcon());

      // Create the tool's GUI options
      Composite toolPanel = buildToolPanel(tool);
      Control control = tool.setupComposite(toolPanel);
      if (null != control) {
        // spans 2 columns, since there are the icon and name on 2 different
        // columns in the toolPanel.
        control.setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        guis.put(tool, toolPanel);
      } else {
        // if no control, we still need to put the Tool into the list
        guis.put(tool, null);
      }

      // After GUI creation, tell the tool which editor it should listen to.
      // Need to set the editor on creation if an editor is active.
      tool.setEditor(getAcceptableEditor());
    }
  }

  /**
   * Create a Composite with a gridLayout using 2 columns. On the first row,
   * there are the icon and a label with the tool's name. Then, we can
   * add the tool options spanning those two columns.
   *
   * @param parent parent Composite
   * @param icon icon for the tool
   * @param name tool's name
   * @return a composite with a gridLayout using 2 columns.
   */
  private Composite buildToolPanel(Tool tool) {
    Composite toolPanel = new Composite(panel, SWT.NONE);
    Label image = new Label(toolPanel, SWT.NONE);
    image.setImage(tool.getIcon());
    Label title = new Label(toolPanel, SWT.NONE);
    title.setText(tool.getName());

    toolPanel.setLayout(new GridLayout(2, false));
    return toolPanel;
  }

  // TODO: Reconsider this implementation with tool.selected() and
  // tool.setEditor().  The selected() method plays to an early notion
  // of panel-less tools, which are pretty rare.  Also, it would be
  // nice to setEditor() only on active tools, and only when they are
  // activated.  NodeEditorTool, for example, has fairly heavyweight
  // costs for setEditor() [tree construction].  It would be nice to
  // pay those cost on demand.
  public void setTool(Tool tool) {
    if (null == guis.get(tool)) {
      // if this tool has no panel, we just active it so it can do its
      // operations, but unselect it just after, because it has no more
      // things to do.
      // we don't unselect the previously selected tool, because we still want
      // it to be activated when the selected tool finished its operations.
      tool.selected(true);
      tool.selected(false);
      return;
    }
    // if the tool has a panel...
    // unselect the previously selected one if any
    if (null != activeTool) {
      activeTool.selected(false);
    }
    // select the new one
    tool.selected(true);
    activeTool = tool;
    // display its options
    stackLayout.topControl = guis.get(tool);

    // relayout the panel to reflect the changes.
    panel.layout();
  }

  @Override
  protected boolean newEditorCallback(ViewEditor e) {
    for (Tool t : guis.keySet()) {
      if (null != t) {
        t.setEditor(e);
      }
    }
    return true;
  }

  @Override
  protected boolean closeEditorCallback(ViewEditor e) {
    for (Tool t : guis.keySet()) {
      if (null != t) {
        t.editorClosed(e);
      }
    }
    return true;
  }

}
