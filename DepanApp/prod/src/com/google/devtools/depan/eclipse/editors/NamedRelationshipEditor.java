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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.eclipse.utils.ModificationListener;
import com.google.devtools.depan.eclipse.utils.RelationshipPicker;
import com.google.devtools.depan.eclipse.utils.RelationshipPickerHelper;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.graph.api.DirectedRelation;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.RelationshipSetAdapter;
import com.google.devtools.depan.util.XmlPersist;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import java.net.URI;
import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class NamedRelationshipEditor extends EditorPart implements
    ModificationListener<DirectedRelation, Boolean> {

  public static final String ID =
    "com.google.devtools.depan.eclipse.editors.RelationshipSetEditor";

  /**
   * Existing sets of relationship.
   */
  private Collection<RelationshipSet> sets;

  /**
   * Persistence object for the sets.
   */
  private XmlPersist<Collection<RelationshipSet>> persistance;

  /**
   * List of sets.
   */
  private ListViewer setsList = null;

  /**
   * Content provider for setsList.
   */
  private TableContentProvider<RelationshipSet> setsContentProvider = null;

  /**
   * Quick picker for existing relationship sets, copying their definition
   * to the current edition.
   */
  private RelationshipPicker relationshipPicker;

  /**
   * Selected set.
   */
  private RelationshipSet selectedSet = null;

  /**
   * Dirty state.
   */
  private boolean isDirty = true;

  /**
   * The file this editor is editing.
   */
  private IFile file;

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart
   *      #doSave(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void doSave(IProgressMonitor monitor) {
    persistance.save(sets);
    setDirtyState(false);

    // touch the file, to notify listeners about the changes
    try {
      file.touch(monitor);
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#doSaveAs()
   */
  @Override
  public void doSaveAs() {
    // no ways to save as right now.
    // see #isSaveAsAllowed()
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart #init(org.eclipse.ui.IEditorSite,
   *      org.eclipse.ui.IEditorInput)
   */
  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    // only accept a file as input.
    if (input instanceof IFileEditorInput) {
      // get the URI
      IFileEditorInput fileInput = (IFileEditorInput) input;
      URI uri = fileInput.getFile().getRawLocationURI();
      // load the file and retreive its content in sets.
      persistance = XmlPersist.load(uri);
      sets = persistance.getObject();
      setDirtyState(false);
      this.file = fileInput.getFile();
    } else {
      throw new PartInitException(
      "Input for editor is not suitable for the NamedRelationshipEditor");
    }

  }

  /**
   * set the dirtyState for <code>this</code> editor.
   * @param dirty
   */
  public void setDirtyState(boolean dirty) {
    if (dirty != isDirty()) {
      this.isDirty = dirty;
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#isDirty()
   */
  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
   */
  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.WorkbenchPart
   *      #createPartControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, true);
    container.setLayout(layout);
    layout.horizontalSpacing = 9;

    // components
    Composite leftPanel = new Composite(container, SWT.NONE);
    setsList = new ListViewer(leftPanel, SWT.V_SCROLL | SWT.BORDER);
    new Label(leftPanel, SWT.NONE).setText("New set named: ");
    final Text newSet = new Text(leftPanel, SWT.SINGLE);
    Button create = new Button(leftPanel, SWT.PUSH);
    Button delete = new Button(leftPanel, SWT.PUSH);

    // relation picker (list of relationships with forward/backward selectors)
    relationshipPicker = new RelationshipPicker();
    Control picker = relationshipPicker.getControl(container);

    create.setText("Create set");
    delete.setText("Delete selected set");

    // content for the list
    setsContentProvider = new TableContentProvider<RelationshipSet>();
    setsContentProvider.initViewer(setsList);

    // listening for changes, so we can set dirtyState.
    relationshipPicker.registerListener(this);

    // event listeners
    setsList.getList().addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        updatePicker();
      }
    });
    create.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        createSet(newSet.getText());
      }
    });
    delete.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        deleteSelectedSet();
      }
    });

    // layout
    leftPanel.setLayout(new GridLayout(4, false));
    leftPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    setsList.getList().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
    newSet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    picker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // setup content
    fill();
  }

  /**
   * delete the currently selected set.
   */
  protected void deleteSelectedSet() {
    RelationshipSet set = (RelationshipSet)
        ((IStructuredSelection) setsList.getSelection()).getFirstElement();
    sets.remove(set);
    setsContentProvider.remove(set);

    setDirtyState(true);
  }

  /**
   * @param text create a new set with the given name.
   */
  protected void createSet(String text) {
    RelationshipSet set = new RelationshipSetAdapter(text);
    sets.add(set);
    setsContentProvider.add(set);

    setDirtyState(true);
  }

  /**
   * update the relationship picker with the currently selected picker.
   * Happens when someone click on a given name in the setList.
   */
  protected void updatePicker() {
    RelationshipSet selected = (RelationshipSet)
        ((IStructuredSelection) setsList.getSelection()).getFirstElement();

    this.selectedSet = selected;
    relationshipPicker.selectRelationshipSet(selected);
  }

  /**
   * Fill the list of sets with loaded data from the file.
   */
  private void fill() {
    for (RelationshipSet set : sets) {
      setsContentProvider.add(set);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {

  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.devtools.depan.eclipse.utils.ModificationListener
   *      #modify(java.lang.Object, java.lang.String, java.lang.Object)
   */
  public void modify(DirectedRelation element, String property, Boolean value) {
    if (null == this.selectedSet) {
      return;
    }
    setDirtyState(true);
    if (RelationshipPickerHelper.COL_FORWARD.equals(property)) {
      this.selectedSet.setMatchForward(element.getRelation(), value);
    } else if (RelationshipPickerHelper.COL_BACKWARD.equals(property)) {
      this.selectedSet.setMatchBackward(element.getRelation(), value);
    }
  }

}
