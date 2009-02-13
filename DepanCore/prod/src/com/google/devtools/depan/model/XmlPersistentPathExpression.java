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

package com.google.devtools.depan.model;

import com.google.common.collect.Lists;
import com.google.devtools.depan.filters.PathMatcherTerm;

import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.List;

/**
 * Used to write an array of {@link PathMatcherTerm}s to persistent storage.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class XmlPersistentPathExpression
    extends XmlPersistentObject<PathMatcherTerm[]> {
  /**
   * Loads an array of {@link PathMatcherTerm}s that is stored at the given
   * location.
   *
   * @param uri Location of the object.
   * @return An array of {@link PathMatcherTerm}s that is read from the given
   * location.
   */
  @Override
  public PathMatcherTerm[] load(URI uri) {
    try {
      InputStreamReader src = new FileReader(new File(uri));
      ObjectInputStream objs = xstream.createObjectInputStream(src);
      return loadPathMatcherTerms(objs);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (Throwable errAny) {
      errAny.printStackTrace();
      return null;
    }
  }

  /**
   * Loads an array of {@link PathMatcherTerm}s from the given input source.
   *
   * @param objs Input Stream where objects are read.
   * @return An array of {@link PathMatcherTerm}s that holds all items read from
   * given location.
   * @throws IOException if any I/O exception occurs.
   * @throws ClassNotFoundException if the class of object that is read cannot
   * be found.
   */
  private static PathMatcherTerm[] loadPathMatcherTerms(ObjectInputStream objs)
      throws IOException, ClassNotFoundException {

    List<PathMatcherTerm> resultList = Lists.newArrayList();
    while (true) {
      Object element;
      try {
        element = objs.readObject();
      } catch (EOFException eof) {
        break;
      }
      if (element instanceof PathMatcherTerm) {
        resultList.add((PathMatcherTerm) element);
      }
      // Ignore other objects in stream
    }
    return resultList.toArray(new PathMatcherTerm[resultList.size()]);
  }

  /**
   * Writes the given {@link PathMatcherTerm}s to given output stream.
   *
   * @param uri The location that shows where the terms must be written.
   * @param terms An array of {@link PathMatcherTerm}s to be written to
   * persistent storage.
   * @throws IOException if exception occurs during writing.
   */
  @Override
  public void save(URI uri, PathMatcherTerm[] terms) throws IOException {
    OutputStreamWriter source = null;
    ObjectOutputStream objs = null;

    try {
      source = new FileWriter(new File(uri));
      objs = xstream.createObjectOutputStream(source, "path-expression");

      savePathMatcherTerms(objs, terms);

    } catch (IOException e) {
      e.printStackTrace();

    } finally {
      if (null != objs) {
        objs.close();
      }
      if (null != source) {
        source.close();
      }
    }
  }

  /**
   * Writes the given {@link PathMatcherTerm}s to given output stream.
   *
   * @param objs Where the terms must be written.
   * @param terms An array of {@link PathMatcherTerm}s to be written to
   * persistent storage.
   * @throws IOException if exception occurs during writing.
   */
  private static void savePathMatcherTerms(
      ObjectOutputStream objs, PathMatcherTerm[] terms) throws IOException {
    for (int i = 0; i < terms.length; i++) {
      objs.writeObject(terms[i]);
    }
  }
}
