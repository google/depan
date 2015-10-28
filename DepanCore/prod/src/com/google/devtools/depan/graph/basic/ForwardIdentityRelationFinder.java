/*
 * Copyright 2013 The Depan Project Authors
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
package com.google.devtools.depan.graph.basic;

import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationFinder;

/**
 * Match all relations in the forward direction.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public class ForwardIdentityRelationFinder implements RelationFinder {

  // Only need one of these
  public static final ForwardIdentityRelationFinder FINDER =
      new ForwardIdentityRelationFinder();

  private ForwardIdentityRelationFinder() {
  }

  @Override
  public boolean match(Relation find) {
    // Really?
    return true;
  }

  @Override
  public boolean matchBackward(Relation find) {
    return false;
  }

  @Override
  public boolean matchForward(Relation find) {
    return true;
  }
}
