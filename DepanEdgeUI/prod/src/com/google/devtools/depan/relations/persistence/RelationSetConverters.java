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
package com.google.devtools.depan.relations.persistence;

import com.google.devtools.depan.persistence.AbstractTypeConverter;
import com.google.devtools.depan.persistence.AbstractCollectionConverter;
import com.google.devtools.depan.graph.api.Relation;
import com.google.devtools.depan.graph.api.RelationSet;
import com.google.devtools.depan.model.RelationSets;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * {@code XStream} converters to handle {@link RelationSet}s.
 * In order to facilitate selection, etc., make sure that all built-in
 * relation sets use the same instance.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RelationSetConverters {

  private static final String RELATION_SET_ALL_TAG = "relation-set-all";
  private static final String RELATION_SET_EMPTY_TAG = "relation-set-empty";
  private static final String RELATION_SET_ARRAY_TAG = "relation-set-array";
  private static final String RELATION_SET_SIMPLE_TAG = "relation-set-simple";
  private static final String RELATION_SET_SINGLE_TAG = "relation-set-single";

  private RelationSetConverters() {
    // Prevent instantiations.
  }

  protected static void configXStream(
      XStream xstream, String typeTag, AbstractTypeConverter converter) {
    xstream.aliasType(typeTag, converter.getType());
    xstream.registerConverter(converter);
  }

  public static void configXStream(XStream xstream) {
    Mapper mapper = xstream.getMapper();
    new ForValue(RelationSets.ALL).registerWithTag(
        xstream, RELATION_SET_ALL_TAG);
    new ForValue(RelationSets.EMPTY).registerWithTag(
        xstream, RELATION_SET_EMPTY_TAG);

    new ForArray(mapper).registerWithTag(
        xstream, RELATION_SET_ARRAY_TAG);
    new ForSimple(mapper).registerWithTag(
        xstream, RELATION_SET_SIMPLE_TAG);
    new ForSingle(mapper).registerWithTag(
        xstream, RELATION_SET_SINGLE_TAG);
  }

  private static class ForValue extends AbstractTypeConverter {
    private final RelationSet value;

    public ForValue(RelationSet value) {
      this.value = value;
    }

    @Override
    public Class<?> getType() {
      return value.getClass();
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
      return value;
    }
  }

  private static class ForSingle extends AbstractCollectionConverter<Relation> {

    public ForSingle(Mapper mapper) {
      super(ForSingle.class, Relation.class, mapper);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
      RelationSets.Single relationSet = (RelationSets.Single) source;
      marshalObject(relationSet.getRelation(), writer, context);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
      try {
        Relation relation = unmarshalChild(reader, context);
        return RelationSets.createSingle(relation);
      } catch (RuntimeException err) {
        err.printStackTrace();
        throw err;
      }
    }
  }

  private static class ForArray extends AbstractCollectionConverter<Relation> {

    public ForArray(Mapper mapper) {
      super(RelationSets.Array.class, Relation.class, mapper);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
      RelationSets.Array relationSet = (RelationSets.Array) source;
      marshalCollection(
          Arrays.asList(relationSet.getRelations()), writer, context);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
      List<Relation> result = unmarshalList(reader, context);
      return new RelationSets.Array((Relation[]) result.toArray());
    }
  }

  private static class ForSimple extends AbstractCollectionConverter<Relation> {

    public ForSimple(Mapper mapper) {
      super(RelationSets.Simple.class, Relation.class, mapper);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
        MarshallingContext context) {
      RelationSets.Simple relationSet = (RelationSets.Simple) source;
      marshalCollection(relationSet.getRelations(), writer, context);
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
        UnmarshallingContext context) {
      Set<Relation> result = unmarshalSet(reader, context);
      return new RelationSets.Simple(result);
    }
  }
}
