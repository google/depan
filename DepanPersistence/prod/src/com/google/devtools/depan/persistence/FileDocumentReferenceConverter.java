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

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.persistence.plugins.ResourceDocumentConfigRegistry;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.FileDocumentReference;
import com.google.devtools.depan.resources.PropertyDocument;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;

/**
 * Custom {@link XStream} converter for {@link FileDocumentReferenceConverter}s.
 * This serializes only the provider details, and reloads the
 * {@code PropertyDocument} as required.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class FileDocumentReferenceConverter implements Converter {

  public static final String FILE_DOC_REF_TAG = "file-doc-ref";

  public static final String DOC_PATH_ATTR = "doc-path";

  public FileDocumentReferenceConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(FILE_DOC_REF_TAG, FileDocumentReference.class);
    xstream.registerConverter(new FileDocumentReferenceConverter());
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return type.isAssignableFrom(FileDocumentReference.class);
  }

  /**
   * Simply output the workspace relative name for the referenced GraphModel.
   */
  @Override
  public void marshal(
      Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    FileDocumentReference<?> docRef = (FileDocumentReference<?>) source;
    String location =
        PlatformTools.fromPath(docRef.getLocation().getFullPath());

    writer.startNode(FILE_DOC_REF_TAG);
    writer.addAttribute(DOC_PATH_ATTR, location);
    writer.endNode();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Obtain the {@link GraphModelReference}, including loading the saved
   * {@link GraphModel} from a project-based or file-system relative location.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    reader.moveDown();
    String docPath = reader.getAttribute(DOC_PATH_ATTR);
    reader.moveUp();

    IContainer project =
        PropertyDocumentReferenceContext.getProjectSource(context);
    IWorkspaceRoot wkspRoot =  project.getWorkspace().getRoot();
    IFile docFile = PlatformTools.buildResourceFile(wkspRoot, docPath);
    PropertyDocument<?> doc = 
        ResourceDocumentConfigRegistry.loadRegistryResourceDocument(docFile);
    return FileDocumentReference.buildFileReference(docFile, doc);
  }
}
