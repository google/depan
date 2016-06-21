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

package com.google.devtools.depan.platform.eclipse.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * A ViewPart that is bound to editors of type E.
 * 
 * This is a Template class, with a variety of hook methods for derived
 * classes to implement.  One set of hook methods provide for ViewPart
 * widget and GUI definitions.  Other hook methods provide integration
 * (via {@link IPartListener} events) for WorkBench activity with and editor
 * of the type E.  These include activation, opened, brought-to-top, closed,
 * and deactivated.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <E>
 */
public abstract class EditorBoundViewPart<E extends IWorkbenchPart>
    extends ViewPart {

  /**
   * The Class for editors that this View monitors.
   */
  private final Class<E> acceptedClass;

  /**
   * The editor most recently received by this ViewPart.
   */
  private E editor;

  /**
   * Derived classes should invoke this with a constant value.
   */
  public EditorBoundViewPart(Class<E> e) {
    this.acceptedClass = e;
  }

  /////////////////////////////////////
  // UX Features

  @Override
  public void createPartControl(Composite parentComposite) {
    IWorkbenchPage parentPage = getViewSite().getPage();
    editor = getAcceptableEditor(parentPage.getActiveEditor());
    createGui(parentComposite);
    parentPage.addPartListener(partListener);
  }

  @Override
  public void dispose() {
    disposeGui();
    getViewSite().getPage().removePartListener(partListener);
    super.dispose();
  }

  /**
   * Hook method for derived classes to generate their controls.
   */
  protected abstract void createGui(Composite parent);

  /**
   * Hook method for derived classes to dispose their UI resources.
   */
  protected abstract void disposeGui();

  @Override
  public void setFocus() {
    // default: do nothing.
  }

  protected E getEditor() {
    return editor;
  }

  protected boolean hasEditor() {
    return null != editor;
  }

  /////////////////////////////////////
  // State management

  protected E getAcceptableEditor(IWorkbenchPart part) {
    if (null == part) {
      return null;
    }
    if (acceptedClass.isAssignableFrom(part.getClass())) {
      return acceptedClass.cast(part);
    }
    return null;
  }

  protected E getActiveEditor() {
    IWorkbenchWindow wndo = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (null == wndo) {
      return null;
    }

    IPartService srvc = wndo.getPartService();
    return getAcceptableEditor(srvc.getActivePart());
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

    @Override
    public void partOpened(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eOpened(acceptedClass.cast(part));
      }
    }

    @Override
    public void partActivated(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eActivated(acceptedClass.cast(part));
      }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass()) && editor != part) {
          eBroughtToTop(acceptedClass.cast(part));
      }
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
      if (acceptedClass.isAssignableFrom(part.getClass())) {
        eClosed(acceptedClass.cast(part));
      }
    }

    @Override
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
   * @return true if this ({@link EditorBoundViewPart}) class should set
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
   * @return true if this ({@link EditorBoundViewPart}) class should
   *          clear (nullify) the current editor in return, false otherwise.
   */
  protected boolean closeEditorCallback(E e) {
    return true;
  }
}
