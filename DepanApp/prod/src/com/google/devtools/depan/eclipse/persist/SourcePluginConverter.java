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

package com.google.devtools.depan.eclipse.persist;

import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.graph_doc.persistence.EdgeConverter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import org.eclipse.core.runtime.CoreException;

/**
 * Custom {@code XStream} converter for {@link SourcePlugin}s.
 * This serializes only the id for the plugin, and reconstitutes it
 * by querying the {@link SourcePluginRegistry}.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class SourcePluginConverter implements Converter {

  public static final String ANALYZER_ID_TAG = "src-analyzer";

  private final SourcePluginRegistry registry;

  public SourcePluginConverter(Mapper mapper, SourcePluginRegistry registry) {
    this.registry = registry;
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(ANALYZER_ID_TAG, SourcePlugin.class);
    xstream.registerConverter(new SourcePluginConverter(
        xstream.getMapper(), SourcePluginRegistry.getInstance()));
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return SourcePlugin.class.isAssignableFrom(type);
  }

  /**
   * Simply output the id for the plugin.
   */
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    SourcePlugin analyzer = (SourcePlugin) source;
    context.convertAnother(registry.getPluginId(analyzer));
  }

  /**
   * {@inheritDoc}
   * 
   * <p>Reads in plugin's id, and obtain its mapping from the
   * known Source Plugin {@link #registry}.
   * 
   * @see EdgeConverter#unmarshal(HierarchicalStreamReader, UnmarshallingContext)
   */
  @Override
  public Object unmarshal(
      HierarchicalStreamReader reader, UnmarshallingContext context) {

    String pluginId = (String) context.convertAnother(null, String.class);
    try {
      return registry.getSourcePluginEntry(pluginId).getInstance();
    } catch (CoreException errCore) {
      throw new IllegalArgumentException(
          "Unrecognized plugin id " + pluginId, errCore);
    }
  }
}
