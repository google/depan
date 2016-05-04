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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;

/**
 * A {@link Sasher} uses the {@link Sash} widget to split a part in two
 * resizable panels. This implementation make {@link Sash} use more easy, by
 * handling resize events, and layouts.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class Sasher extends Composite {

  /**
   * The Sash object.
   */
  private Sash sash;

  /**
   * Style used to construct the Sash object. It is tested against SWT.VERTICAL,
   * so it should never be build with SWT.VERTICAL | SWT.HORIZONTAL.
   */
  private int sashStyle;

  /**
   * A minimum size for the sash, applying when resizing it.
   */
  int limit = 20;

  /**
   * Construct a {@link Sasher} object for the given parent and style.
   *
   * @see Composite#Composite(Composite, int)
   * @param parent
   * @param style
   */
  public Sasher(Composite parent, int style) {
    super(parent, style);
    this.setLayout(new FormLayout());
  }

  /**
   * Initialize the sasher, with the two given contained controls, the style,
   * {@link SWT#VERTICAL} or {@link SWT#HORIZONTAL}, and the percentage of
   * space initially used by the first Control.
   *
   * @param first first control (top or left)
   * @param second second control (down or right)
   * @param sasherStyle {@link SWT#VERTICAL} or {@link SWT#HORIZONTAL}.
   * @param percent percentage of space initially used by the first Control
   */
  public void init(Control first, Control second,
      int sasherStyle, int percent) {
    this.sashStyle = sasherStyle;

    sash = new Sash(this, sashStyle);

    // create position instructions
    FormData firstData = new FormData();
    final FormData sashData = new FormData();
    FormData secondData = new FormData();

    // setup constant positions
    firstData.left = new FormAttachment(0);
    firstData.top = new FormAttachment(0);
    sashData.left = new FormAttachment(0);
    sashData.top = new FormAttachment(percent);
    secondData.right = new FormAttachment(100);
    secondData.bottom = new FormAttachment(100);

    // setup direction dependent positions
    if (isVertical()) { // horizontal == top/down
      firstData.right = new FormAttachment(sash);
      firstData.bottom = new FormAttachment(100);
      sashData.bottom = new FormAttachment(100);
      secondData.left = new FormAttachment(sash);
      secondData.top = new FormAttachment(0);
    } else {
      firstData.right = new FormAttachment(100);
      firstData.bottom = new FormAttachment(sash);
      sashData.right = new FormAttachment(100);
      secondData.left = new FormAttachment(0);
      secondData.top = new FormAttachment(sash);
    }

    // set the layouts
    first.setLayoutData(firstData);
    sash.setLayoutData(sashData);
    second.setLayoutData(secondData);

    // move event / constraints:
    sash.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(Event e) {
        if (isVertical()) {
          resizeVertical(e, sashData);
        } else {
          resizeHorizontal(e, sashData);
        }
      }
    });
  }

  /**
   * Set the limit of space used by each components.
   *
   * @param newLimit new minimum size for each components.
   */
  public void setLimit(int newLimit) {
    this.limit = newLimit;
  }

  /**
   * Handle an horizontal resize event.
   *
   * @param e Resize event
   * @param sashData sashData involved
   */
  private void resizeHorizontal(Event e, FormData sashData) {
    Rectangle sashRect = sash.getBounds();
    Rectangle shellRect = Sasher.this.getBounds();
    int bottom = shellRect.height - sashRect.height - limit;
    e.y = Math.max(Math.min(e.y, bottom), limit);
    if (e.y != sashRect.y)  {
      sashData.top = new FormAttachment(0, e.y);
      Sasher.this.layout();
    }
  }

  /**
   * Handle an vertical resize event.
   *
   * @param e Resize event
   * @param sashData sashData involved
   */
  private void resizeVertical(Event e, FormData sashData) {
    Rectangle sashRect = sash.getBounds();
    Rectangle shellRect = Sasher.this.getBounds();
    int right = shellRect.width - sashRect.width - limit;
    e.x = Math.max(Math.min(e.x, right), limit);
    if (e.x != sashRect.x)  {
      sashData.top = new FormAttachment(0, e.x);
      Sasher.this.layout();
    }
  }

  /**
   * Determine if the sash object must be considered as vertical or horizontal.
   *
   * @return true if the sash is vertical.
   */
  private boolean isVertical() {
    // Arbitrary choice. That's what is used in Sash implementation.
    return (sashStyle & SWT.VERTICAL) != 0;
  }
}
