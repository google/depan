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

package com.google.devtools.depan.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Resource tree element that contains other resources.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ResourceContainer {

  // Only "the" root should have a null parent
  private ResourceContainer parent;

  private String label;

  private final Map<String, ResourceContainer> children =
      Maps.newHashMap();

  private final Map<String, Object> resources =
      Maps.newHashMap();

  private ResourceContainer(ResourceContainer parent, String label) {
    this.parent = parent;
    this.label = label;
  }

  public boolean isRoot() {
    return null == parent;
  }

  public ResourceContainer getParent() {
    return parent;
  }

  public String getLabel() {
    return label;
  }

  private boolean validLabel(String label) {
    if (children.containsKey(label)) {
      return false;
    }
    if (resources.containsKey(label)) {
      return false;
    }
    return true;
  }

  /**
   * Factory for the resource root.  Expected to be called once from
   * {@link AnalysisResources}.  All other {@link ResourceContainer}s should
   * be accessed from this instance, or created via {@link #addChild(String)}.
   */
  public static ResourceContainer buildRootContainer(String label) {
    return new ResourceContainer(null, label);
  }

  public ResourceContainer addChild(String label) {
    if (validLabel(label)) {
      ResourceContainer result = new ResourceContainer(this, label);
      children.put(label, result);
      return result;
    }
    return null;
  }

  public Object addResource(String label, Object resource) {
    if (validLabel(label)) {
      resources.put(label, resource);
      return resource;
    }
    return null;
  }

  public Object addResource(PropertyDocument<?> resource) {
    return addResource(resource.getName(), resource);
  }

  public ResourceContainer getChild(String label) {
    return children.get(label);
  }

  public Collection<ResourceContainer> getChildren() {
    return ImmutableList.copyOf(children.values());
  }

  public Object getResource(String label) {
    return resources.get(label);
  }

  public Collection<Object> getResources() {
    return ImmutableList.copyOf(resources.values());
  }

  public IPath getPath() {
    return getPath(this);
  }

  /**
   * Convert the {@link ResourceContainer} into an Eclipse Project relative
   * folder path.
   * 
   * As a static method, it's much harder to inadvertently alter the state
   * of the instance.
   */
  public static IPath getPath(ResourceContainer container) {
    IPath result = Path.fromOSString(container.getLabel());
    ResourceContainer pathRsrc = container.getParent();
    while (null != pathRsrc) {
      IPath front = Path.fromOSString(pathRsrc.getLabel());
      pathRsrc = pathRsrc.getParent();
      result = front.append(result);
    }

    return result;
  }

  /**
   * Provide {@link ResourceContainer} from supplied {@code refPath}.
   * The {@code refPath} must name a child container, not a resource.
   * 
   * @return {@code null} if {@code refPath} is not a valid descendant.
   */
  public ResourceContainer getFromPath(IPath refPath) {
    ResourceContainer result = this;
    String[] segments = refPath.segments();
    if (segments.length < 1) {
      return null;
    }
    if (!(result.getLabel().equals(segments[0]))) {
      return null;
    }
    List<String> children =
        Arrays.asList(segments).subList(1, segments.length);
    for (String segment : children) {
      result = result.getChild(segment);
      if (null == result) {
        return null;
      }
    }

    return result;
  }
}
