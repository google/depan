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

package com.google.devtools.depan.graphml.builder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collection of logging and GraphML utilities.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class GraphMLLogger {

  private GraphMLLogger() {
    // Prevent instantiation.
  }

  // Common logger for this package
  public static final Logger LOG =
      Logger.getLogger(GraphMLLogger.class.getPackage().getName());

  public static void logException(String msg, Throwable err) {
    LOG.log(Level.SEVERE, msg, err);
  }
}
