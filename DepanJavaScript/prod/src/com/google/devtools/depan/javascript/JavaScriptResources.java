/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.javascript;

/**
 * Define the resources that the JavaScript plug-in defines and uses.
 * <p/>
 * In the current implementation, all resources are "borrowed" from other
 * plug-ins, especially the {@code FileSystem} and {@code Java} plugins.
 * As this plug-in matures, plug-in specific images and other resources are
 * anticipated.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class JavaScriptResources {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID =
      "com.google.devtools.depan.javascript";

  // TODO(leeca): Add JavaScript specific resources, instead of borrowing
  // images, etc. from Java, FileSystem, etc.
}
