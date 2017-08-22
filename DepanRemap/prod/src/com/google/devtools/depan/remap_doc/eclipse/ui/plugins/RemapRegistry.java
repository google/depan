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

package com.google.devtools.depan.remap_doc.eclipse.ui.plugins;


import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.remap_doc.eclipse.RemapLogger;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;

import java.util.Collection;
import java.util.List;

/**
 * @author Lee Carver
 */
public class RemapRegistry extends
    ContributionRegistry<RemapContributor> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.remap_doc.eclipse.ui.registry.relation";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static RemapRegistry INSTANCE = null;

  static class Entry extends ContributionEntry<RemapContributor>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected RemapContributor createInstance() throws CoreException {
      return (RemapContributor) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private RemapRegistry() {
  }

  @Override
  protected ContributionEntry<RemapContributor> buildEntry(
      String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    RemapLogger.LOG.error(
        "Remap registry load failure for {}", entryId, err);
  }

  /////////////////////////////////////
  // Project Relations

  public Collection<RemapContributor> getRemapContributions() {
    Collection<ContributionEntry<RemapContributor>> contribs =
        getContributions();
    List<RemapContributor> result =
        Lists.newArrayListWithExpectedSize(contribs.size());
    for (ContributionEntry<RemapContributor> item : contribs) {
      result.add(item.getInstance());
    }
    return result;
  }

  public Class<? extends ElementEditor> getEditor(Element element) {
    // TODO Auto-generated method stub
    return null;
  }


  public Image getImage(Element element) {
    // TODO Auto-generated method stub
    return null;
  }

  /////////////////////////////////////
  // Singleton access methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized RemapRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new RemapRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static Collection<RemapContributor> getRegistryRemapContributions() {
    return getInstance().getRemapContributions();
  }

  public static Class<? extends ElementEditor> getRegistryEditor(Element element) {
    return getInstance().getEditor(element);
  }

  public static Image getRegistryImage(Element element) {
    return getInstance().getImage(element);
  }
}
