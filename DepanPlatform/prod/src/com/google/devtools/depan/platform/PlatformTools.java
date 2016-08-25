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

package com.google.devtools.depan.platform;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PlatformTools {

  private PlatformTools() {
    // Prevent instantiation.
  }

  public static void throwCoreException(
      String message, String pluginId) throws CoreException {
    IStatus status = new Status(
        IStatus.ERROR, pluginId, IStatus.OK, message, null);
    throw new CoreException(status);
  }

  /**
   * Provide the text of the last resource segment,
   * with any extension removed.
   */
  public static String getBaseName(IResource resouce) {
    String name = resouce.getName();
    String ext = resouce.getFileExtension();
    // If null, no period is present
    if (null == ext) {
      return name;
    }
    // remove period and extension from end
    return name.substring(0, name.length() - 1 - ext.length());
  }
}
