/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.filesystem;

import com.google.common.collect.Lists;
import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.eclipse.plugins.ElementClassTransformer;
import com.google.devtools.depan.eclipse.plugins.ElementTransformer;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.utils.ElementEditor;
import com.google.devtools.depan.eclipse.visualization.ogl.GLEntity;
import com.google.devtools.depan.filesystem.eclipse.FileSystemIconTransformer;
import com.google.devtools.depan.filesystem.eclipse.FileSystemImageTransformer;
import com.google.devtools.depan.filesystem.eclipse.FileSystemNodeComparator;
import com.google.devtools.depan.filesystem.eclipse.FileSystemNodePainter;
import com.google.devtools.depan.filesystem.eclipse.FileSystemShapeTransformer;
import com.google.devtools.depan.filesystem.eclipse.NewFileSystemWizard;
import com.google.devtools.depan.filesystem.editors.FileSystemElementEditors;
import com.google.devtools.depan.filesystem.graph.DirectoryElement;
import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.filesystem.graph.FileSystemRelation;
import com.google.devtools.depan.filesystem.integration.FileSystemDefinitions;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.RelationshipSet;
import com.google.devtools.depan.model.RelationshipSetAdapter;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;

/**
 * DepAn Plug-in that models the File System. It is intended to be included from
 * other plug-ins that use a file system; however, it is possible to use this
 * plug-in in DepAn as is.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class FileSystemPlugin implements SourcePlugin {

  /**
   * List of Analysis wizards to include in the DepAn perspective.
   */
  private static final Collection<String> ANALYSIS_WIZARD_IDS;

  /**
   * Collection of classes of element types.
   */
  protected static final Collection<Class<? extends Element>> classes;

  /**
   * Collection of built-in relation types.
   */
  protected static final Collection<Relation> relations;

  /**
   * Collection of built-in relationship sets for a file system.
   */
  protected static final Collection<RelationshipSet> builtinSets =
      Lists.newArrayList();

  /**
   * Default built-in relationship set. It represents any container relations
   * between elements of this File System Plug-in.
   */
  protected static final RelationshipSetAdapter FS_CONTAINER =
      new RelationshipSetAdapter("Filesystem Containers");

  static {
    ANALYSIS_WIZARD_IDS = Lists.newArrayList();
    ANALYSIS_WIZARD_IDS.add(NewFileSystemWizard.ANALYSIS_WIZARD_ID);

    classes = Lists.newArrayList();
    classes.add(DirectoryElement.class);
    classes.add(FileElement.class);

    relations = Lists.newArrayList();
    for (Relation r : FileSystemRelation.values()) {
      relations.add(r);
      FS_CONTAINER.addRelation(r, true, true);
    }

    builtinSets.add(FS_CONTAINER);
  }

  /**
   * Returns the collection of classes of element types in File System Plug-in.
   *
   * @return Collection of classes of element types.
   */
  public static Collection<? extends Class<? extends Element>>
      getClassesStatic() {
    return classes;
  }

  /**
   * Returns the collection of built-in relation types of File System Plug-in.
   *
   * @return Collection of built-in relation types.
   */
  public static Collection<? extends Relation> getRelationsStatic() {
    return relations;
  }

  @Override
  public Collection<Class<? extends Element>> getElementClasses() {
    return classes;
  }

  @Override
  public Collection<? extends Relation> getRelations() {
    return relations;
  }

  @Override
  public Collection<? extends RelationshipSet> getBuiltinRelationshipSets() {
    return builtinSets;
  }

  @Override
  public RelationshipSet getDefaultRelationshipSet() {
    return FS_CONTAINER;
  }

  @Override
  public ElementTransformer<Color> getElementColorProvider() {
    return FileSystemNodePainter.getInstance();
  }

  @Override
  public ElementClassTransformer<Class<? extends ElementEditor>>
      getElementEditorProvider() {
    return FileSystemElementEditors.getInstance();
  }

  @Override
  public ElementTransformer<ImageDescriptor>
      getElementImageDescriptorProvider() {
    return FileSystemIconTransformer.getInstance();
  }

  @Override
  public ElementTransformer<Image> getElementImageProvider() {
    return FileSystemImageTransformer.getInstance();
  }

  @Override
  public ElementTransformer<GLEntity> getElementShapeProvider() {
    return FileSystemShapeTransformer.getInstance();
  }

  @Override
  public Comparator<Element> getElementSorter() {
    return FileSystemNodeComparator.getInstance();
  }

  @Override
  public Config getXMLConfig() {
    return FileSystemDefinitions.getInstance();
  }

  @Override
  public Collection<String> getNewAnalysisIds() {
    return ANALYSIS_WIZARD_IDS;
  }
}
