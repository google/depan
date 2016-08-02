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

package com.google.devtools.depan.nodes.filters.context;

import static org.junit.Assert.*;

import com.google.devtools.depan.nodes.filters.model.ContextKey;
import com.google.devtools.depan.nodes.filters.model.ContextKey.Base;
import com.google.devtools.depan.nodes.filters.model.FilterContext;

import com.google.common.collect.Maps;

import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class MapContextTest {

  @Test
  public void testBasic() {
    Map<ContextKey, Object> testData = Maps.newHashMap();
    String testValue = "Any object";
    testData.put(Base.UNIVERSE, testValue);
    FilterContext testContext = new MapContext(testData);
    assertEquals(testValue, testContext.get(Base.UNIVERSE));
  }
}
