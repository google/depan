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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Simplify JSON document parsing by using a push-down stack of element
 * handlers.  Some utility classes within this package provide support
 * for common JSON scenarios.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PushDownJsonHandler {

  private final JsonParser parser;

  /////////////////////////////////////
  // Stack of element handlers

  private List<ElementHandler> items = Lists.newArrayList();

  private void push(ElementHandler processor) {
    items.add(processor);
  }

  private void pop() {
    if (items.isEmpty()) {
      throw new IllegalStateException(
          "Attempt to pop from empty XML parsing stack");
    }

    items.remove(items.size() - 1);
  }

  private ElementHandler top() {
    if (items.isEmpty()) {
      throw new IllegalStateException(
          "Attempt to read top from empty XML parsing stack");
    }

    return items.get(items.size() - 1);
  }

  /////////////////////////////////////
  // Public API

  public PushDownJsonHandler(JsonParser parser) {
    this.parser = parser;
  }

  public void parseDocument(ElementHandler docHandler)
      throws IOException {

    push(docHandler);
    JsonToken tkn = parser.nextToken();
    while (null != tkn) {
      switch(tkn.id()) {
      case JsonTokenId.ID_START_OBJECT:
        ElementHandler newHandler = top().newObject();
        push(newHandler);
        break;
      case JsonTokenId.ID_END_OBJECT:
        ElementHandler endHandler = top();
        endHandler.endObject();
        pop();
        break;
      case JsonTokenId.ID_FIELD_NAME:
        ElementHandler fieldHandler = top();
        fieldHandler.fieldName(parser.getText());
        break;
      case JsonTokenId.ID_START_ARRAY:
        top().newArray();
        break;
      case JsonTokenId.ID_END_ARRAY:
        top().endArray();
        break;
      case JsonTokenId.ID_STRING:
        top().withString(parser.getText());
        break;
      case JsonTokenId.ID_NUMBER_INT:
        top().withBigInteger(parser.getBigIntegerValue());
        break;
      case JsonTokenId.ID_NUMBER_FLOAT:
        top().withBigDecimal(parser.getDecimalValue());
        break;
      case JsonTokenId.ID_TRUE:
        top().withBoolean(true);
        break;
      case JsonTokenId.ID_FALSE:
        top().withBoolean(false);
        break;
      case JsonTokenId.ID_NULL:
        top().withNull();
        break;
      default:
        PushJsonLogger.LOG.warn(fmtToken(tkn, parser.getText()));
        break;
      }

      // Keep going
      tkn = parser.nextToken();
    }
  }

  private String fmtToken(JsonToken tkn, String value) {
    StringBuilder result = new StringBuilder();
    result.append("token ");
    result.append(Integer.toString(tkn.id()));
    if (!Strings.isNullOrEmpty(value)) {
      result.append(" ");
      result.append(value);
    }
    return result.toString();
  }
}
