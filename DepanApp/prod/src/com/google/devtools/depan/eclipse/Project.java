/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * A class handling data in projects. Works for the current workspace.
 * Mainly a wrapper for {@link IProject}, giving ways to access to files in
 * the project.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public final class Project {

  /**
   * The current workspace.
   */
  private static IWorkspace workspace;

  /**
   * The workspace root.
   */
  private static IWorkspaceRoot myWorkspaceRoot;

  /**
   * A mapping from a project name to it's {@link Project} instance.
   */
  private static Map<String, Project> projectMap = Maps.newHashMap();

  // initialize static resources.
  static {
    workspace = ResourcesPlugin.getWorkspace();
    myWorkspaceRoot = workspace.getRoot();
  }

  /**
   * This project Eclipse instance.
   */
  private final IProject project;

  /**
   * @return a collection of {@link Project} in the current Workspace.
   */
  public static Collection<Project> getProjects() {
    Collection<Project> list = new ArrayList<Project>();
    for (IProject project : myWorkspaceRoot.getProjects()) {
      if (!project.isOpen()) {
        continue;
      }
      String name = project.getName();
      if (projectMap.containsKey(name)) {
        list.add(projectMap.get(name));
      } else {
        Project newProject = new Project(project);
        projectMap.put(name, newProject);
        list.add(newProject);
      }
    }
    return list;
  }

  /**
   * create a project wrapping the given {@link IProject}.
   * @param project IProject eclipse model.
   */
  private Project(IProject project) {
    this.project = project;
  }

  /**
   * List the files, returned as {@link IResource} under <code>this</code>
   * project. Only files matching the given extension are returned.
   *
   * @param extension
   * @return a collection of {@link IResource}s contained in the top-level
   *         project directory
   */
  public Collection<IResource> listFiles(String extension) {
    Collection<IResource> result = new ArrayList<IResource>();
    try {
      for (IResource resource : project.members()) {
        if (extension.equals(resource.getFileExtension())) {
          result.add(resource);
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * @return the project name.
   */
  public String getName() {
    return project.getName();
  }
}
