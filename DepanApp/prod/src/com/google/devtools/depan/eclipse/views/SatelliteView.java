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

import com.google.devtools.depan.eclipse.editors.ViewEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class SatelliteView extends ViewPart {

  public static final String ID =
    "com.google.devtools.depan.eclipse.views.SatelliteView";

  private java.awt.Frame frame;

  private ViewEditor editor = null;

  @Override
  public void createPartControl(Composite parent) {
    Composite swtAwtComponent = new Composite(parent, SWT.EMBEDDED);
    swtAwtComponent.setLayout(new GridLayout(1, false));
    swtAwtComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    frame = SWT_AWT.new_Frame(swtAwtComponent);

    getViewSite().getPage().addPartListener(partListener);
  }

  @Override
  public void setFocus() {
  }

  @Override
  public void dispose() {
    getSite().getPage().removePartListener(partListener);
    super.dispose();
  }

  private void refresh() {
    frame.removeAll();
    if (null != editor) {
      // FIXME(ycoppel): SWT satellite view
//      VisualizationViewer vv = openEditor.getSatellite();
//      frame.add(vv);
    }
    frame.validate();
    frame.repaint();
  }

  private IPartListener partListener = new IPartListener() {
    public void partOpened(IWorkbenchPart part) {
      trackOpenChatEditors(part);
    }

    public void partDeactivated(IWorkbenchPart part) {
    }

    public void partClosed(IWorkbenchPart part) {
      editor = null;
      refresh();
    }

    public void partBroughtToTop(IWorkbenchPart part) {
      trackOpenChatEditors(part);
    }

    public void partActivated(IWorkbenchPart part) {
      trackOpenChatEditors(part);
    }

    private void trackOpenChatEditors(IWorkbenchPart part) {
      if (!(part instanceof ViewEditor)) {
        return;
      }
      editor = (ViewEditor) part;
      refresh();
    }
  };
}

