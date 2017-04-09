/**
 * 
 */
package com.google.devtools.depan.relations.models;

import com.google.devtools.depan.analysis_doc.model.ModelMatcher;
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
      "Empty", ModelMatcher.ALL_MODELS, RelationSets.EMPTY);

  public static RelationSetDescriptor ALL = new RelationSetDescriptor(
      "All", ModelMatcher.ALL_MODELS, RelationSets.ALL);
}
