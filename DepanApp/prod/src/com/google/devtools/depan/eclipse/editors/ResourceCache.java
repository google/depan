/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.eclipse.editors;

import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.persist.GraphModelXmlPersist;
import com.google.devtools.depan.model.GraphModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
 * Cache large resources (mostly .dpang files) so we don't have to reload
 * them on every reference.
 * <p>
 * Uses workspace relative IPaths obtained from the IFile as keys for the
 * loaded GraphModels.  Uses raw resource URIs obtained from the IFile if
 * the graph needs to be loaded.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ResourceCache implements IResourceChangeListener {

  /**
   * Cache of previously loaded graph models.
   */
  private Map<IPath, GraphModel> loadedGraphs = Maps.newHashMap();

  /////////////////////////////////////
  // GraphModel cache
  private static ResourceCache INSTANCE = new ResourceCache();

  static {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    INSTANCE.attachWorkspace(workspace);
  }

  protected GraphModel retrieveGraphModel(IFile file) {
    return loadedGraphs.get(file.getFullPath());
  }

  protected void installGraphModel(IFile file, GraphModel graph) {
    loadedGraphs.put(file.getFullPath(), graph);
  }

  public static GraphModel loadGraphModel(IFile file) {
    GraphModelXmlPersist loader = new GraphModelXmlPersist();
    return loader.load(file.getRawLocationURI());
  }

  public static void saveGraphModel(IFile file, GraphModel graph)
      throws CoreException {
    GraphModelXmlPersist loader = new GraphModelXmlPersist();
    loader.save(file.getRawLocationURI(), graph);
    file.refreshLocal(IResource.DEPTH_ZERO, null);
  }

  /**
   * Provide the graph model located for the given file.
   * If the file has already be loaded, the graph model is provided from an
   * internal cache.  If the file has not been loaded, read it from the
   * location and also add it to the internal cache.
   * 
   * @param file location for the graph model
   * @return graph model obtained from the location
   */
  public static GraphModel fetchGraphModel(IFile file) {
    return INSTANCE.getGraphModel(file);
  }

  /**
   * Provide the graph model located at the given file.
   * If the files has already be loaded, the graph model is provided from an
   * internal cache.  If the file has not been loaded, read it from the location
   * but do not add it to the internal cache.
   * 
   * @param uri location (and cache key) for the graph model
   * @return graph model provided by the uri
   */
  public static GraphModel importGraphModel(IFile file) {
    return INSTANCE.provideGraphModel(file);
  }

  /**
   * Write a new graph model into both the file system and the cache.
   * @param file location for the graph model
   * @param graph graph to save
   */
  public static void storeGraphModel(IFile file, GraphModel graph)
      throws CoreException {
    INSTANCE.insertGraphModel(file, graph);
  }

  /**
   * Provide the graph model, adding it to the cache if it isn't there.
   * 
   * @param file location for the graph model
   * @return graph model provided by the location
   */
  protected GraphModel getGraphModel(IFile file) {
    GraphModel result = retrieveGraphModel(file);

    if (null == result) {
      result = loadGraphModel(file);
      installGraphModel(file, result);
    }

    return result;
  }

  /**
   * Provide the graph model without adding it to the cache.  If it is in the
   * cache, use that rather then re-reading it from the URI.
   * 
   * @param file location for the graph model
   * @return graph model provided by the location
   */
  protected GraphModel provideGraphModel(IFile file) {
    GraphModel result = retrieveGraphModel(file);

    if (null != result) {
      return result;
    }

    return loadGraphModel(file);
  }

  /**
   * Write a new graph model into both the file system and the cache.
   * @param file location for the graph model
   * @param graph graph to save
   */
  protected void insertGraphModel(IFile file, GraphModel graph)
      throws CoreException {
    saveGraphModel(file, graph);
    installGraphModel(file, graph);
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
