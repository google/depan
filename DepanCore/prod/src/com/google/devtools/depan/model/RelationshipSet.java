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

package com.google.devtools.depan.model;

import java.util.Collection;
import java.util.Collections;

import com.google.devtools.depan.graph.api.DirectedRelationFinder;
import com.google.devtools.depan.graph.api.Relation;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface RelationshipSet extends DirectedRelationFinder {

  /**
   * Empty <code>RelationshipSet</code> to denote empty sets of relationships.
   */
  public static final RelationshipSet EMTPY = new RelationshipSet() {
    /**
     * Returns an empty set.
     *
     * @return Empty Set of <code>Relation</code>s.
     */
    @Override
    public Collection<Relation> getBackwardRelations() {
      return Collections.emptySet();
    }

    /**
     * Returns an empty set.
     *
     * @return Empty Set of <code>Relation</code>s.
     */
    @Override
    public Collection<Relation> getForwardRelations() {
      return Collections.emptySet();
    }

    /**
     * Returns an empty <code>String</code>.
     *
     * @return Empty <code>String</code>.
     */
    @Override
    public String getName() {
      return "None";
    }

    /**
     * This operation is not supported. Throws an
     * <code>UnsupportedOperationException</code> if called.
     */
    @Override
    public void setMatchBackward(Relation relation, boolean isMatching) {
      throw new UnsupportedOperationException();
    }

    /**
     * This operation is not supported. Throws an
     * <code>UnsupportedOperationException</code> if called.
     */
    @Override
    public void setMatchForward(Relation relation, boolean isMatching) {
      throw new UnsupportedOperationException();
    }

    /**
     * Returns <code>false</code>.
     *
     * @return False.
     */
    @Override
    public boolean matchBackward(Relation find) {
      return false;
    }

    /**
     * Returns <code>false</code>.
     *
     * @return False.
     */
    @Override
    public boolean matchForward(Relation find) {
      return false;
    }
  };

  public String getName();

  public Collection<Relation> getForwardRelations();
  public Collection<Relation> getBackwardRelations();

  public void setMatchForward(Relation relation, boolean isMatching);
  public void setMatchBackward(Relation relation, boolean isMatching);
}
