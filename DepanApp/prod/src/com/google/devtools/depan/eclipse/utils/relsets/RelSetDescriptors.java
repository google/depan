/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.utils.relsets;

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.Project;
import com.google.devtools.depan.eclipse.editors.GraphDocument;
import com.google.devtools.depan.eclipse.editors.ViewDocument;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.core.resources.IResource;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Define RelSetDescriptors for all of the known cases, such as built-in,
 * saved, or temporary.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class RelSetDescriptors {

  // No instantiations of this name-space class.
  private RelSetDescriptors() {
  }

  /**
   * Basic implementation of {@code RelationshipSetDescriptor} which assumes a
   * known RelationshipSet, but the naming is left abstract.
   */
  protected abstract static class AbstractRelSetDescriptor
      implements RelSetDescriptor {
    private final RelationshipSet relSet;

    public AbstractRelSetDescriptor(RelationshipSet relSet) {
      this.relSet = relSet;
    }

    @Override
    public RelationshipSet getRelSet() {
      return relSet;
    }
  }

  /**
   * A {@link RelationshipSetDescriptor} representing a built-in
   * {@link RelationshipSet}
   */
  protected static class BuiltinRelSetDescriptor
      extends AbstractRelSetDescriptor {
    public BuiltinRelSetDescriptor(RelationshipSet set) {
      super(set);
    }

    @Override
    public String getName() {
      return "* " + getRelSet().getName();
    }
  }

  /**
   * A {@link RelationshipSetDescriptor} that represents a
   * {@link RelationshipSet} saved on disk.
   */
  protected static class SavedRelSetDescriptor
      extends AbstractRelSetDescriptor {
    final String path;

    public SavedRelSetDescriptor(String pathToFile, RelationshipSet relSet) {
      super(relSet);
      this.path = pathToFile;
    }

    @Override
    public String getName() {
      return "+ " + getRelSet().getName() + " (" + path + ")";
    }

    /**
     * Generate hash code based on name and path.
     */
    @Override
    public int hashCode() {
      RelationshipSet relSet = getRelSet();
      int hash = 1;
      hash = hash * 31 + (null == path ? 0 : path.hashCode());
      hash = hash * 31 + (null == relSet ? 0 : relSet.getName().hashCode());
      return hash;
    }

    /*
     * Determine equality based on name and path.
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SavedRelSetDescriptor) {
        SavedRelSetDescriptor that = (SavedRelSetDescriptor) obj;
        return that.path.equals(this.path)
            && that.getRelSet().getName().equals(this.getRelSet().getName());
      }
      return super.equals(obj);
    }
  }

  /**
   * A {@link RelationshipSetDescriptor} representing a temporary 
   * {@link RelationshipSet}; i.e. which is lost when the program is closed.
   */
  protected static class TempRelSetDescriptor
      extends AbstractRelSetDescriptor {
    public final String name;

    public TempRelSetDescriptor(String name, RelationshipSet relSet) {
      super(relSet);
      this.name = name;
    }

    @Override
    public String getName() {
      return "// " + name;
    }
  }

  /////////////////////////////////////
  // Builders for RelSetDescriptors

  public static List<RelSetDescriptor> buildGraphChoices(
      GraphDocument graphInfo) {
    List<RelSetDescriptor> result = Lists.newArrayList();
    addKnownSets(result, graphInfo.getBuiltinAnalysisRelSets());
    return result;
  }

  public static List<RelSetDescriptor> buildViewChoices(
      ViewDocument viewInfo) {
    List<RelSetDescriptor> result = Lists.newArrayList();
    addKnownSets(result, viewInfo.getBuiltinAnalysisRelSets());
    return result;
  }

  public static void addKnownSets(
      List<RelSetDescriptor> relsets,
      Collection<RelationshipSet> builtinAnalysisRelSets) {
    addBuiltinRelSets(relsets, builtinAnalysisRelSets);
    addProjectRelSets(relsets);
    // addTemporaryRelSets(relsets);
  }

  /**
   * Add all the given sets as temporary sets.
   * 
   * @param sets a map from a relationshipSet to its "title".
   */
  public static void addTemporaryRelSets(
      List<RelSetDescriptor> relsets,
      Map<RelationshipSet, String> tempRelSets) {
    for (Map.Entry<RelationshipSet, String> entries : tempRelSets.entrySet()) {
      relsets.add(new TempRelSetDescriptor(entries.getValue(), entries.getKey()));
    }
  }

  /**
   * Fill the drop down list with data found in the workspace, in any project.
   */
  public static void addBuiltinRelSets(
      List<RelSetDescriptor> relsets,
      Collection<RelationshipSet> builtinRelSets) {

    // Populate list of relsets with the built-in rel-set choices.
    for (RelationshipSet set : builtinRelSets) {
      relsets.add(new BuiltinRelSetDescriptor(set));
    }
  }

  /**
   * Fill the drop down list with data found in the workspace, in any project.
   */
  public static void addProjectRelSets(
      List<RelSetDescriptor> relsets
      ) {

    // Add in the user defined rel-sets.
    RelSetXmlPersist relSetLoader = new RelSetXmlPersist();
    Collection<Project> projects = Project.getProjects();
    for (Project project : projects) {
      // get the list of files ending with ".dpans"
      for (IResource resource :
          project.listFiles(RelSetXmlPersist.RELATION_SET_EXT)) {
        final URI relSetUri = resource.getLocationURI();
        Collection<RelationshipSet> loadedRelsets =
            relSetLoader.load(relSetUri);
        for (RelationshipSet relset : loadedRelsets) {
          relsets.add(new SavedRelSetDescriptor(relSetUri.toString(), relset));
        }
      }
    }
  }

  /**
   * @param input
   * @param instanceSet
   */
  public static List<RelSetDescriptor> addTemporaryRelSet(
      List<RelSetDescriptor> baseRelSets, String relSetName,
      RelationshipSet tempRelSet) {
    List<RelSetDescriptor> result = Lists.newArrayList(baseRelSets);
    result.add(new TempRelSetDescriptor(relSetName, tempRelSet));
    return result;
    
  }
}
