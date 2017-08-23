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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A JSON element handler that logs un-processed events.  This class is useful
 * when implementing a new JSON parser, since it reports unexpected
 * events.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class LoggingElementHandler implements ElementHandler {

  private final String selfPath;

  private String loggedField;

  public LoggingElementHandler(String selfPath) {
    this.selfPath = selfPath;
  }

  public LoggingElementHandler() {
    this("ROOT");
  }

  protected void reportEvent(String event) {
    PushJsonLogger.LOG.info("{} @{}", getFieldName(), event);
  }

  protected void reportValue(String method, String value) {
    PushJsonLogger.LOG.info("{}: {} = {}", getFieldName(), method, value);
  }

  protected String getSelfName() {
    return selfPath;
  }

  protected String getFieldName() {
    return selfPath + "." + loggedField;
  }

  protected void setLoggedField(String fieldName) {
    this.loggedField = fieldName;
  }

  /**
   * Overrideable, typically for self-nested handlers.
   */
  protected LoggingElementHandler buildNewElementHandler() {
    return new LoggingElementHandler(getFieldName());
  }

  @Override
  public ElementHandler newObject() {
    reportEvent("newObject");
    return buildNewElementHandler();
  }

  @Override
  public void endObject() {
    reportEvent("end");
  }

  @Override
  public void fieldName(String text) {
    loggedField = text;
    PushJsonLogger.LOG.info("{} >> fieldName {}", selfPath, text);
  }

  @Override
  public void newArray() {
    reportEvent("newArray");
  }

  @Override
  public void endArray() {
    reportEvent("endArray");
  }

  @Override
  public void withString(String text) {
    reportValue("withString", text);
  }

  @Override
  public void withBigInteger(BigInteger bigIntegerValue) {
    reportValue("withBigInteger", bigIntegerValue.toString());
  }

  @Override
  public void withBigDecimal(BigDecimal decimalValue) {
    reportValue("withBigDecimal", decimalValue.toString());
  }

  @Override
  public void withBoolean(boolean b) {
    reportValue("withBoolean", Boolean.toString(b));
  }

  @Override
  public void withNull() {
    reportValue("withNull", "null");
  }
}
