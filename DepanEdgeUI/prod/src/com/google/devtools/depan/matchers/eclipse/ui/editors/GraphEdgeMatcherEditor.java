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

package com.google.devtools.depan.matchers.eclipse.ui.editors;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.GraphEdgeMatcherEditorPart;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.ModificationListener;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.EdgeMatcherDocXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;

import java.net.URI;

/**
 * Based on a legacy version of {@code NamedRelationshipEditor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class GraphEdgeMatcherEditor extends EditorPart {

  public static final String ID =
      "com.google.devtools.depan.matchers.eclipse.ui.editors.GraphEdgeMatcherEditor";

  /**
   * EdgeMatcher document that is being edited.
   */
  // private Collection<GraphEdgeMatcherDescriptor> edgeMatchers;
  private GraphEdgeMatcherDescriptor matcherInfo;

  /**
   * Relation editing table for EdgeMatcher.
   */
  private GraphEdgeMatcherEditorPart edgeMatcherEditor;

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

    URI location = file.getRawLocationURI();
    persistDocument(location, monitor);
  }

  @Override
  public void doSaveAs() {
    SaveAsDialog saveas = new SaveAsDialog(getSite().getShell());
    saveas.setOriginalFile(file);
    saveas.setOriginalName(matcherInfo.getName());
    if (saveas.open() != SaveAsDialog.OK) {
      return;
    }

    // get the file relatively to the workspace.
    IFile saveFile = WorkspaceTools.calcViewFile(
        saveas.getResult(), GraphEdgeMatcherDescriptor.EXTENSION);
    // TODO: set up a progress monitor
    persistDocument(saveFile.getRawLocationURI(), null);
    setPartName(saveFile.getName());
  }


  /**
   * Save the current {@link GraphEdgeMatcherDescriptor} at the supplied
   * location.
   * 
   * @param location
   * @param monitor
   */
  private void persistDocument(
      URI location, IProgressMonitor monitor) {
    try {
      EdgeMatcherDocXmlPersist persist = EdgeMatcherDocXmlPersist.build(false);
      persist.save(location, matcherInfo);

      setDirtyState(false);

      // touch the file, to notify listeners about the changes
      file.touch(monitor);
    } catch (CoreException err) {
      monitor.setCanceled(true);
      PersistenceLogger.logException(
          "Unable to save named relationship to " + location,
          err);
    }
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException {
    setSite(site);
    setInput(input);
    // only accept a file as input.
    if (input instanceof IFileEditorInput) {
      // get the URI
      IFileEditorInput fileInput = (IFileEditorInput) input;
      file = fileInput.getFile();

      EdgeMatcherDocXmlPersist persist = EdgeMatcherDocXmlPersist.build(true);
      matcherInfo = persist.load(file.getRawLocationURI());

      setDirtyState(false);
      return;
    }

    // Something unexpected
    throw new PartInitException(
        "Input for editor is not suitable for the NamedRelationshipEditor");
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
    return true;
  }

  @Override
  public void createPartControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, true);
    container.setLayout(layout);
    layout.horizontalSpacing = 9;

    // components
    // TODO: Name for matcher ..

    // relation picker (list of relationships with forward/backward selectors)
    edgeMatcherEditor = new GraphEdgeMatcherEditorPart();
    Control picker = edgeMatcherEditor.getControl(container);
    picker.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // listening for changes, so we can set dirtyState.
    edgeMatcherEditor.registerModificationListener(
        new ModificationListener<Relation, Boolean>() {

          @Override
          public void modify(Relation element, String property, Boolean value) {
            handleModify(element, property, value);
          }
        });
  }

  private void handleModify(Relation element, String property, Boolean value) {
    setDirtyState(true);
  }

  @Override
  public void setFocus() {
  }
}
