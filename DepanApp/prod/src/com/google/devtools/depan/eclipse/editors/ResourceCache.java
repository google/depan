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

import com.google.devtools.depan.collect.Maps;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.XmlPersistentGraph;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import java.net.URI;
import java.util.Map;

/**
 * Cache large resources (mostly .dpang files) so we don't have to reload
 * them on every reference.
 *
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
public class ResourceCache implements IResourceChangeListener {

  /////////////////////////////////////
  // GraphModel cache
  private static ResourceCache INSTANCE = new ResourceCache();
  
  static {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    INSTANCE.attachWorkspace(workspace);
    
  }

  public static GraphModel fetchGraphModel(URI uri) {
    return INSTANCE.getGraphModel(uri);
  }

  private Map<URI, GraphModel> loadedGraphs = Maps.newHashMap();

  protected GraphModel getGraphModel(URI uri) {
    GraphModel result = loadedGraphs.get(uri);

    if (null == result) {
      XmlPersistentGraph loader = new XmlPersistentGraph();
      result = loader.load(uri);
      loadedGraphs.put(uri, result);
    }

    return result;
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
