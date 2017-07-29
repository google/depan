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

package com.google.devtools.depan.pushjson;

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.google.common.collect.Lists;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class PushDownJsonHandlerTest {

  @Test
  public void testEmptyDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser("");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    assertNull(docHandler.child);
  }

  @Test
  public void testMinimalDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser("{}");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
  }

  @Test
  public void testStringFieldDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser(
        "{ \"blix\" : \"blax\" }");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
    docHandler.child.assertField("blix");
    assertEquals("blax", docHandler.child.stringValue);
  }

  @Test
  public void testIntegerFieldDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser(
        "{ \"blix\" : 1234 }");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
    docHandler.child.assertField("blix");
    assertEquals(new BigInteger("1234"), docHandler.child.bigIntegerValue);
  }

  @Test
  public void testDecimalFieldDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser(
        "{ \"blix\" : 123.45 }");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
    docHandler.child.assertField("blix");
    assertEquals(new BigDecimal("123.45"), docHandler.child.bigDecimalValue);
  }

  @Test
  public void testTrueFieldDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser(
        "{ \"blix\" : true }");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
    docHandler.child.assertField("blix");
    assertEquals(true, docHandler.child.boolValue);
  }

  @Test
  public void testFalseFieldDoc() throws JsonParseException, IOException {
    JsonParser parser = new JsonFactory().createParser(
        "{ \"blix\" : false }");
    PushDownJsonHandler handler = new PushDownJsonHandler(parser);
    TestDocumentHandler docHandler = new TestDocumentHandler();

    handler.parseDocument(docHandler);

    docHandler.child.assertNesting();
    docHandler.child.assertField("blix");
    assertEquals(false, docHandler.child.boolValue);
  }

  private static class TestDocumentHandler extends DocumentHandler {

    public TestNestingHandler child;

    @Override
    public ElementHandler newObject() {
      child = new TestNestingHandler();
      return child;
    }
  }

  private static class TestNestingHandler implements ElementHandler {

    public int newObjectCount = 0;
    public int endObjectCount = 0;
    public int fieldNameCount = 0;
    public int newArrayCount = 0;
    public int endArrayCount = 0;
    public int withStringCount = 0;
    public int withBigIntegerCount = 0;
    public int withBigDecimalCount = 0;
    public int withBooleanCount = 0;
    public int withNullCount = 0;

    public List<TestNestingHandler> children = Lists.newArrayList();

    private String fieldName;
    private String stringValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private boolean boolValue;

    public void assertNesting() {
      assertTrue( "Improper object nesting: "
          + "endObject() count " + endObjectCount
          + " should be exactly one",
          1 == endObjectCount);
      assertTrue( "Improper array nesting: "
          + "newArray() count " + newArrayCount
          + " does not match endArray() count" + endArrayCount,
          newArrayCount == endArrayCount);
    }

    public void assertField(String expected) {
      assertEquals("Unexpected field name: expected " + expected
          + ", actual " + fieldName,
          expected, fieldName);
    }

    @Override
    public ElementHandler newObject() {
      newObjectCount++;
      TestNestingHandler child = new TestNestingHandler();
      children.add(child);
      return child;
    }

    @Override
    public void endObject() {
      endObjectCount++;
    }

    @Override
    public void fieldName(String text) {
      fieldNameCount++;
      this.fieldName = text;
    }

    @Override
    public void newArray() {
      newArrayCount++;
    }

    @Override
    public void endArray() {
      endArrayCount++;
    }

    @Override
    public void withString(String stringValue) {
      withStringCount++;
      this.stringValue = stringValue;
    }

    @Override
    public void withBigInteger(BigInteger bigIntegerValue) {
      withBigIntegerCount++;
      this.bigIntegerValue = bigIntegerValue;
    }

    @Override
    public void withBigDecimal(BigDecimal decimalValue) {
      withBigDecimalCount++;
      this.bigDecimalValue = decimalValue;
    }

    @Override
    public void withBoolean(boolean boolValue) {
      withBooleanCount++;
      this.boolValue = boolValue;
    }

    @Override
    public void withNull() {
      withNullCount++;
    }
  }
}
