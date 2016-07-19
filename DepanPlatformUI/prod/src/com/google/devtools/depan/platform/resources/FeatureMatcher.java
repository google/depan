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

package com.google.devtools.depan.platform.resources;

import com.google.devtools.depan.graph_doc.model.DependencyModel;

import java.util.Collection;

/**
 * Match a {@link DependencyModel} if any of the features are known
 * by this instance.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class FeatureMatcher implements ModelMatcher {

  private final Collection<String> nodeFeatures;
  private final Collection<String> relationFeatures;

  public FeatureMatcher(
      Collection<String> nodeFeatures,
      Collection<String> relationFeatures) {
    this.nodeFeatures = nodeFeatures;
    this.relationFeatures = relationFeatures;
  }

  @Override
  public boolean forModel(DependencyModel model) {
    if (checkNodes(model)) {
      return true;
    }
    if (checkRelations(model)) {
      return true;
    }

    return false;
  }

  public boolean checkRelations(DependencyModel model) {
    for (String contrib : model.getRelationContribs()) {
      if (relationFeatures.contains(contrib)) {
        return true;
      }
    }
    return false;
  }


  public boolean checkNodes(DependencyModel model) {
    for (String contrib : model.getNodeContribs()) {
      if (nodeFeatures.contains(contrib)) {
        return true;
      }
    }
    return false;
  }

}
