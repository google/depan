/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.ruby;

import com.google.devtools.depan.filesystem.FileSystemRelationContributor;
import com.google.devtools.depan.graph_doc.model.DependencyModel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class RubyPluginActivator implements BundleActivator {

  /** Plug-in ID used to identify this plug-in. */
  public static final String PLUGIN_ID = "com.google.devtools.depan.ruby";


  public static final DependencyModel RUBY_MODEL = buildDependencyModel();

  public RubyPluginActivator() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }

  private static DependencyModel buildDependencyModel() {
    DependencyModel.Builder result = new DependencyModel.Builder();
    result.addRelationContrib(RubyRelationContributor.ID);
    result.addRelationContrib(FileSystemRelationContributor.ID);
    return result.build();
  }
}
