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

import com.google.devtools.depan.eclipse.wizards.PushDownXmlHandler;
import com.google.devtools.depan.eclipse.wizards.PushDownXmlHandler.DocumentHandler;

import com.google.common.base.Strings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Collection of logging and POM manipulation tools for Maven analysis.
 */
public class Tools {

  public static final String DEFAULT_POM = "pom.xml";

  private Tools() {
    // Prevent instantiation.
  }

  // Common logger for this package
  public static final Logger LOG =
      Logger.getLogger(Tools.class.getPackage().getName());

  public static void warnThrown(String msg, Throwable err) {
    LOG.log(Level.WARNING, msg, err);
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
    MavenExecutor exec = MavenExecutor.build(moduleFile);
    exec.evalEffectivePom(context);

    if (0 != exec.getExitCode()) {
      LOG.warning("Err " + exec.getExitCode()
          + " getting effective POM for " + moduleFile.getPath()
          + nl() + nl() + "Maven Console output >" +  nl() + exec.getOut());
    }

    String effPom = exec.getEffPom();
    if (Strings.isNullOrEmpty(effPom)) {
      LOG.warning("Empty effective POM for " + moduleFile.getPath()
      + nl() + nl() + "Maven Console output >" +  nl() + exec.getOut());
    }

    StringReader reader = new StringReader(effPom);
    return new InputSource(reader);
  }
}
