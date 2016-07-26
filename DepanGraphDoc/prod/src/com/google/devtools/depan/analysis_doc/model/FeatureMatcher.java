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

import java.util.List;

/**
 * Match a {@link DependencyModel} if any of the features are known
 * by this instance.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FeatureMatcher implements ModelMatcher {

  private final DependencyModel model;

  public FeatureMatcher(DependencyModel model) {
    this.model = model;
  }

  @Override
  public boolean forModel(DependencyModel check) {
    if (isListed(
        check.getNodeContribs(), model.getNodeContribs())) {
      return true;
    }
    if (isListed(
        check.getRelationContribs(), model.getRelationContribs())) {
      return true;
    }
    return false;
  }

  private boolean isListed(List<String> inCheck, List<String> inModel) {
    for (String contrib : inCheck) {
      if (inModel.contains(contrib)) {
        return true;
      }
    }
    return false;
  }

  public DependencyModel getModel() {
    return model;
  }
}
