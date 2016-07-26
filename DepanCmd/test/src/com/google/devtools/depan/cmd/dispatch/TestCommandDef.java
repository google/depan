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

import static org.junit.Assert.assertTrue;

import com.google.devtools.depan.cmd.analyzers.AnalyzerCommand;
import com.google.devtools.depan.cmd.dispatch.CommandDef;
import com.google.devtools.depan.cmd.dispatch.CommandExec;
import com.google.devtools.depan.cmd.setops.UnionCommand;

import org.junit.Test;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class TestCommandDef {

  @Test
  public void testAnalyzeLookup() {
    CommandExec cmd = CommandDef.lookup("analyze");
    assertTrue(cmd instanceof AnalyzerCommand);
  }

  @Test
  public void testUnionLookup() {
    CommandExec cmd = CommandDef.lookup("union");
    assertTrue(cmd instanceof UnionCommand);
  }
}
