/*
 * Copyright 2017 The Depan Project Authors
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

import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.PropertyDocument;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.resources.ResourceContainer;
import com.google.devtools.depan.resources.ResourceDocumentReference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.eclipse.core.runtime.IPath;

/**
 * Custom {@link XStream} converter for {@link PropertyDocumentReference}s.
 * This serializes only the provider details, and reloads the
 * {@code PropertyDocument} as required.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ResourceDocumentReferenceConverter implements Converter {

  public static final String RSRC_DOC_REF_TAG = "rsrc-doc-ref";

  public static final String DOC_PATH_ATTR = "doc-container";
  public static final String DOC_NAME_ATTR = "doc-label";

  public ResourceDocumentReferenceConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(RSRC_DOC_REF_TAG, ResourceDocumentReference.class);
    xstream.registerConverter(new ResourceDocumentReferenceConverter());
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return type.isAssignableFrom(ResourceDocumentReference.class);
  }

  /**
   * Simply output the workspace relative name for the referenced GraphModel.
   */
  @Override
  public void marshal(
      Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    ResourceDocumentReference<?> docRef = (ResourceDocumentReference<?>) source;
    ResourceContainer container = docRef.getResourceContainer();
    String docPath = PlatformTools.fromPath(container.getPath());
    String docName = docRef.getDocument().getName();

    writer.startNode(RSRC_DOC_REF_TAG);
    writer.addAttribute(DOC_PATH_ATTR, docPath);
    writer.addAttribute(DOC_NAME_ATTR, docName);
    writer.endNode();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Obtain the {@link ResourceDocumentReference}, including connecting to
   * the project defined resources.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    reader.moveDown();
    String docPath = reader.getAttribute(DOC_PATH_ATTR);
    String docName = reader.getAttribute(DOC_NAME_ATTR);
    reader.moveUp();

    ResourceContainer root =
        PropertyDocumentReferenceContext.getResourceRoot(context);
    IPath refPath = PlatformTools.buildPath(docPath);
    ResourceContainer container = root.getFromPath(refPath);
    if (null == container ) {
      return null;
    }
    Object document = container.getResource(docName);
    if (null != document) {
      return ResourceDocumentReference.buildResourceReference(
          container, (PropertyDocument<?>) document);
    }
    return null;
  }
}
