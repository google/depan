/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.maven.builder;

import com.google.devtools.depan.pushxml.PushDownXmlHandler.ElementHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.TextElementHandler;
import com.google.devtools.depan.maven.graph.ArtifactElement;
import com.google.devtools.depan.model.GraphNode;

/**
 * Handle the mechanics of collecting the Maven "coordinates" for an artifact.
 * The coordinates are the label for a the common Maven {@link ArtifactElement}.
 * 
 * Maven coordinates occur in many contexts, and often with other elements.
 * This is especially true in project definitions, where the various parts
 * of the coordinates might be scattered across the file, even intermixed with
 * build or dependencies elements.  In practice, this rarely happens but it
 * still seems to be a permitted structure.
 * 
 * In agreement with the Maven behaviors, groupId, artifactId, and versionId
 * are always required.  Missing values cause exceptions.  The classifier
 * and package elements are optional, and provide null if they are not
 * available.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class LabelCapture {

  public static final String GROUP_ID = "groupId";
  public static final String ARTIFACT_ID = "artifactId";
  public static final String VERSION = "version";
  public static final String PACKAGING = "packaging";
  public static final String CLASSIFIER = "classifier";

  private TextElementHandler groupId;
  private TextElementHandler artifactId;
  private TextElementHandler version;
  private TextElementHandler packaging;
  private TextElementHandler classifier;

  /**
   * Provide an {@link ElementHandler} for the supplied name if it is
   * recognized as a Maven coordinate element.  Otherwise, {@code null} is
   * returned, and the caller should provide a seperate {@link ElementHandler}.
   * 
   * @param name element name to convert into a {@link ElementHandler}
   * @return {@link ElementHandler} for provided name,
   *   or {@code null} if the name is not recognized.
   */
  public ElementHandler captureElement(String name) {
    if (GROUP_ID.equals(name)) {
      groupId = new TextElementHandler(name);
      return groupId;
    }
    if (ARTIFACT_ID.equals(name)) {
      artifactId = new TextElementHandler(name);
      return artifactId;
    }
    if (VERSION.equals(name)) {
      version = new TextElementHandler(name);
      return version;
    }
    if (PACKAGING.equals(name)) {
      packaging = new TextElementHandler(name);
      return packaging;
    }
    if (CLASSIFIER.equals(name)) {
      classifier = new TextElementHandler(name);
      return classifier;
    }

    // Not an artifact label element
    return null;
  }

  /**
   * Convert the label from the Maven coordinates
   * into a DepAn {@link GraphNode}.
   * 
   * Since the Maven coordinate elements can be scattered throughout their
   * containing element, this method should be called only at end of
   * the enclosing element.
   */
  public GraphNode buildDefinitionNode(MavenContext context) {
    ArtifactElement definition = ArtifactElement.buildDefinition(
        getGroupId(), getArtifactId(), getVersion(),
        getPackaging(), getClassifier());
    return cannonicalize(context, definition);
  }


  /**
   * Convert the label from the Maven coordinates
   * into a DepAn {@link GraphNode}.
   * 
   * Since the Maven coordinate elements can be scattered throughout their
   * containing element, this method should be called only at end of
   * the enclosing element.
   */
  public GraphNode buildReferenceNode(MavenContext context) {
    ArtifactElement reference = ArtifactElement.buildReference(
        getGroupId(), getArtifactId(), getVersion(),
        getPackaging(), getClassifier());
    return cannonicalize(context, reference);
  }

  private GraphNode cannonicalize(
      MavenContext context, ArtifactElement artifact) {
    GraphNode lookup = context.lookup(artifact);
    if (null != lookup) {
      return lookup;
    }
    return artifact;
  }

  private String getGroupId() {
    return groupId.getText();
  }

  private String getArtifactId() {
    return artifactId.getText();
  }

  private String getVersion() {
    return version.getText();
  }

  private String getPackaging() {
    if (null != packaging) {
      return packaging.getText();
    }
    return null;
  }

  private String getClassifier() {
    if (null != classifier) {
      return classifier.getText();
    }
    return null;
  }
}
