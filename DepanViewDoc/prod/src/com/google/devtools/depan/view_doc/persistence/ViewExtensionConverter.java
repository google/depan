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

package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.view_doc.eclipse.ViewDocLogger;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtensionRegistry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@link XStream} converter to handle {@link ViewExtension}s.
 * The {@link ViewExtension} instances are serialized by their
 * {@code contribId} from the {@link ViewExtensionRegistry}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ViewExtensionConverter implements Converter {

  public static final String VIEW_EXT_ATTR = "view-ext";

  public ViewExtensionConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.registerConverter(new ViewExtensionConverter());
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return ViewExtension.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(
      Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    ViewExtension ext = (ViewExtension) source;
    String extId = ViewExtensionRegistry.getRegistryId(ext);

    writer.addAttribute(VIEW_EXT_ATTR, extId);
  }

  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    String extId = getExtensionId(reader);

    try {
      ViewExtension result =
          ViewExtensionRegistry.getRegistryExtension(extId);
      return result;
    } catch (RuntimeException err) {
      ViewDocLogger.LOG.error(
          "Unable to locate extension {}.", extId, err);
      throw err;
    }
  }

  private String getExtensionId(HierarchicalStreamReader reader) {
    try {
      return reader.getAttribute(VIEW_EXT_ATTR);
    } catch (RuntimeException err) {
      ViewDocLogger.LOG.error("Unable to locate extension id", err);
      throw err;
    }
  }
}
