/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.utils.relsets;

import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;
import com.google.devtools.depan.model.RelationshipSet;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
 * Easy to use {@code load()} and {@code save()} methods for 
 * {@link RelationshipSet} collections.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class RelSetXmlPersist {

  public static final String RELATION_SET_EXT = "dpans";

  protected final ObjectXmlPersist xmlPersist;

  public RelSetXmlPersist() {
    this.xmlPersist = 
        new ObjectXmlPersist(XStreamFactory.getSharedGraphXStream());
  }

  @SuppressWarnings("unchecked")
  public Collection<RelationshipSet> load(URI uri) {
    try {
      return (Collection<RelationshipSet>) xmlPersist.load(uri);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to load GraphModel from " + uri, errIo);
    }
  }

  public void save(URI uri, Collection<RelationshipSet> relsets) {
    try {
      xmlPersist.save(uri, relsets);
    } catch (IOException errIo) {
      throw new RuntimeException(
          "Unable to save GraphModel to " + uri, errIo);
    }
  }
}
