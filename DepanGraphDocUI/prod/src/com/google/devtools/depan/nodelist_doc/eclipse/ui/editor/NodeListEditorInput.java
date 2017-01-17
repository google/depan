/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.nodelist_doc.eclipse.ui.editor;

import com.google.devtools.depan.nodelist_doc.eclipse.NodeListResources;
import com.google.devtools.depan.nodelist_doc.model.NodeListDocument;
import com.google.devtools.depan.platform.NewEditorHelper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Define the editor startup state for a newly defined NodeList.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class NodeListEditorInput implements IEditorInput {

  private final NodeListDocument nodeListInfo;

  private final String baseName;

  private String displayName;

  public NodeListEditorInput(NodeListDocument nodeListInfo, String baseName) {
    this.nodeListInfo = nodeListInfo;
    this.baseName = baseName;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object getAdapter(Class adapter) {
    return null;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return NodeListResources.IMAGE_DESC_NODELIST_DOC;
  }

  @Override
  public String getName() {
    if (null == displayName) {
      displayName = calcDisplayName();
    }
    return displayName;
  }

  public String getBaseName() {
    return baseName;
  }

  @Override
  public IPersistableElement getPersistable() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return getName();
  }

  public NodeListDocument getNodeListDocument() {
    return nodeListInfo;
  }

  private String calcDisplayName() {
    if (null == baseName) {
      return NodeListResources.NEW_NODE_LIST;
    }
    if (baseName.isEmpty()) {
      return NodeListResources.NEW_NODE_LIST;
    }
    return NewEditorHelper.newEditorLabel(
        baseName + " - " + NodeListResources.NEW_NODE_LIST);
  }
}
