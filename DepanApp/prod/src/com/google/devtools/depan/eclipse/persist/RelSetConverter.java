/*
 * Copyright 2014 Pnambic Computing
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
import com.google.devtools.depan.model.RelationshipSet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@code XStream} converter to handle {@code RelationshipSet}s.
 * In order to facilitate selection, etc., make sure that all built-in
 * relation sets use the same instance.
 * 
 * TODO: Provide a real relation set registry that include user defined
 * relation sets to avoid aliases and duplicate definitions.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class RelSetConverter implements Converter {

  private static final String RELATION_SET_TAG = "relation-set";

  // Attribute names
  private static final String REL_SET = "rel-set";
  private static final String PLUGIN_ID = "plugin-id";

  public RelSetConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(RELATION_SET_TAG, RelSetConverter.class);
    xstream.registerConverter(new RelSetConverter());
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return RelationshipSet.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    RelationshipSet relSet = (RelationshipSet) source;
    SourcePlugin plugin = findBuiltIn(relSet);
    if (null != plugin) {
      String entryId = SourcePluginRegistry.getInstance().getPluginId(plugin);
      writer.addAttribute(PLUGIN_ID, entryId);
      writer.addAttribute(REL_SET, relSet.getName());
    }
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      String pluginId = reader.getAttribute(PLUGIN_ID);
      String relSetName = reader.getAttribute(REL_SET);
      if ((null != pluginId) && (null != relSetName)) {
        SourcePlugin plugin = SourcePluginRegistry.getSourcePlugin(pluginId);
        if (null == plugin) {
          return null;
        }
        for (RelationshipSet builtIn : plugin.getBuiltinRelationshipSets()) {
          if (relSetName.equals(builtIn.getName())) {
            return builtIn;
          }
        }
      }
      return null;
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }

  private SourcePlugin findBuiltIn(RelationshipSet relSet) {
    // Populate the list viewer with all known relations.
    for (SourcePlugin plugin : SourcePluginRegistry.getInstances()) {
      for (RelationshipSet builtIn : plugin.getBuiltinRelationshipSets()) {
        if (builtIn == relSet) {
          return plugin;
        }
      }
    }
    return null;
  }
}
