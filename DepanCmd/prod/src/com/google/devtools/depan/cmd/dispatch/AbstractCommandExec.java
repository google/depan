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
public abstract class AbstractCommandExec implements CommandExec {

  private List<String> args;

  @Override
  public void setArgs(List<String> args) {
    this.args = args;
  }

  protected List<String> getArgs() {
    return args;
  }

  protected String getArg(int index) {
    return args.get(index);
  }

  protected List<String> getParms() {
    return args.subList(1, args.size());
  }

  /**
   * Provide command parameters.  The origin for parameters is shifted one
   * so the zero-th parameter immediately follows the command argument 
   * (i.e. {@code getArg(1) == getParm(0)}.
   * 
   * The result might be {@code null}, but it should never throw an
   * {@link IndexOutOfBoundsException}.
   */
  protected String getParm(int index) {
    if (index < 0) {
      return null;
    }
    if ((1 + index) >= args.size()) {
      return null;
    }
    return args.get(1 + index);
  }

  protected String getParm(int index, String def) {
    String result = getParm(index);
    if (null != result) {
      return result;
    }
    return def;
  }
}
