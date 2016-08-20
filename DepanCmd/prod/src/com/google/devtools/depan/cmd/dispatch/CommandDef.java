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

package com.google.devtools.depan.cmd.dispatch;

import com.google.devtools.depan.cmd.analyzers.AnalyzerFactory;
import com.google.devtools.depan.cmd.setops.SubtractFactory;
import com.google.devtools.depan.cmd.setops.UnionFactory;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CommandDef {

  private static Map<String, CommandFactory> cmds = Maps.newHashMap();
  static {
    cmds.put("analyze", new AnalyzerFactory());
    cmds.put("subtract", new SubtractFactory());
    cmds.put("union", new UnionFactory());
  }

  /**
   * @param string
   * @return
   */
  public static CommandExec lookup(String string) {
    CommandFactory result = cmds.get(string);
    if (null != result) {
      return result.buildCommand();
    }

    return new UnrecognizedCommand();
  }

}
