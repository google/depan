// Copyright 2010 The Depan Project Authors

package com.google.devtools.depan.filesystem.builder;

import java.io.File;

/**
 * Basic implementation of {@link PathInfo} for {@code File} like objects
 * that have a file-system directory as their parent.
 *
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractFilePathInfo implements PathInfo {
  private final File path;

  public AbstractFilePathInfo(File path) {
    this.path = path;
  }

  /**
   * Allow derived types to obtain the instance's path.
   * 
   * @return path for instance
   */
  protected File getFilePath() {
    return path;
  }

  @Override
  public String getId() {
    return createNode().getId();
  }

  @Override
  public PathInfo getParentInfo() {
    File parent = path.getParentFile();
    if (null == parent) {
      return null;
    }
    return new DirectoryPathInfo(parent);
  }
}
