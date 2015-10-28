/*
 * Copyright 2007 The Depan Project Authors
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

import com.google.common.collect.Sets;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.graph.api.DirectedRelation;
import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * A DirectedRelationFinder allowing to match multiple Relations, with for each
 * of them, informations about the directions to match.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class MultipleDirectedRelationFinder
    implements DirectedRelationFinder, PathMatcher {

  protected Map<Relation, DirectedRelation> map =
      new HashMap<Relation, DirectedRelation>();
  
  /**
   * Default constructor which creates a
   * <code>MultipleDirectedRelationFinder</code> without any 
   * <code>Relation</code>s.
   */
  public MultipleDirectedRelationFinder() {
  }
  
  /**
   * Constructor which creates a finder whose filters are copied from 
   * <code>original</code>.
   * 
   * @param original The original object that contains the filters.
   */
  public MultipleDirectedRelationFinder(
      MultipleDirectedRelationFinder original) {
    Set<Entry<Relation, DirectedRelation>> entries = original.map.entrySet();
    for (Entry<Relation, DirectedRelation> entry : entries) {
      addRelation(entry.getKey(), entry.getValue().matchForward(),
          entry.getValue().matchBackward());
    }
  }
  
  /**
   * Add a match for the given relation, in the given direction. If the relation
   * is already included in the map, the new <code>forward</code> and
   * <code>backward</code> choices are or-ed with the previous settings.
   * 
   * @param rel the relations.
   * @param forward true to match in the forward direction.
   * @param backward true to match in the backward direction.
   */
  public void addRelation(Relation rel, boolean forward, boolean backward) {
    if (map.containsKey(rel)) {
      DirectedRelation d = map.get(rel);
      d.setMatchForward(d.matchForward() || forward);
      d.setMatchBackward(d.matchBackward() || backward);
    } else {
      map.put(rel, new BasicDirectedRelation(rel, forward, backward));          
    }
  }
  
  /**
   * Add a match for the given relation, in the given direction.
   * Any already defined match will be erased.
   * 
   * @param rel the relations.
   * @param forward true to match in the forward direction.
   * @param backward true to match in the backward direction.
   */
  public void addOrReplaceRelation(
      Relation rel, boolean forward, boolean backward) {
    map.put(rel, new BasicDirectedRelation(rel, forward, backward));          
  }

  @Override
  public boolean matchBackward(Relation find) {
    if (map.containsKey(find)) {
      return map.get(find).matchBackward();
    }
    return false;
  }

  @Override
  public boolean matchForward(Relation find) {
    if (map.containsKey(find)) {
      return map.get(find).matchForward();
    }
    return false;
  }
  
  /**
   * Returns a String representation of this object.
   * 
   * @return String representation of this object
   */
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Entry<Relation, DirectedRelation> entry : map.entrySet()) {
      DirectedRelation directedRelation = entry.getValue();
      String relationText = toString(directedRelation);

      if (!relationText.isEmpty()) {
        result.append(" ");
        result.append(relationText);
      }
    }
    return result.toString().trim();
  }
  
  /**
   * Pre-defined String constants used in 
   * <code>String toString(DirectedRelation directedRelation)</code>
   */
  private static final String USAGE_TEXT[] = {"", "F", "R", "F,R"};

  /**
   * Returns a String representation of the given {@link DirectedRelation}
   * object.
   * 
   * @param directedRelation
   * @return String representation of the given object
   */
  public static String toString(DirectedRelation directedRelation) {
    int usage = 0;
    if (directedRelation.matchForward()) {
      usage += 1;
    }
    if (directedRelation.matchBackward()) {
      usage += 2;
    }
    if (usage == 0) {
      return "";
    }

    Relation relation = directedRelation.getRelation();
    String result = "(" + relation.getForwardName() + ","
        + relation.getReverseName() + "," + USAGE_TEXT[usage] + ")";
    return result;
  }
  
  /**
   * Returns the display name of this object. The returned string is 
   * the same as the output of <code>String toString()</code>.
   * 
   *  @return Returns the <code>String</code> representation of this object.
   */
  @Override
  public String getDisplayName() {
    return toString();
  }
  
  /**
   * Computes a list of nodes that are the output of this filter, given a set of
   * nodes as input.
   * 
   * @param graph The model that contains the relations between nodes.
   * @param input A collection of <code>GraphNode</code> objects given as input.
   * @return A collection of <code>GraphNode</code> objects that is the result
   * of applying this filter to the input.
   */
  @Override
  public Collection<GraphNode> nextMatch(
      GraphModel graph, Collection<GraphNode> input) {
    Collection<GraphNode> result = Sets.newHashSet();

    Iterable<GraphNode> relatedNodes = graph.getRelated(input, this);

    for (GraphNode node : relatedNodes) {
      result.add(node);
    }
    return result;
  }
}
