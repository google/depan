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

package com.google.devtools.depan.analysis_doc.model;

import com.google.devtools.depan.graph_doc.model.DependencyModel;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ModelAnalysisDocument<T> extends AnalysisDocument<T> {

  public static final String MODEL_KEY = "model";

  public ModelAnalysisDocument(String name, ModelMatcher matcher, T info) {
    super(name, matcher, info);
  }

  public ModelAnalysisDocument(
      String name, DependencyModel model, T info) {
    super(name, new FeatureMatcher(model), info);
  }

  public DependencyModel getModel() {

    ModelMatcher matcher = getMatcher();
    if (matcher instanceof FeatureMatcher) {
      return ((FeatureMatcher) matcher).getModel();
    }
    return null;
  }

  @Override
  public Object getObject(String key) {
    if (MODEL_KEY.equals(key)) {
      return getModel();
    }
    return super.getObject(key);
  }

  @Override
  protected boolean isWritable(String key) {
    if (MODEL_KEY.equals(key)) {
      return false;
    }
    return super.isWritable(key);
  }
}
