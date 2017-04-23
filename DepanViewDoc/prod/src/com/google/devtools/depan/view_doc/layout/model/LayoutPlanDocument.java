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

package com.google.devtools.depan.view_doc.layout.model;

import com.google.devtools.depan.resources.PropertyDocument;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutPlanDocument<T extends LayoutPlan>
    extends PropertyDocument<T> {

  public static final String INFO_KEY = "info";

  public static final String SUMMARY_KEY = "summary";

  private final T info;

  public LayoutPlanDocument(String name, T info) {
    super(name);
    this.info = info;
  }

  public T getInfo() {
    return info;
  }

  public void setSummary(String summary) {
    setProperty(SUMMARY_KEY, summary);
  }

  public String getSummary() {
    return getProperty(SUMMARY_KEY);
  }

  @Override
  public Object getObject(String key) {
    if (INFO_KEY.equals(key)) {
      return getInfo();
    }
    return super.getObject(key);
  }

  @Override
  protected boolean isWritable(String key) {
    if (INFO_KEY.equals(key)) {
      return false;
    }
    return super.isWritable(key);
  }

  public String buildSummary() {
    return info.buildSummary();
  }
}
