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

package com.google.devtools.depan.persistence.plugins;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.PersistenceLogger;
import com.google.devtools.depan.platform.plugin.ContributionEntry;
import com.google.devtools.depan.platform.plugin.ContributionRegistry;
import com.google.devtools.depan.resources.PropertyDocument;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Registry of XStream configuration contributions.  The data from all
 * contributions is loaded for convenient accesses.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ResourceDocumentConfigRegistry extends
  ContributionRegistry<ResourceDocumentConfig> {

  /**
   * Extension point name for persistence configuration
   */
  public final static String EXTENTION_POINT =
      "com.google.devtools.depan.persistence.resources.config";

  /**
   * Static instance. This class is a singleton.
   * It is created lazily on first access.
   */
  private static ResourceDocumentConfigRegistry INSTANCE = null;


  static class Entry extends ContributionEntry<ResourceDocumentConfig>{

    public Entry(String bundleId, IConfigurationElement element) {
      super(bundleId, element);
    }

    @Override
    protected ResourceDocumentConfig createInstance() throws CoreException {
      return (ResourceDocumentConfig) buildInstance(ATTR_CLASS);
    }
  }

  /**
   * Singleton class: private constructor to prevent instantiation.
   */
  private ResourceDocumentConfigRegistry() {
  }

  @Override
  protected ContributionEntry<ResourceDocumentConfig>
      buildEntry(String bundleId, IConfigurationElement element) {
    return new Entry(bundleId, element);
  }

  @Override
  protected void reportException(String entryId, Exception err) {
    PersistenceLogger.LOG.error(
        "XStream configuration load failure for {}", entryId, err);
  }

  private ResourceDocumentConfig getDocumentConfig(String docExt) {
    for (ContributionEntry<ResourceDocumentConfig> entry : getContributions()) {
      ResourceDocumentConfig config = entry.getInstance();
      if (config.forExtension(docExt)) {
        return config;
      }
    }
    return null;
  }

  /////////////////////////////////////
  // Static NodeElement Methods

  /**
   * Provide the {@code SourcePluginRegistry} singleton.
   * It is created lazily when needed.
   */
  public static synchronized ResourceDocumentConfigRegistry getInstance() {
    if (null == INSTANCE) {
      INSTANCE = new ResourceDocumentConfigRegistry();
      INSTANCE.load(EXTENTION_POINT);
    }
    return INSTANCE;
  }

  public static ResourceDocumentConfig getRegistryDocumentConfig(
      String docExt) {
    return getInstance().getDocumentConfig(docExt);
  }

  public static PropertyDocument<?> loadRegistryResourceDocument(IFile file) {
    String docExt = file.getFileExtension();
    ResourceDocumentConfig rsrcConfig =
        ResourceDocumentConfigRegistry.getRegistryDocumentConfig(docExt);
    AbstractDocXmlPersist<?> xmlPersist =
        rsrcConfig.getDocXmlPersist(file, true);
    return (PropertyDocument<?>) xmlPersist.load(file.getRawLocationURI());
  }
}
