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

import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class CommandDispatch {

  private CommandExec exec;

  public void dispatch(List<String> args) {
    exec = buildExec(args);
    exec.setArgs(args);
    exec.exec();
  }

  private CommandExec buildExec(List<String> args) {
    if (args.isEmpty()) {
      return new UnrecognizedCommand();
    }

    return CommandDef.lookup(args.get(0));
  }

  /**
   * @return
   */
  public Object getResult() {
    return exec.getResult();
  }
}
