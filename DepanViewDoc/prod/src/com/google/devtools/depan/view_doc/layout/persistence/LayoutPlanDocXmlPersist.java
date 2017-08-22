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

package com.google.devtools.depan.view_doc.layout.persistence;

import com.google.devtools.depan.persistence.AbstractDocXmlPersist;
import com.google.devtools.depan.persistence.ObjectXmlPersist;
import com.google.devtools.depan.persistence.XStreamFactory;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import java.net.URI;

/**
 * Provide easy to use load and save methods for
 * {@link LayoutPlanDocument}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class LayoutPlanDocXmlPersist
    extends AbstractDocXmlPersist<LayoutPlanDocument<LayoutPlan>> {

  private final static LayoutPlanDocXStreamConfig DOC_CONFIG =
      new LayoutPlanDocXStreamConfig();

  public LayoutPlanDocXmlPersist(ObjectXmlPersist xmlPersist) {
    super(xmlPersist);
  }

  public static LayoutPlanDocXmlPersist build(boolean readable) {
    ObjectXmlPersist persist = XStreamFactory.build(readable, DOC_CONFIG);
    return new LayoutPlanDocXmlPersist(persist);
  }

  /////////////////////////////////////
  // Hook method implementations for AbstractDocXmlPersist

  @SuppressWarnings("unchecked")
  @Override
  protected LayoutPlanDocument<LayoutPlan> coerceLoad(Object load) {
      return (LayoutPlanDocument<LayoutPlan>) load;
  }

  @Override
  protected String buildLoadErrorMsg(URI uri) {
    return formatErrorMsg("Unable to load layout plan from {0}", uri);
  }

  @Override
  public String buildSaveErrorMsg(URI uri) {
    return formatErrorMsg("Unable to save layout plan to {0}", uri);
  }
}
