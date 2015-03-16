/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.elementkinds.ElementKindDescriptor;
import com.google.devtools.depan.eclipse.utils.elementkinds.PsuedoElementKindDescriptor;
import com.google.devtools.depan.model.GraphNode;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Compute the number of instances for each type of node.
 * 
 * <p>The {@link #incrStats(Collection)} method can be used repeatedly to
 * build up statistics for disjoint sets of method types.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ElementKindStats {

  /**
   * Provide the counts for each element kind.
   */
  public interface Info {
    int getCount();
    ElementKindDescriptor getKind();
  }

  private class BasicInfo implements Info {

    private final ElementKindDescriptor kind;
    private final int count;

    public BasicInfo(ElementKindDescriptor kind, int count) {
      this.kind = kind;
      this.count = count;
    }

    @Override
    public int getCount() {
      return count;
    }

    @Override
    public ElementKindDescriptor getKind() {
      return kind;
    }
  }

  /** Artificial element kind for total element counts. */
  private ElementKindDescriptor TOTAL_NODES =
      new PsuedoElementKindDescriptor("Total", getDepanPlugin());

  /** Artificial element kind for unknown element kind counts. */
  private ElementKindDescriptor OTHER_NODES =
      new PsuedoElementKindDescriptor("Other", getDepanPlugin());

  private int totalCount = 0;

  private int otherCount = 0;

  private final Map<ElementKindDescriptor, Integer> kindCounts;

  private static SourcePlugin getDepanPlugin() {
    return SourcePluginRegistry.getSourcePlugin(Resources.PLUGIN_ID);
  }

  /**
   * Create an element kind statistics object that computes the number
   * of instances for the indicated set of element kinds.
   * 
   * @param kinds Element kinds to count occurrences.
   */
  public ElementKindStats(Collection<ElementKindDescriptor> kinds) {
    this.kindCounts = Maps.newHashMap();
    for (ElementKindDescriptor kind : kinds) {
      kindCounts.put(kind, 0);
    }
  }

  /**
   * Update the current statistics for the new set of analysis nodes.
   * 
   * @param nodes group of nodes to increment analysis counts.
   */
  public void incrStats(Collection<GraphNode> nodes) {
    totalCount += nodes.size();

    for (GraphNode node : nodes) {
      ElementKindDescriptor descr = findDescriptor(node);
      if (null == descr) {
        otherCount++;
      }
      else {
        int count = kindCounts.get(descr);
        kindCounts.put(descr, count + 1);
      }
    }
  }

  /**
   * Provide a complete list of analysis results, including total and other
   * element kind counts.  The results are suitable for display with an
   * {@link ElementKindStatsView}.
   * 
   * @return per-element kind list of node counts.
   */
  public Collection<Info> createStats() {
    List<Info> result = Lists.newArrayList();
    result.add(new BasicInfo(TOTAL_NODES, totalCount));
    result.add(new BasicInfo(OTHER_NODES, otherCount));
    for (Map.Entry<ElementKindDescriptor, Integer> entry : kindCounts.entrySet()) {
      result.add(new BasicInfo(entry.getKey(), entry.getValue()));
    }
    return result;
  }

  /**
   * Determine which element kind to associate with a node.  Each node is
   * associated with first descriptor to which it can be assigned
   * ({@code isAssignmableFrom()}).
   * 
   * @param node node to find
   * @return descriptor for node kind, or {@code null} if none in analysis
   *     set.
   */
  private ElementKindDescriptor findDescriptor(
      GraphNode node) {
    for (ElementKindDescriptor kind : kindCounts.keySet()) {
      if (kind.getElementKind().isAssignableFrom(node.getClass())) {
        return kind;
      }
    }
    return null;
  }
}
