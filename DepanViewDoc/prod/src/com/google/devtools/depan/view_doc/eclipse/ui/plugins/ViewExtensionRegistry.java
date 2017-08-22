/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.eclipse.ui.plugins;

import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.model.NodeColorMode;
import com.google.devtools.depan.view_doc.model.NodeRatioMode;
import com.google.devtools.depan.view_doc.model.NodeShapeMode;
import com.google.devtools.depan.view_doc.model.NodeSizeMode;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import java.util.Collection;

/**
 * @author <a href='mailto:leeca@pnambic.com'>Lee Carver</a>
 */
public class ViewExtensionRegistry
    extends ContributionRegistry<ViewExtension> {

  /**
   * Extension point name for view extensions
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.view_doc.eclipse.ui.registry.view_extension";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static ViewExtensionRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<ViewExtension>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected ViewExtension createInstance() throws CoreException {
      return (ViewExtension) buildInstance(ATTR_CLASS);
    }
  }

  private ViewExtensionRegistry() {
    // Prevent instantiation.
  }

  @Override
  protected ContributionEntry<ViewExtension> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    ViewDocLogger.LOG.error(
        "View extension registry load failure for {}", entryId, err);
  }

  private ViewExtension getExtension(String extensionId) {
    ContributionEntry<ViewExtension> result = getContribution(extensionId);
    if (null != result) {
      return result.getInstance();
    }
    return null;
  }

  /////////////////////////////////////
  // Participate in rendering

  private void deriveDetails(ViewEditor editor) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      entry.getInstance().deriveDetails(editor);
    }
  }

  private void prepareView(ViewEditor editor) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      try {
        entry.getInstance().prepareView(editor);
      } catch (Exception err) {
        ViewDocLogger.LOG.error(
            "Unable to prepare extension {} for editor {}",
            entry.getId(), editor.getBaseName() , err);
      }
    }
  }

  /////////////////////////////////////
  // Color operations

  private NodeColorMode getGetDefaultColorMode() {
    // TODO Auto-generated method stub
    return null;
  }

  private NodeColorMode getGetNodeColorMode(String label) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      for (NodeColorMode mode : entry.getInstance().getNodeColorModes()) {
        if (mode.getLabel().equals(label)) {
          return mode;
        }
      }
    }
    return null;
  }

  private Collection<NodeColorMode> getNodeColorModes() {
    Collection<NodeColorMode> result = Lists.newArrayList();
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      result.addAll(entry.getInstance().getNodeColorModes());
    }
    return result;
  }

  /////////////////////////////////////
  // Ratio operations

  private NodeRatioMode getDefaultRatioMode() {
    // TODO Auto-generated method stub
    return null;
  }

  private NodeRatioMode getNodeRatioMode(String label) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      for (NodeRatioMode mode : entry.getInstance().getNodeRatioModes()) {
        if (mode.getLabel().equals(label)) {
          return mode;
        }
      }
    }
    return null;
  }

  private Collection<NodeRatioMode> getNodeRatioModes() {
    Collection<NodeRatioMode> result = Lists.newArrayList();
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      result.addAll(entry.getInstance().getNodeRatioModes());
    }
    return result;
  }

  /////////////////////////////////////
  // Shape operations

  private NodeShapeMode getGetDefaultShapeMode() {
    // TODO Auto-generated method stub
    return null;
  }

  private NodeShapeMode getGetNodeShapeMode(String label) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      for (NodeShapeMode mode : entry.getInstance().getNodeShapeModes()) {
        if (mode.getLabel().equals(label)) {
          return mode;
        }
      }
    }
    return null;
  }

  private Collection<NodeShapeMode> getNodeShapeModes() {
    Collection<NodeShapeMode> result = Lists.newArrayList();
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      result.addAll(entry.getInstance().getNodeShapeModes());
    }
    return result;
  }

  /////////////////////////////////////
  // Size operations

  private Collection<NodeSizeMode> getNodeSizeModes() {
    Collection<NodeSizeMode> result = Lists.newArrayList();
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      result.addAll(entry.getInstance().getNodeSizeModes());
    }
    return result;
  }

  private NodeSizeMode getGetNodeSizeMode(String label) {
    for (ContributionEntry<ViewExtension> entry : getContributions()) {
      for (NodeSizeMode mode : entry.getInstance().getNodeSizeModes()) {
        if (mode.getLabel().equals(label)) {
          return mode;
        }
      }
    }
    return null;
  }

  private NodeSizeMode getGetDefaultSizeMode() {
    // TODO Auto-generated method stub
    return null;
  }

  /////////////////////////////////////
  // Static access methods

  /**
   * Provide the {@code ViewExtensionRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized ViewExtensionRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new ViewExtensionRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static ViewExtension getRegistryExtension(String extensionId) {
    return getInstance().getExtension(extensionId);
  }

  public static String getRegistryId(ViewExtension ext) {
    return getInstance().getContributionId(ext);
  }

  public static void deriveRegistryDetails(ViewEditor editor) {
    getInstance().deriveDetails(editor);
  }

  public static void prepareRegistryView(ViewEditor editor) {
    getInstance().prepareView(editor);
  }

  public static NodeColorMode getRegistryGetDefaultColorMode() {
    return getInstance().getGetDefaultColorMode();
  }

  public static NodeColorMode getRegistryGetNodeColorMode(String label) {
    return getInstance().getGetNodeColorMode(label);
  }

  public static Collection<NodeColorMode> getRegistryNodeColorModes() {
    return getInstance().getNodeColorModes();
  }

  public static NodeRatioMode getRegistryDefaultRatioMode() {
    return getInstance().getDefaultRatioMode();
  }

  public static NodeRatioMode getRegistryGetNodeRatioMode(String label) {
    return getInstance().getNodeRatioMode(label);
  }

  public static Collection<NodeRatioMode> getRegistryNodeRatioModes() {
    return getInstance().getNodeRatioModes();
  }

  public static NodeShapeMode getRegistryGetDefaultShapeMode() {
    return getInstance().getGetDefaultShapeMode();
  }

  public static NodeShapeMode getRegistryGetNodeShapeMode(String label) {
    return getInstance().getGetNodeShapeMode(label);
  }

  public static Collection<NodeShapeMode> getRegistryNodeShapeModes() {
    return getInstance().getNodeShapeModes();
  }

  public static NodeSizeMode getRegistryGetDefaultSizeMode() {
    return getInstance().getGetDefaultSizeMode();
  }

  public static NodeSizeMode getRegistryGetNodeSizeMode(String label) {
    return getInstance().getGetNodeSizeMode(label);
  }

  public static Collection<NodeSizeMode> getRegistryNodeSizeModes() {
    return getInstance().getNodeSizeModes();
  }
}
