/*
 * Copyright 2008 The Depan Project Authors
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

package com.google.devtools.depan.filters;

/**
 * Holds information about a {@link PathMatcher} such as recursiveness and
 * accumulativeness.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class PathMatcherTerm {
  /**
   * The <code>PathMatcher</code> object which is used as a filter.
   */
  private final PathMatcher term;
  
  /**
   * Denotes whether this <code>PathMatcher</code> term has to be applied
   * recursively.
   */
  private boolean recursive;
  
  /**
   * Denotes whether the output of a previous <code>PathMatcher</code> object
   * should be  combined with the output of this object.
   */
  private boolean cumulative;
  
  /**
   * Creates a new object with the filter specified by <code>pathMatcher</code>.
   * Depending on parameters, this term may be recursive and/or cumulative.
   * 
   * @param pathMatcher The <code>PathMatcher</code> object contained by this
   * term.
   * @param recursive Denotes whether this term must be applied recursively.
   * @param cumulative Denotes whether this term must be applied cumulatively.
   */
  public PathMatcherTerm(PathMatcher pathMatcher, boolean recursive,
      boolean cumulative) {
    this.term = pathMatcher;
    this.recursive = recursive;
    this.cumulative = cumulative;
  }

  /**
   * Returns whether this filter needs to be applied recursively.
   * 
   * @return True iff the filters in this object must be applied recursively. 
   */
  public boolean isRecursive() {
    return recursive;
  }
  
  /**
   * Sets whether this filter needs to be applied recursively.
   * 
   * @param newState The new value for recursiveness. 
   */
  public void setRecursive(boolean newState) {
    recursive = newState;
  }

  /**
   * Returns whether the output of these filters must include all nodes in the
   * input.
   * 
   * @return True iff the output of these filters must include all nodes in the 
   * input. 
   */
  public boolean isCumulative() {
    return cumulative;
  }
  
  /**
   * Sets whether the output of these filters must include all nodes in the 
   * input.
   * 
   * @param newState New state to copy all input nodes to the output. 
   */
  public void setCumulative(boolean newState) {
    cumulative = newState;
  }
  
  /**
   * Returns the String representation of the <code>PathMatcher</code> object.
   * 
   *  @return Returns the <code>String</code> representation of the
   *  <code>PathMatcher</code> object.
   */
  public String getDisplayName() {
    return term.getDisplayName();
  }

  /**
   * Returns associated <code>PathMatcher</code> object.
   * 
   * @return Associated <code>PathMatcher</code> object.
   */
  public PathMatcher getPathMatcher() {
    return term;
  }
}
