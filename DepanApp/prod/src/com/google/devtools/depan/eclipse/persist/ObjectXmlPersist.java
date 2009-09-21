/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.eclipse.persist;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;

/**
 * Handle persistence of object to and from XML files.
 * This should correctly serialize any DepAn object that contains plugin
 * defined nodes and edges.
 * <p>
 * As tempting as it appears to re-write this with generics, simple approaches
 * fail to provide advantages due to erasure.  Adding a generic class to the
 * type fails to provide the necessary information to correctly cast the
 * results from {@code #load(URI)}.  The compiler will accept the cast, but
 * it doesn't do the right thing.  It could work if the constructor actually
 * accepted a type token (e.g. {@code Blix.class}), but that's a more heavy
 * handed implementation.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ObjectXmlPersist {
  /**
   * The entity that converts objects to XML.
   */
  protected final XStream xstream;

  /**
   * Create a serializer using the provided XStream.
   * The {@code XStreamFactory} class can synthesize an appropriate
   * {@code XStream} for most purposes.
   * 
   * @param xstream {@code XStream }to use for serialization
   */
  public ObjectXmlPersist(XStream xstream) {
    this.xstream = xstream;
  }

  /**
   * Create a instance that uses the shared StAX XStream driver.  The StAX
   * driver is preferred, especially for large objects 
   * (e.g. {@code GraphModel}s), since it uses much less ignorable white-space.
   * It also has the advantage that an XML declaration is automatically written
   * at the top of the file, so the resulting output is actually valid XML.
   * Finally, reusing the shared XStream instance reduces the configuration
   * costs of multiple XStreams.
   */
  public ObjectXmlPersist() {
    this(XStreamFactory.getSharedXStream());
  }

  /**
   * Load an object from the provided URI.
   * 
   * @param uri location of persistent object
   * @return object fro location
   * @throws IOException
   */
  public Object load(URI uri) throws IOException {
    InputStreamReader src = null;

    try {
      src = new FileReader(new File(uri));
      return xstream.fromXML(src);
    } finally {
      if (null != src) {
        src.close();
      }
    }
  }

  /**
   * Save an object to the provided URI.
   * 
   * @param uri location for persistent representation
   * @param item object to persist
   * @throws IOException
   */
  public void save(URI uri, Object item) throws IOException {
    OutputStreamWriter dst = null;

    try {
      dst = new FileWriter(new File(uri));
      xstream.toXML(item, dst);
    } finally {
      if (null != dst) {
        dst.close();
      }
    }
  }
}
