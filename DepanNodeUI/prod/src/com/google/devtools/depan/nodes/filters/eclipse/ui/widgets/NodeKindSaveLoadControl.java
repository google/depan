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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.matchers.persistence.GraphEdgeMatcherResources;
import com.google.devtools.depan.nodes.filters.model.NodeKindDocument;
import com.google.devtools.depan.nodes.filters.persistence.NodeKindDocXmlPersist;
import com.google.devtools.depan.nodes.filters.persistence.NodeKindResources;
import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.platform.eclipse.ui.widgets.GenericSaveLoadControl;
import com.google.devtools.depan.platform.eclipse.ui.widgets.SaveLoadConfig;
import com.google.devtools.depan.resources.ResourceContainer;

import org.eclipse.swt.widgets.Composite;

import java.net.URI;

/**
 * Control for saving and loading {@link GraphEdgeMatcherDescriptor}s
 * (a.k.a. {@code EdgeMatcherDocument}).
 * 
 * This provides the right set of document extensions.
 * Concrete types still need to add {@link #getSaveWizard()} and
 * {@link #loadURI(URI)} implementations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class NodeKindSaveLoadControl
    extends GenericSaveLoadControl<NodeKindDocument> {

  public NodeKindSaveLoadControl(Composite parent) {
    super(parent, CONFIG);
  }

  private static SaveLoadConfig<NodeKindDocument> CONFIG =
      new ControlSaveLoadConfig();

  private static class ControlSaveLoadConfig
      extends SaveLoadConfig<NodeKindDocument> {

    @Override
    public ResourceContainer getContainer() {
      return GraphEdgeMatcherResources.getContainer();
    }

    @Override
    public AbstractDocXmlPersist<NodeKindDocument> getDocXmlPersist(
        boolean readable) {
      return NodeKindDocXmlPersist.build(readable);
    }

    @Override
    public String getSaveLabel() {
      return "Save as NodeKinds...";
    }

    @Override
    public String getLoadLabel() {
      return "Load from NodeKinds...";
    }

    @Override
    public String getBaseName() {
      return NodeKindResources.BASE_NAME;
    }

    @Override
    public String getExension() {
      return GraphEdgeMatcherResources.EXTENSION;
    }
  }
}
