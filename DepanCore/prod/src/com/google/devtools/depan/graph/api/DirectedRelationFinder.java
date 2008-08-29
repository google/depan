/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.graph.api;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface DirectedRelationFinder {

  /**
   * match a relation in the forward direction.
   * 
   * @param find relation to match.
   * @return true if the relation passed the filter.
   */
  public boolean matchForward(Relation find);
  
  /**
   * match a relation in the backward direction.
   * 
   * @param find relation to match.
   * @return true if the relation passed the filter.
   */
  public boolean matchBackward(Relation find);
  
}
