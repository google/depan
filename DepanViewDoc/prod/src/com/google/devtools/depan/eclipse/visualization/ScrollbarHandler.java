/*
 * Copyright 2013 The Depan Project Authors
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
package com.google.devtools.depan.eclipse.visualization;

import com.google.devtools.depan.eclipse.visualization.ogl.GLScene;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import java.awt.geom.Rectangle2D;

/**
 * Integrate the OGL Grip with the Scrollbars from the SWT Composite.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ScrollbarHandler {
  private static final int INCR_SCALE = 16;

  private static final int SCROLL_MINIMUM = 0;

  private final Scrollable view;
  private final GLScene scene;

  private SelectionListener selectListener;

  private ScrollBar vertBar;
  private double vertSelBase;
  private double vertRange;

  private ScrollBar horizBar;
  private double horizSelBase;
  private double horizRange;

  public ScrollbarHandler(Scrollable view, GLScene scene) {
    this.view = view;
    this.scene = scene;
    this.vertBar = view.getVerticalBar();
    this.horizBar = view.getHorizontalBar();
  }

  public void acquireResources() {
    selectListener = new SelectionListener() {

      @Override
      public void widgetSelected(SelectionEvent e) {
        double yPos = vertSelBase - (vertRange *
            ((double) vertBar.getSelection() / (double) vertBar.getMaximum()));
        double xPos = horizSelBase + (horizRange *
            ((double) horizBar.getSelection() / (double) horizBar.getMaximum()));
        scene.moveToPosition((float) xPos, (float) yPos);
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
      }
    };

    vertBar.addSelectionListener(selectListener);
    horizBar.addSelectionListener(selectListener);
  }

  public void disposeResources() {
    if (null != selectListener) {
      vertBar.removeSelectionListener(selectListener);
      horizBar.removeSelectionListener(selectListener);
    }
  }

  public void updateDrawingBounds(Rectangle2D drawing, Rectangle2D viewport) {
    Rectangle viewArea = view.getClientArea();

    // Adjust vertical bar
    int vertScale = vertBar.getMaximum();

    // Room to scroll drawing off window top or bottom.
    vertRange = (2 * viewport.getHeight()) + drawing.getHeight();

    // Thumb should represent entire viewport
    int vertThumbSpan = (int)(vertScale
        * (viewport.getHeight() / vertRange));
    int vertIncr = Math.max(1, vertThumbSpan / INCR_SCALE);

    // Vertical scroll position is zero at top of screen, with increasing
    // pixel positions down the screen.  The OGL graphics space is the other
    // way round, with zero at the bottom and increasing positions up the
    // screen.  So use the top (MaxY) of the viewport box for the scrollbar
    // selection value.
    int vertScrollSel = (int)(vertScale
        * (viewport.getHeight() +  drawing.getMaxY() - viewport.getMaxY()) / vertRange);
    vertBar.setValues(vertScrollSel, SCROLL_MINIMUM, viewArea.height, vertThumbSpan, vertIncr, vertThumbSpan);
    vertSelBase = drawing.getMaxY() + (viewport.getHeight() / 2);

    // Adjust horizontal bar
    int horizScale = horizBar.getMaximum();

    // Room to scroll drawing off window top or bottom.
    horizRange = (2 * viewport.getWidth()) + drawing.getWidth();

    // Thumb should represent entire viewport
    int horizThumbSpan = (int)(horizScale
        * (viewport.getWidth() / horizRange));
    int horizIncr = Math.max(1, horizThumbSpan / INCR_SCALE);

    // Horizontal scroll position is zero at top of left of screen, with
    // increasing pixel positions going right across the screen.  This matches
    // the OGL graphics space.  So use the left (MinX) of the viewport and
    // drawing boxes for the scrollbar selection value.
    int horizScrollSel = (int)(horizScale
        * (viewport.getWidth() + viewport.getMinX() - drawing.getMinX()) / horizRange);
    horizBar.setValues(horizScrollSel, SCROLL_MINIMUM, viewArea.width, horizThumbSpan, horizIncr, horizThumbSpan);
    horizSelBase = drawing.getMinX() - viewport.getWidth() / 2;
  }
}
