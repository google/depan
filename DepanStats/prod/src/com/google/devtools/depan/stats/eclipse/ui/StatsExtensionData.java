/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.stats.eclipse.ui;

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.stats.jung.JungStatistics;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.model.AbstractExtensionData;
import com.google.devtools.depan.view_doc.model.ExtensionData;

/**
 * Define the persistent user options and data the node statistics extension
 * contributes to the view document.
 * 
 * @author Lee Carver
 */
public class StatsExtensionData extends AbstractExtensionData {

  // The data selectors to save as part of the view.
  private PropertyDocumentReference<GraphEdgeMatcherDescriptor> statsMatcher;

  private transient JungStatistics stats;

  public StatsExtensionData(ViewExtension extension, Object instance) {
    super(extension, instance);
  }

  public static void setStatsData(ViewEditor editor, StatsExtensionData data) {
    StatsViewExtension ext = StatsViewExtension.getInstance();
    editor.setExtensionData(ext, null, data);
  }

  public static StatsExtensionData getStatsData(ViewEditor editor) {
    StatsViewExtension ext = StatsViewExtension.getInstance();
    if (null != ext) {
      return (StatsExtensionData) editor.getExtensionData(ext);
    }
    return null;
  }

  public static StatsExtensionData getStatsData(
      ViewEditor editor, ViewExtension extension) {
    ExtensionData result = editor.getExtensionData(extension);
    if (null == result) {
      result = buildExtensionData(editor, extension);
      editor.setExtensionData(extension, null, result);
    }
    return (StatsExtensionData) result;
  }

  private static StatsExtensionData buildExtensionData(
      ViewEditor editor, ViewExtension extension) {
    StatsExtensionData result = new StatsExtensionData(extension, null);
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcherRef =
        editor.getLayoutEdgeMatcherRef();
    result.setStatsMatcherRef(matcherRef);
    return result;
  }

  /////////////////////////////////////
  // Public API

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor>
  getStatsMatcherRef() {
    return statsMatcher;
  }

  public void setStatsMatcherRef(
      PropertyDocumentReference<GraphEdgeMatcherDescriptor> statsMatcher) {
    this.statsMatcher = statsMatcher;
  }

  public void calcJungStatistics(GraphModel model) {
    GraphEdgeMatcherDescriptor doc = statsMatcher.getDocument();
    stats = JungStatistics.build(model, doc);
  }

  public JungStatistics getJungStatistics() {
    return stats;
  }
}
