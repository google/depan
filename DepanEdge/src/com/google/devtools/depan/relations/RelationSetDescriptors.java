/**
 * 
 */
package com.google.devtools.depan.relations;

import com.google.devtools.depan.model.RelationSets;

/**
 * @author Standard Lee
 *
 */
public class RelationSetDescriptors {

  private RelationSetDescriptors() {
    // Prevent instantiation
  }

  public static RelationSetDescriptor EMPTY = new RelationSetDescriptor(
      "Empty", RelationSets.EMPTY);
}
