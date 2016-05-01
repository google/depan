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

package com.google.devtools.depan.platform;

import com.google.common.collect.Maps;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import java.util.Map;

/**
 * Generate EditorPart names for new editors.
 * 
 * Based on a View specific version ({@code ViewModelHelper}) originally
 * implemented by ycoppel@google.com (Yohann Coppel).
 */
public class NewEditorHelper {

  /** Prevent instantiation of this singleton. */
  private NewEditorHelper() {
  }

  /**
   * Cache of known entity labels.  The mapped integer is the next count
   * to use for another similarly named entity.  It start at one when an
   * entity is first seen, so the original get a name without a count and the
   * next one gets a label with a suffix " 1".
   */
  private static final Map<String, Integer> labelCounts =
      Maps.newHashMap();

  /**
   * Generate a temporary label for a new instance of an editor.  This value
   * should never be used to persist the editor data; a different mechanism
   * should be used to generate good persistent named.
   * <p>
   * This implementation gives the first label without a number.  Successive
   * label get a number appended.
   * <p>
   * In a rich interface, it would be tempting to add formatting options.
   * 
   * @param baseLabel
   * @return Editor name that should be unique for this execution
   */
  public static String newEditorLabel(String baseLabel) {
    Integer count = labelCounts.get(baseLabel);
    if (null == count) {
      count = 1;
      labelCounts.put(baseLabel, count);
      return baseLabel;
    }

    // Affix a number if this label has been seen before
    String result = baseLabel + " " + count;
    labelCounts.put(baseLabel, count + 1);
    return result;
  }

  /**
   * Ensure that we have a file extension on the file name.  This does not
   * overwrite an existing extension, but ensures that at least one extension
   * is present.
   * 
   * @param savePath Initial save path from user
   * @param defExt default extension to add if {@code savePath} lacks one
   * @return valid IFile with an extension.
   */
  public static IFile buildNameWithExtension(IPath savePath, String defExt) {
    if (null == savePath.getFileExtension()) {
      savePath.addFileExtension(defExt);
    }
    return ResourcesPlugin.getWorkspace().getRoot().getFile(savePath);
  }
}
