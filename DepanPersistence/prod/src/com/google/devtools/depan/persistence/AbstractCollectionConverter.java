/*
 * Copyright 2015 The Depan Project Authors
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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Handle the common case of an object with a bunch of semi-homogenous
 * element.
 *
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public abstract class AbstractCollectionConverter<T> extends AbstractTypeConverter {
  private final Class<?> collectType;
  private final Class<?> elementType;
  private final Mapper mapper;

  public AbstractCollectionConverter(
      Class<?> collectType, Class<?> elementType, Mapper mapper) {
    this.collectType = collectType;
    this.elementType = elementType;
    this.mapper = mapper;
  }

  @Override
  public Class<?> getType() {
    return collectType;
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return getType().isAssignableFrom(type);
  }

  /**
   * Utility method to write a child object to the output stream.
   */
  protected void marshalObject(Object item,
      HierarchicalStreamWriter writer, MarshallingContext context) {
    String nodeLabel = mapper.serializedClass(item.getClass());
    writer.startNode(nodeLabel);
    context.convertAnother(item);
    writer.endNode();
  }

  /**
   * Utility method to write a collection of children objects
   * to the output stream.
   */
  protected void marshalCollection(Collection<T> items,
      HierarchicalStreamWriter writer, MarshallingContext context) {

    for (T item : items) {
      marshalObject(item, writer, context);
    }
  }


  /**
   * Utility method to read a single child object from the input stream.
   */
  @SuppressWarnings("unchecked")
  protected T unmarshalChild(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    String childName = reader.getNodeName();
    Class<?> childClass = mapper.realClass(childName);
    if (elementType.isAssignableFrom(childClass)) {
      return (T) context.convertAnother(null, childClass);
    }
    return null;
  }

  private void unmarshalCollection(
      Collection<T> collect,
      HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      while (reader.hasMoreChildren()) {
        reader.moveDown();
        T item = unmarshalChild(reader, context);
        if (null != item) {
          collect.add(item);
        }
        reader.moveUp();
      }
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }

  /**
   * Utility method to read a list of children objects
   * from the input stream.
   */
  public List<T> unmarshalList(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      List<T> result = Lists.newArrayList();
      unmarshalCollection(result, reader, context);
      return result;
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }

  /**
   * Utility method to read a set of children objects
   * from the input stream.
   */
  public Set<T> unmarshalSet(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      Set<T> result = Sets.newHashSet();
      unmarshalCollection(result, reader, context);
      return result;
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }
}
