/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;

/**
 * Allow saving and restoring any type of object in XML.
 * Uses the XStream library.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <T> object type to save / load...
 */
public class XmlPersist<T> {

  private T persistObject;
  private final URI uri;
  private static final XStream XSTREAM = new XStream(new DomDriver());
  
  protected XmlPersist(T object, URI uri) {
    this.persistObject = object;
    this.uri = uri;
  }
  
  /**
   * Save the object to the file specified at construction time.
   *  
   * @return true if the file was saved correctly.
   */
  public boolean save() {
    try {
      XSTREAM.toXML(persistObject, new FileOutputStream(new File(uri)));
      return true;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Load the file given by its URI, and deserialize it with XStream to an
   * object of type T, and return a {@link XmlPersist} object that can be easily
   * used later for saving again after a modification of the object.
   * 
   * @param <T> Type to deserialize from the file.
   * @param uri file
   * @return a new {@link XmlPersist} object, or <code>null</code> if the
   *         loading failed.
   */
  // suppressWarning: unchecked cast to T from Object
  @SuppressWarnings("unchecked")
  public static <T> XmlPersist<T> load(URI uri) {
    T loadedObject;
    try {
      loadedObject = (T) XSTREAM.fromXML(new FileInputStream(new File(uri)));
      return new XmlPersist<T>(loadedObject, uri); 
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Save the given object under the given URI, and return a {@link XmlPersist}
   * object that can be easily used later for saving again after a modification
   * of the object.
   * 
   * @param <T> the type of the object to save
   * @param toSave the object to save
   * @param uri file
   * @return a new {@link XmlPersist} object, or <code>null</code> if an error
   *         occurs during saving.
   */
  public static <T> XmlPersist<T> save(T toSave, URI uri) {
    XmlPersist<T> migrationToSave = new XmlPersist<T>(toSave, uri);
    boolean ok = migrationToSave.save();
    if (ok) {
      return migrationToSave;
    }
    return null;
  }

  public T getObject() {
    return persistObject;
  }

  public URI getUri() {
    return uri;
  }

  /**
   * @param newObject
   */
  public void save(T newObject) {
    this.persistObject = newObject;
    this.save();
  }
}
