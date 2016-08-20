/*
 * Copyright 2016 The Depan Project Authors
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

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Base set of utilities for marshalling objects via XStream.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractMappingConverter
    extends AbstractTypeConverter {

  private final Mapper mapper;

  public AbstractMappingConverter(Mapper mapper) {
    this.mapper = mapper;
  }

  /////////////////////////////////////
  // Mapper-based unmarshalling services

  protected void marshalObject(Object item,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    String nodeLabel = mapper.serializedClass(item.getClass());
    writer.startNode(nodeLabel);
    context.convertAnother(item);
    writer.endNode();
  }

  protected Object unmarshalObject(
      HierarchicalStreamReader reader, UnmarshallingContext context) {
    reader.moveDown();
    try {
      String childName = reader.getNodeName();
      Class<?> childClass = mapper.realClass(childName);

      return context.convertAnother(null, childClass);
    } finally {
      reader.moveUp();
    }
  }
}
