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

/**
 * Adds simple error and reporting capabilities.  With a shared
 * {@link ErrorAccumulator} instance, aggregates counts for sub-handlers
 * are easy to configure.
 * 
 * @author Lee Carver
 */
public class ErrorHandlingElementHandler extends LoggingElementHandler {

  public static interface ErrorAccumulator {
    void addError(int count);
    int getTotal();
  }

  public static class BasicAccumulator implements ErrorAccumulator {
    private int total = 0;

    public void addError(int count) {
      total += count;
    }

    public int getTotal() {
      return total ;
    }
  }

  private final ErrorAccumulator errors;

  public ErrorHandlingElementHandler(String selfPath, ErrorAccumulator errors) {
    super(selfPath);
    this.errors = errors;
  }

  public ErrorHandlingElementHandler(String selfPath) {
    this(selfPath, new BasicAccumulator());
  }

  protected ErrorAccumulator getAccumulator() {
    return errors;
  }

  public void addError(int count) {
    errors.addError(count);
  }

  public void report(String text) {
    PushJsonLogger.LOG.info("{}: {}", getFieldName(), text);
  }

  public void warn(String text) {
    PushJsonLogger.LOG.warn("{}: {}", getFieldName(), text);
    addError(1);
  }

  public static class Nestable extends ErrorHandlingElementHandler {

    public Nestable(String selfPath, ErrorAccumulator errors) {
      super(selfPath, errors);
    }

    public Nestable(String selfPath) {
      super(selfPath);
    }

    protected LoggingElementHandler buildNewElementHandler() {
      return new Nestable(getFieldName(), getAccumulator());
    }
  }
}
