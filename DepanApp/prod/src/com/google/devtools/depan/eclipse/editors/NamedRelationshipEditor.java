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

package com.google.devtools.depan.eclipse.editors;

import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;
import com.google.devtools.depan.eclipse.utils.GraphEdgeMatcherEditorPart;
import com.google.devtools.depan.eclipse.utils.ModificationListener;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphEdgeMatcher;
import com.google.devtools.depan.platform.TableContentProvider;

import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class NamedRelationshipEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.eclipse.editors.RelationshipSetEditor";

  /**
   * Existing sets of relationship.
   */
  private Collection<GraphEdgeMatcherDescriptor> edgeMatchers;

  /**
   * List of sets.
   */
  private ListViewer setsList;

  /**
   * Content provider for setsList.
   */
  private TableContentProvider<GraphEdgeMatcherDescriptor> edgeMatcherContentProvider;

  /**
   * Relation editing table for EdgeMatcher.
   */
  private GraphEdgeMatcherEditorPart edgeMatcherEditor;

  /**
   * Selected set.
   */
  private GraphEdgeMatcherDescriptor selectedSet = null;

  /**
   * Dirty state.
   */
  private boolean isDirty = true;

  /**
   * The file this editor is editing.
   */
  private IFile file;

  @Override
  public void doSave(IProgressMonitor monitor) {
    try {
      // TODO(leeca):  Is this configured with the correct XStream flavor?
      ObjectXmlPersist persist =
          new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
      persist.save(file.getRawLocationURI(), edgeMatchers);

      setDirtyState(false);

      // touch the file, to notify listeners about the changes
      file.touch(monitor);
    } catch (CoreException e) {
      e.printStackTrace();
    } catch (IOException errIo) {
      monitor.setCanceled(true);
      throw new RuntimeException(
          "Unable to save named relationship to " + file.getRawLocationURI(),
          errIo);
    }
  }

  @Override
  public void doSaveAs() {
    // no ways to save as right now.
    // see #isSaveAsAllowed()
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    // only accept a file as input.
    if (input instanceof IFileEditorInput) {
      try {
        // get the URI
        IFileEditorInput fileInput = (IFileEditorInput) input;
        file = fileInput.getFile();

        // load the file and retrieve its content in sets.
        // TODO(leeca):  Is this configured with the correct XStream flavor?
        ObjectXmlPersist persist =
            new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());

        edgeMatchers = loadNamedRelationship(persist, file.getRawLocationURI());

        setDirtyState(false);
      } catch (IOException errIo) {
        throw new PartInitException(
            "Unable to load named relationship from "
            + file.getRawLocationURI(), errIo);
      }
    } else {
      throw new PartInitException(
      "Input for editor is not suitable for the NamedRelationshipEditor");
    }
  }

  /**
   * Isolate unchecked conversion.
   */
  @SuppressWarnings("unchecked")
  private Collection<GraphEdgeMatcherDescriptor> loadNamedRelationship(
      ObjectXmlPersist persist, URI fromUri) throws IOException {
    return (Collection<GraphEdgeMatcherDescriptor>) persist.load(fromUri);
  }

  /**
   * Set the dirtyState for <code>this</code> editor.
   * 
   * @param dirty
   */
  public void setDirtyState(boolean dirty) {
    if (dirty != isDirty()) {
      this.isDirty = dirty;
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
  }

  @Override
  public boolean isDirty() {
    return this.isDirty;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, true);
    container.setLayout(layout);
    layout.horizontalSpacing = 9;

    // components
    Composite leftPanel = new Composite(container, SWT.NONE);
    setsList = new ListViewer(leftPanel, SWT.V_SCROLL | SWT.BORDER);
    new Label(leftPanel, SWT.NONE).setText("New edges named: ");
    final Text newSet = new Text(leftPanel, SWT.SINGLE);
    Button create = new Button(leftPanel, SWT.PUSH);
    Button delete = new Button(leftPanel, SWT.PUSH);

    // relation picker (list of relationships with forward/backward selectors)
    edgeMatcherEditor = new GraphEdgeMatcherEditorPart();
    Control picker = edgeMatcherEditor.getControl(container);

    create.setText("Create set");
    delete.setText("Delete selected set");

    // content for the list
    edgeMatcherContentProvider = new TableContentProvider<GraphEdgeMatcherDescriptor>();
    edgeMatcherContentProvider.initViewer(setsList);

    // listening for changes, so we can set dirtyState.
    edgeMatcherEditor.registerListener(
        new ModificationListener<Relation, Boolean>() {

          @Override
          public void modify(Relation element, String property, Boolean value) {
            handleModify(element, property, value);
          }
        });
    
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
        deleteEdgeMatcher();
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
  protected void deleteEdgeMatcher() {
    GraphEdgeMatcherDescriptor set = (GraphEdgeMatcherDescriptor)
        ((IStructuredSelection) setsList.getSelection()).getFirstElement();
    edgeMatchers.remove(set);
    edgeMatcherContentProvider.remove(set);

    setDirtyState(true);
  }

  /**
   * @param text create a new set with the given name.
   */
  protected void createSet(String text) {
    GraphEdgeMatcherDescriptor set = 
        new GraphEdgeMatcherDescriptor(text, new GraphEdgeMatcher());
    edgeMatchers.add(set);
    edgeMatcherContentProvider.add(set);

    setDirtyState(true);
  }

  /**
   * update the edge matcher picker with the currently selected picker.
   * Happens when someone click on a given name in the setList.
   */
  protected void updatePicker() {
    GraphEdgeMatcherDescriptor selected = (GraphEdgeMatcherDescriptor)
        ((IStructuredSelection) setsList.getSelection()).getFirstElement();

    this.selectedSet = selected;
    edgeMatcherEditor.selectEdgeMatcher(selected, false);
  }

  /**
   * Fill the list of sets with loaded data from the file.
   */
  private void fill() {
    for (GraphEdgeMatcherDescriptor edgeMatcher : edgeMatchers) {
      edgeMatcherContentProvider.add(edgeMatcher);
    }
  }

  @Override
  public void setFocus() {
  }

  private void handleModify(Relation element, String property, Boolean value) {
    if (null == selectedSet) {
      return;
    }
    setDirtyState(true);
  }
}
