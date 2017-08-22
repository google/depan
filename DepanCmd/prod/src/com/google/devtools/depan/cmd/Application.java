/*
 * Copyright 2007 The Depan Project Authors
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

package com.google.devtools.depan.cmd;

import com.google.devtools.depan.cmd.dispatch.CommandDispatch;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import java.util.Arrays;
import java.util.Map;

/**
 * Obtain the command line parameters from OSGi, and use them to
 * dispatch (find and execute) a command.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class Application implements IApplication {

  @Override
  public Object start(IApplicationContext context) throws Exception {
    final Map<?, ?> args = context.getArguments();
    final String[] appArgs = (String[]) args.get("application.args");
    CommandDispatch cli = new CommandDispatch();
    cli.dispatch(Arrays.asList(appArgs));

    Object result = cli.getResult();
    if (IApplication.EXIT_OK != result) {
      CmdLogger.LOG.error("Depan Cmd failure with result: {}", result);
    }
    return result;
  }

  @Override
  public void stop() {
  }
}
