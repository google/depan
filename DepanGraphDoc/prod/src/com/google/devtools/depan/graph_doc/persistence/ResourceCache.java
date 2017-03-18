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

package com.google.devtools.depan.graph_doc.persistence;

import com.google.devtools.depan.graph_doc.model.GraphDocument;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import java.net.URI;
import java.util.Map;

/**
 * Cache large resources (mostly .dgi files) so we don't have to reload
 * them on every reference.
 *
 * <p>Uses workspace relative IPaths obtained from the IFile as keys for the
 * loaded GraphDocumentss.  Uses raw resource URIs obtained from the IFile if
 * the graph needs to be loaded.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ResourceCache implements IResourceChangeListener {

  /**
   * Cache of previously loaded graph documents.
   */
  private Map<IPath, GraphDocument> loadedGraphs = Maps.newHashMap();

  /////////////////////////////////////
  // GraphDocument cache
  private static ResourceCache INSTANCE = new ResourceCache();

  static {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    INSTANCE.attachWorkspace(workspace);
  }

  protected GraphDocument retrieveGraphDocument(IFile file) {
    return loadedGraphs.get(file.getFullPath());
  }

  protected void installGraphDocument(IFile file, GraphDocument graph) {
    loadedGraphs.put(file.getFullPath(), graph);
  }

  public static GraphDocument loadGraphDocument(IFile file) {
    GraphModelXmlPersist loader = GraphModelXmlPersist.build(true);
    return loader.load(file.getRawLocationURI());
  }

  public static void saveGraphDocument(IFile file, GraphDocument graph) {
    GraphModelXmlPersist persist = GraphModelXmlPersist.build(false);
    persist.saveDocument(file, graph, null);
  }

  /**
   * Provide the graph document located for the given file.
   * If the file has already be loaded, the graph document is provided from an
   * internal cache.  If the file has not been loaded, read it from the
   * location and also add it to the internal cache.
   * 
   * @param file location for the graph document
   * @return graph document obtained from the location
   */
  public static GraphDocument fetchGraphDocument(IFile file) {
    return INSTANCE.getGraphDocument(file);
  }

  /**
   * Provide the graph document located at the given file.
   * If the files has already be loaded, the graph document is provided from an
   * internal cache.  If the file has not been loaded, read it from the location
   * but do not add it to the internal cache.
   * 
   * @param uri location (and cache key) for the graph document
   * @return graph document provided by the uri
   */
  public static GraphDocument importGraphDocument(IFile file) {
    return INSTANCE.provideGraphDocument(file);
  }

  /**
   * Write a new graph document into both the file system and the cache.
   * 
   * @param file location for the graph document
   * @param graph graph to save
   */
  public static void storeGraphDocument(IFile file, GraphDocument graph)
      throws CoreException {
    INSTANCE.insertGraphDocument(file, graph);
  }

  /**
   * Provide the graph document, adding it to the cache if it isn't there.
   * 
   * @param file location for the graph document
   * @return graph document provided by the location
   */
  protected GraphDocument getGraphDocument(IFile file) {
    GraphDocument result = retrieveGraphDocument(file);

    if (null == result) {
      result = loadGraphDocument(file);
      installGraphDocument(file, result);
    }

    return result;
  }

  /**
   * Provide the graph document without adding it to the cache.  If it is in the
   * cache, use that rather then re-reading it from the URI.
   * 
   * @param file location for the graph document
   * @return graph document provided by the location
   */
  protected GraphDocument provideGraphDocument(IFile file) {
    GraphDocument result = retrieveGraphDocument(file);

    if (null != result) {
      return result;
    }

    return loadGraphDocument(file);
  }

  /**
   * Write a new graph document into both the file system and the cache.
   * @param file location for the graph document
   * @param graph graph to save
   */
  protected void insertGraphDocument(IFile file, GraphDocument graph)
      throws CoreException {
    saveGraphDocument(file, graph);
    installGraphDocument(file, graph);
  }

  protected void attachWorkspace(IWorkspace workspace) {
    workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
  }

  protected void releaseWorkspace(IWorkspace workspace) {
    workspace.removeResourceChangeListener(this);
  }

  @Override
  public void resourceChanged(IResourceChangeEvent event) {
    try {
      IResourceDeltaVisitor visitor = new ForgetResources();
      event.getDelta().accept(visitor);
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Forget any cached property about any changed resource.
   * As needs expand, this may need to be more refined about how changes
   * are handled.
   *
   * One open area is notifying viewers, etc. when a resource has changed.
   * With this change, we might continue to have editors with stale data,
   * but reopening a resource should always provide the most current data.
   */
  private class ForgetResources implements IResourceDeltaVisitor {

    @Override
    public boolean visit(IResourceDelta delta) {
      URI resourceKey = delta.getResource().getLocationURI();
      loadedGraphs.remove(resourceKey);
      return true;
    }
  }
}
