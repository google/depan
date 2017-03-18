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

package com.google.devtools.depan.view_doc.eclipse.ui.widgets;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.GenericSaveLoadControl;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.relations.persistence.RelationSetResources;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.view_doc.model.RelationDisplayDocument;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayDocXmlPersist;
import com.google.devtools.depan.view_doc.persistence.RelationDisplayResources;

import org.eclipse.swt.widgets.Composite;

/**
 * Control for saving and loading {@link RelationDisplayDocument}s.
 * The class defines the type-specific strings and factories
 * for the supplied generic type {@link RelationDisplayDocument}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class RelationDisplaySaveLoadControl
    extends GenericSaveLoadControl<RelationDisplayDocument> {

  public RelationDisplaySaveLoadControl(Composite parent) {
    super(parent, CONFIG);
  }

  private static SaveLoadConfig<RelationDisplayDocument> CONFIG =
      new ControlSaveLoadConfig();

  private static class ControlSaveLoadConfig
      extends SaveLoadConfig<RelationDisplayDocument> {

    @Override
    public ResourceContainer getContainer() {
      return RelationSetResources.getContainer();
    }

    @Override
    public AbstractDocXmlPersist<RelationDisplayDocument> getDocXmlPersist(
        boolean readable) {
      return RelationDisplayDocXmlPersist.build(readable);
    }

    @Override
    public String getSaveLabel() {
      return "Save as Relation Display resource...";
    }

    @Override
    public String getLoadLabel() {
      return "Load from Relation Display resource...";
    }

    @Override
    public String getBaseName() {
      return RelationDisplayResources.BASE_NAME;
    }

    @Override
    public String getExension() {
      return RelationDisplayResources.EXTENSION;
    }
  }
}
