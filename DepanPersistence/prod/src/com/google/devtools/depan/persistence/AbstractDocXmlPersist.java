/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.persistence;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;

/**
 * Provide easy to use load and save methods for many document types.
 * 
 * Concrete types must provide a {@link #coerceLoad(Object)} method to
 * properly handle Java type matching with generic erasure.
 * 
 * Derived types are encouraged to supply a static build method that
 * properly configures an XStreamConfig for the supplied document type
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractDocXmlPersist<T> {

  protected final ObjectXmlPersist xmlPersist;

  public AbstractDocXmlPersist(ObjectXmlPersist xmlPersist) {
    this.xmlPersist = xmlPersist;
  }

  /////////////////////////////////////
  // Hook methods for derived classes
  protected abstract T coerceLoad(Object load);

  protected abstract String buildLoadErrorMsg(URI uri);

  public abstract String buildSaveErrorMsg(URI uri);

  protected String format(String pattern, Object... arguments) {
    return MessageFormat.format(pattern, arguments);
  }

  private void logException(String msg, Exception errIo) {
    PersistenceLogger.LOG.error(msg, errIo);
  }

  protected String formatErrorMsg(String pattern, URI uri) {
    return MessageFormat.format(pattern, uri);
  }

  public T load(URI uri) {
    try {
      return coerceLoad(xmlPersist.load(uri));
    } catch (IOException errIo) {
      String msg = buildLoadErrorMsg(uri);
      logException(msg, errIo);
      throw new RuntimeException(msg, errIo);
    }
  }

  public void save(URI uri, T doc) {
    try {
      xmlPersist.save(uri, doc);
    } catch (IOException errIo) {
      String msg = buildSaveErrorMsg(uri);
      logException(msg, errIo);
      throw new RuntimeException(msg, errIo);
    }
  }

  /**
   * Cancels the {@code monitor} if there is an exception,
   * but reports no worked steps on the supplied {@code monitor}.
   */
  public void saveDocument(
      IFile file, T docInfo,
      IProgressMonitor monitor) {
    URI location = file.getLocationURI();
    try {
      this.save(location, docInfo);
      file.refreshLocal(IResource.DEPTH_ZERO, monitor);
    } catch (Exception err) {
      if (null != monitor) {
        monitor.setCanceled(true);
      }
      String msg = buildSaveErrorMsg(location);
      logException(msg, err);
    }
  }
}
