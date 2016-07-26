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
import com.google.devtools.depan.resources.PropertyDocument;

/**
 * Document type for analysis objects that are tied to a set of relation and
 * node contributions.
 *
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class AnalysisDocument<T> extends PropertyDocument<T> {

  public static final String INFO_KEY = "info";

  private final T info;

  private ModelMatcher matcher;

  public AnalysisDocument(String name, ModelMatcher matcher, T info) {
    super(name);
    this.matcher = matcher;
    this.info = info;
  }

  public T getInfo() {
    return info;
  }

  public boolean forModel(DependencyModel model) {
    return matcher.forModel(model);
  }

  @Override
  public Object getObject(String key) {
    if (INFO_KEY.equals(key)) {
      return getInfo();
    }
    return super.getObject(key);
  }

  @Override
  protected boolean isWritable(String key) {
    if (INFO_KEY.equals(key)) {
      return false;
    }
    return super.isWritable(key);
  }

  protected ModelMatcher getMatcher() {
    return matcher;
  }
}
