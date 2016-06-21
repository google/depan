/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.platform.eclipse.ui.widgets.GenericSaveLoadControl;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;
import com.google.devtools.depan.relations.persistence.RelationSetDescriptorXmlPersist;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import java.net.URI;

/**
 * Control for saving and loading {@link RelationSet}s.
 * 
 * This provides the right set of document extensions.
 * Concrete types still need to add {@link #getSaveWizard()} and
 * {@link #loadURI(URI)} implementations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class RelationSetSaveLoadControl
    extends GenericSaveLoadControl {

  public RelationSetSaveLoadControl(
      Composite parent, String saveLabel, String loadLabel) {
    super(parent, saveLabel, loadLabel);
  }

  public RelationSetSaveLoadControl(Composite parent) {
    super(parent, "Save as RelationSet...", "Load from RelationSet...");
  }

  @Override
  protected void prepareLoadDialog(FileDialog dialog) {
    dialog.setFilterExtensions(new String[] {RelationSetDescriptor.EXTENSION});
  }

  /**
   * Utility method for derived types to use when implementing
   * {@link #loadURI(URI)}.
   */
  protected RelationSetDescriptor loadRelationSetDescr(URI uri) {
    return RelationSetDescriptorXmlPersist.build(true).load(uri);
  }
}
