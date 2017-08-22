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

package com.google.devtools.depan.cmd.dispatch;

import com.google.devtools.depan.cmd.CmdLogger;
import com.google.devtools.depan.graph_doc.model.GraphDocument;
import com.google.devtools.depan.graph_doc.persistence.GraphModelXmlPersist;

import org.eclipse.equinox.app.IApplication;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public abstract class AbstractCommandExec implements CommandExec {

  private List<String> args;

  private Object result =  IApplication.EXIT_OK;

  @Override
  public void setArgs(List<String> args) {
    this.args = args;
  }

  @Override
  public Object getResult() {
    return result;
  }

  protected List<String> getArgs() {
    return args;
  }

  protected String getArg(int index) {
    return args.get(index);
  }

  protected List<String> getParms() {
    return args.subList(1, args.size());
  }

  /**
   * Obtain remaining arguments.
   */
  protected List<String> getParmsAfter(int index) {
    return args.subList(2 + index, args.size());
  }

  /**
   * Provide command parameters.  The origin for parameters is shifted one
   * so the zero-th parameter immediately follows the command argument 
   * (i.e. {@code getArg(1) == getParm(0)}.
   * 
   * The result might be {@code null}, but it should never throw an
   * {@link IndexOutOfBoundsException}.
   */
  protected String getParm(int index) {
    if (index < 0) {
      return null;
    }
    if ((1 + index) >= args.size()) {
      return null;
    }
    return args.get(1 + index);
  }

  protected String getParm(int index, String def) {
    String result = getParm(index);
    if (null != result) {
      return result;
    }
    return def;
  }

  protected void failWithMessage(String msg) {
    result = new Integer(1);
    CmdLogger.LOG.warn(msg);
  }

  protected URI buildLocation(String outName) {
    File result = new File(outName);
    return result.toURI();
  }

  /**
   * Provide the {@link GraphDocument} associated
   * with the supplied {@link URI}.
   * 
   * If the URI fails to load as a {@link GraphDocument}, writes a message
   * to the log and returns {@code null}.
   */
  protected GraphDocument buildGraphDoc(URI graphUri) {
    try {
      CmdLogger.LOG.info("Loading GraphDoc from {}", graphUri);
      GraphModelXmlPersist loader = GraphModelXmlPersist.build(true);
      return loader.load(graphUri);
    } catch (RuntimeException err) {
      CmdLogger.LOG.error("Unable to load GraphDoc from {}", graphUri, err);
    }
    return null;
  }

  /**
   * Provide the {@link GraphDocument} associated
   * with the supplied {@link #location}.
   * 
   * If the location fails to load as a {@link GraphDocument},
   * writes a message to the log and returns {@code null}.
   */
  protected GraphDocument buildGraphDoc(String location) {
    URI graphUri = buildLocation(location);
    return buildGraphDoc(graphUri);
  }

  /**
   * Provide the {@link GraphDocument} associated
   * with the supplied parameter {@link #index}.
   * 
   * If the index fails to load as a {@link GraphDocument},
   * writes a message to the log and returns {@code null}.
   */
  protected GraphDocument buildGraphDoc(int index) {
    return buildGraphDoc(getParm(index));
  }
}
