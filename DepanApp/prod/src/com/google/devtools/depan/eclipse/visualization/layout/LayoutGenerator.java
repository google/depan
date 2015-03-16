/*
 * Copyright 2013 The Depan Project Authors
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
package com.google.devtools.depan.eclipse.visualization.layout;

/**
 * Transform a {@link LayoutContext} into a {@link LayoutRunner}.
 * The {@code LayoutRunner} can be used to compute positions for nodes
 * included in the graph supplied by the {@code LayoutContext}.
 * 
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
public interface LayoutGenerator {

  LayoutRunner buildRunner(LayoutContext context);

  // TODO: Make into an Eclipse/OGSi extension point.
  // Add order and various aspect categories.
}
