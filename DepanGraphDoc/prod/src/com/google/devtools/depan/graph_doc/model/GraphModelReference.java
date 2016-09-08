/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.graph_doc.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Define a pairing between a graphModel and a file.  A custom converter that
 * is installed with {@code ObjectXmlPersist} serializes instances using
 * only the resource location.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class GraphModelReference {
  private final String path;
  private final GraphDocument graph;

  public GraphModelReference(String path, GraphDocument graph) {
    this.path = path;
    this.graph = graph;
  }

  public GraphModelReference(IFile location, GraphDocument graph) {
    this(location.getFullPath().toPortableString(), graph);
  }

  public String getGraphPath() {
    return path;
  }

  /**
   * @return the location
   */
  public IFile getLocation() {
    return getLocation(path);
  }

  /**
   * @return the graph
   */
  public GraphDocument getGraph() {
    return graph;
  }

  /**
   * @return the location
   */
  public static IFile getLocation(String path) {
    return (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
  }
}
