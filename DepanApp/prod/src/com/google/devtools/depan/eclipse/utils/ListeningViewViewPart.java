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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * a viewPart listening for editors of type E. Events are triggered when
 * an editor E is selected, unselected, opened, closed...
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E>
 */
public abstract class ListeningViewViewPart<E extends IWorkbenchPart>
    extends ViewPart {

  /** Editor component for this part */
  // TODO(leeca): is this necessary?  Compute at beginning
  // (in createPartControl()) and receive in events.
  // What else is it used for?
  private E editor;

  /** The Class for editors that this View monitors */
  private final Class<E> acceptedClass;

  /** Basic constructors.*/
  public ListeningViewViewPart(Class<E> e) {
    this.acceptedClass = e;
  }

  // Suppressed for getActivePart()
  @Override
  public void createPartControl(Composite parentComposite) {
    IWorkbenchPage parentPage = getViewSite().getPage();
    this.editor = getAcceptableEditor(parentPage.getActivePart());
    parentPage.addPartListener(partListener);
    createGui(parentComposite);
  }

  /**
   * Extending classes overload this method to generate their controls.
   * Overloaded methods can use the Composite {@link #parent} if they need
   * to connect to their containing Composite.
   */
  protected abstract void createGui(Composite parent);

  /* (non-Javadoc)
   * @see org.eclipse.ui.part.WorkbenchPart#dispose()
   */
  @Override
  public void dispose() {
    getViewSite().getPage().removePartListener(partListener);
    super.dispose();
  }

  @Override
  public void setFocus() {
    // default: do nothing.
  }

  protected E getAcceptableEditor(IWorkbenchPart part) {
    if (null == part) {
      return null;
    }
    if (acceptedClass.isAssignableFrom(part.getClass())) {
      return acceptedClass.cast(part);
    }
    return null;
  }

  protected E getAcceptableEditor() {
    return getAcceptableEditor(editor);
  }

  /**
   * Part listener listen for changes in parts. We are interested
   * for changes in {@link ViewPart}s' focus, so we can load the
   * associated tree with the currently selected {@link ViewPart}.
   */
  private IPartListener partListener = new IPartListener() {
    public void partOpened(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eOpened(acceptedClass.cast(part));
      }
    }

    public void partActivated(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eActivated(acceptedClass.cast(part));
      }
    }

    public void partBroughtToTop(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eBroughtToTop(acceptedClass.cast(part));
      }
    }

    public void partClosed(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass())) {
        eClosed(acceptedClass.cast(part));
      }
    }

    public void partDeactivated(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass())) {
        eDeactivated(acceptedClass.cast(part));
      }
    }
  };

  public void eOpened(E e) {
    if (newEditorCallback(e)) {
      this.editor = e;
    }
  }

  public void eActivated(E e) {
    if (newEditorCallback(e)) {
      this.editor = e;
    }
  }

  public void eBroughtToTop(E e) {
    if (newEditorCallback(e)) {
      this.editor = e;
    }
  }
  public void eClosed(E e) {
    if (closeEditorCallback(e)) {
      this.editor = null;
    }
  }

  // Don't change the editor just because focus has changed.
  public void eDeactivated(E e) {
  }

  /**
   * Call back invoked with the new <code>E</code> if the new editor is
   * different than the previously selected one.
   *
   * @param e the new editor.
   * @return true if this ({@link ListeningViewViewPart}) class should set
   *          editor in as well, false otherwise.
   */
  protected boolean newEditorCallback(E e) {
    return true;
  }

  /**
   * Call back invoked with the old <code>E</code> if that editor
   * is being closed.
   *
   * @param e editor being closed
   * @return true if this ({@link ListeningViewViewPart}) class should
   *          clear (nullify) the current editor in return, false otherwise.
   */
  protected boolean closeEditorCallback(E e) {
    return true;
  }
}
