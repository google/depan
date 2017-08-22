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

import com.google.devtools.depan.cmd.CmdLogger;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class UnrecognizedCommand extends AbstractCommandExec {

  @Override
  public void exec() {
    CmdLogger.LOG.warn(buildMessage());
  }

  @Override
  public Object getResult() {
    return new Integer(1);
  }

  public String buildMessage() {
    List<String> args = getArgs();
    if (null == args) {
      return "Null argument list supplied";
    }
    if (args.isEmpty()) {
      return "Empty argument list supplied";
    }

    int argCnt = args.size();
    if (1 == argCnt) {
      return fmt("Unrecognized command: {0}", args.get(0));
    }

    StringBuilder result = new StringBuilder();
    result.append(
        fmt("Unrecognized command: {0} with {1} parameters:",
            args.get(0), argCnt - 1));
    for (String param : args.subList(1, argCnt)) {
      result.append(" ");
      result.append(param);
    }
    return result.toString();
  }

  private static String fmt(String pattern, Object... arguments) {
    return MessageFormat.format(pattern, arguments);
  }
}
