/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.maven.graph;

import com.google.common.base.Strings;

/**
 * {@link MavenElement} that represents an Maven POM artifact.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class ArtifactElement extends MavenElement {
  public static final String JAR_PACKAGING = "jar";
  public static final String COLON = ":";

  public static final String DEFAULT_PACKAGING = JAR_PACKAGING;

  /**
   * Maven artifact coordinates.
   */
  private final String groupId;
  private final String artifactId;
  private final String version;
  private final String packaging;
  private final String classifier;

  /**
   * Create an ArtifactElement value object.
   * Only classifier may be {@code null}.  All other supplied arguments
   * must be non-{@code null}.
   * 
   * Most consumers should use the factory method
   * {@link #buildDefinition(String, String, String, String, String)}.
   */
  public ArtifactElement(
      String groupId, String artifactId, String version,
      String packaging, String classifier) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.packaging = packaging;
    this.classifier = classifier;
  }

  /**
   * Create a Maven analysis element based on it's coordinates.
   * The correct default values are supplied if the supplied packaging
   * or classifier is blank. The supplied groupId, artifactId, and version
   * may never be blank.
   */
  public static ArtifactElement buildDefinition(
      String groupId, String artifactId, String version,
      String packaging, String classifier) {
    if (Strings.isNullOrEmpty(classifier)) {
      classifier = null;
    }
    if (Strings.isNullOrEmpty(packaging)) {
      packaging = DEFAULT_PACKAGING;
    }
    return new ArtifactElement(
        groupId, artifactId, version, packaging, classifier);
  }

  /**
   * Create a Maven analysis element based on it's coordinates.
   * Artifact references may omit the packaging or classifier.
   */
  public static ArtifactElement buildReference(
      String groupId, String artifactId, String version,
      String packaging, String classifier) {
    return new ArtifactElement(
        groupId, artifactId, version, packaging, classifier);
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getPackaging() {
    return packaging;
  }

  public String getClassifier() {
    return classifier;
  }

  @Override
  public String getCoordinate() {
    StringBuilder result = new StringBuilder();
    result.append(groupId);

    result.append(COLON);
    result.append(artifactId);

    if (null != classifier) {
      result.append(COLON);
      if (!Strings.isNullOrEmpty(packaging)) {
        result.append(packaging);
      }
      result.append(COLON);
      result.append(classifier);
    } else if (!JAR_PACKAGING.equals(packaging)) {
      result.append(COLON);
      result.append(packaging);
    }

    result.append(COLON);
    result.append(version);
    return result.toString();
  }

  @Override
  public String friendlyString() {
    return getCoordinate();
  }

  @Override
  public void accept(MavenElementVisitor visitor) {
    visitor.visitArtifactElement(this);
  }

  /**
   * Uses field members to create a hashCode.
   */
  @Override
  public int hashCode() {
    int result = 37;
    result = 31 * result + groupId.hashCode();
    result = 31 * result + artifactId.hashCode();
    result = 31 * result + version.hashCode();
    if (null != packaging) {
      result = 31 * result + packaging.hashCode();
    }
    if (null != classifier) {
      result = 31 * result + classifier.hashCode();
    }
    return result;
  }

  /**
   * Two {@link ArtifactElement}s are equals iff all of their member
   * fields match.
   */
  @Override
  public boolean equals(Object obj) {
    if (! (obj instanceof ArtifactElement)) {
      return super.equals(obj);
    }
    ArtifactElement that = (ArtifactElement) obj;
    if (!groupId.equals(that.groupId)) {
      return false;
    }
    if (!artifactId.equals(that.artifactId)) {
      return false;
    }
    if (!version.equals(that.version)) {
      return false;
    }
    if (!matchPackaging(that)) {
      return false;
    }
    return matchClassifier(that);
  }

  private boolean matchPackaging(ArtifactElement match) {
    if (null == packaging) {
      return null == match.packaging;
    }
    return packaging.equals(match.packaging);
  }

  private boolean matchClassifier(ArtifactElement match) {
    if (null == classifier) {
      return null == match.classifier;
    }
    return classifier.equals(match.classifier);
  }

  /**
   * Returns the {@code String} representation of this object.
   *
   * @return {@code String} representation of this object.
   */
  @Override
  public String toString() {
    return "Artifact " + getCoordinate();
  }

  public String getBaseLabel() {
    StringBuilder result = new StringBuilder();
    result.append(groupId);

    result.append(COLON);
    result.append(artifactId);

    result.append(COLON);
    result.append(version);
    return result.toString();
  }
}
