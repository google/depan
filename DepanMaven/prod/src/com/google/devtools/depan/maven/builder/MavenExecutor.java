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

import com.google.devtools.depan.maven.builder.MavenContext;
import com.google.devtools.depan.platform.process.ProcessExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Execute the external Maven command to compute the effective-pom for
 * a named POM file.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class MavenExecutor extends ProcessExecutor {

  private static final String JAVA_HOME = "JAVA_HOME";

  private static final String POM_XML = "pom.xml";

  private final String projectPom;

  private final File projectDir;

  private final String projectLabel;

  // How to run Maven
  private final String javaHome;

  private final String mavenExe;

  private final String effPomCmd;

  // Intermediate data for analysis
  private String effPomText;

  public MavenExecutor(
      String projectPom, File projectDir, String projectLabel,
      String javaHome, String mavenExe, String effPomCmd) {
    this.projectPom = projectPom;
    this.projectDir = projectDir;
    this.projectLabel = projectLabel;
    this.javaHome = javaHome;
    this.mavenExe = mavenExe;
    this.effPomCmd = effPomCmd;
  }

  public static MavenExecutor build(
      File pomFile, String javaHome, String mavenExe, String effPomCmd)
      throws IOException {
    File canonicalFile = pomFile.getCanonicalFile();
    String projectPom = canonicalFile.getName();
    File projectDir = canonicalFile.getParentFile();
    String projectLabel = buildProjectLabel(projectPom, projectDir);
    return new MavenExecutor(
        projectPom, projectDir, projectLabel,
        javaHome, mavenExe, effPomCmd);
  }

  private static String buildProjectLabel(String projectPom, File projectDir) {
    if (POM_XML.equals(projectPom)) {
      return projectDir.getName();
    }
    return new File(projectDir.getName(), projectPom).getPath();
  }

  @Override
  protected void configErrThread(Thread forErr) {
    forErr.setName("mvn [err] " + projectLabel);
  }

  @Override
  protected void configOutThread(Thread forOut) {
    forOut.setName("mvn [out] " + projectLabel);
  }

  /**
   * Compute the effective POM for the supplied POM definition file.
   * After this method returns, various results from the execution can
   * be retrieved.  The available results include the effective pom and
   * the exit code from the process execution.
   */
  public void evalEffectivePom(MavenContext context)
      throws IOException, InterruptedException {
    File effPomFile = File.createTempFile("depan-effpom", ".xml");

    ProcessBuilder builder = new ProcessBuilder(
        mavenExe, "-f", projectPom, effPomCmd,
        "-Doutput=" + effPomFile.getAbsolutePath());
    builder.directory(projectDir);
    Map<String, String> env = builder.environment();
    env.put(JAVA_HOME, javaHome);

    execProcess(builder);

    effPomText = loadFile(effPomFile);
  }

  private String loadFile(File source) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(source.toURI()));
    return new String(encoded, Charset.defaultCharset());
  }

  public String getEffPom() {
    return effPomText;
  }
}
