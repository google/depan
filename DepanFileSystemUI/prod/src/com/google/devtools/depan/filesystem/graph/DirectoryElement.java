/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.filesystem.graph;


import java.io.File;

/**
 * {@link FileSystemElement} that represents a directory.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class DirectoryElement extends FileSystemElement {
  /**
   * Absolute path to this directory.
   */
  private final String path;

  /**
   * Constructs a <code>DirectoryElement</code> using the absolute path provided
   * as a parameter.
   *
   * @param path Absolute path to the directory.
   */
  public DirectoryElement(String path) {
    this.path = path;
  }

  @Override
  public String getPath() {
    return path;
  }

  /**
   * Returns the directory name of this object.
   *
   * @return Directory name of this object.
   */
  @Override
  public String friendlyString() {
    return new File(getPath()).getName();
  }

  @Override
  public void accept(FileSystemElementVisitor visitor) {
    visitor.visitDirectoryElement(this);
  }

  /**
   * Uses path to create a hashCode.
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getPath().hashCode();
  }

  /**
   * Two {@link DirectoryElement}s are equals iff their paths are equals.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DirectoryElement) {
      return getPath().equals(((DirectoryElement) obj).getPath());
    }
    return super.equals(obj);
  }

  /**
   * Returns the <code>String</code> representation of this object.
   *
   * @return <code>String</code> representation of this object.
   */
  @Override
  public String toString() {
    return "Dir " + getPath();
  }
}
