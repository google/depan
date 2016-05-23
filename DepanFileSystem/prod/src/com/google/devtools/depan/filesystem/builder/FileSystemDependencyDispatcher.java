/*
 * Copyright 2009 The Depan Project Authors
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
package com.google.devtools.depan.filesystem.builder;

import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.api.GraphBuilder;
import com.google.devtools.depan.model.builder.chain.DependenciesDispatcher;

/**
 * A simple dependency dispatcher that accepts everything.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class FileSystemDependencyDispatcher extends DependenciesDispatcher {

  public FileSystemDependencyDispatcher(GraphBuilder builder) {
    super(builder);
  }

  @Override
  protected boolean passFilter(GraphNode node) {
    return true;
  }
}
