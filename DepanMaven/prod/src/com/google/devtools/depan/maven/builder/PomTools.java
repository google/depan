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

import com.google.devtools.depan.maven.MavenLogger;
import com.google.devtools.depan.pushxml.PushDownXmlHandler;
import com.google.devtools.depan.pushxml.PushDownXmlHandler.DocumentHandler;

import com.google.common.base.Strings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Collection of logging and POM manipulation tools for Maven analysis.
 */
public class PomTools {

  public static final String DEFAULT_POM = "pom.xml";

  private PomTools() {
    // Prevent instantiation.
  }

  /**
   * Abbreviation for a new line.
   */
  public static String nl() {
    return System.lineSeparator();
  }

  public static InputSource getInputSource(File docFile) throws IOException {
    Reader docReader = new FileReader(docFile);
    InputSource result = new InputSource(docReader);
    result.setPublicId(docFile.getPath());
    return result;
  }

  public static File getPomFile(File docFile) {
    if (docFile.isDirectory()) {
      return new File (docFile, DEFAULT_POM);
    }
    return docFile;
  }

  /**
   * All nodes and dependencies from the supplied {@code docSource} are
   * installed in the resulting graphs via the {@code builder}.
   */
  public static void loadModule(
      DocumentHandler pomLoader, InputSource docSource)
      throws ParserConfigurationException, SAXException, IOException {
    PushDownXmlHandler loader = new PushDownXmlHandler(pomLoader);
    loader.parseDocument(docSource);
  }

  public static InputSource loadEffectivePom(
      File moduleFile, MavenContext context)
      throws IOException, InterruptedException {
    MavenExecutor exec = context.build(moduleFile);
    exec.evalEffectivePom(context);

    if (0 != exec.getExitCode()) {
      MavenLogger.LOG.warn(
          "Err {}  getting effective POM for {}\n\nMaven Console output >\n{}",
          exec.getExitCode(), moduleFile.getPath(), exec.getOut());
    }

    String effPom = exec.getEffPom();
    if (Strings.isNullOrEmpty(effPom)) {
      MavenLogger.LOG.warn(
          "Empty effective POM for {}\n\nMaven Console output >\n",
          moduleFile.getPath(), exec.getOut());
    }

    StringReader reader = new StringReader(effPom);
    return new InputSource(reader);
  }

  public static InputSource getPomSource(
      File pomFile, MavenContext context, PomProcessing processing)
      throws IOException, InterruptedException {
    switch (processing) {
    case EFFECTIVE:
      return PomTools.loadEffectivePom(pomFile, context);
    case NONE:
      FileInputStream stream = new FileInputStream(pomFile);
      return new InputSource(stream);
    }

    MavenLogger.LOG.warn("Unexpected processing for {}", pomFile.getPath());
    return null;
  }}
