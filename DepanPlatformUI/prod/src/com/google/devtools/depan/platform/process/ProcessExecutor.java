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

package com.google.devtools.depan.platform.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Execute a system {@link Process} on this thread.  Standard input and
 * output are collected via per-process threads and their contents
 * are available after {@link #execProcess(ProcessBuilder)} terminates.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public abstract class ProcessExecutor {

  private int exitCode;
  private String outText;
  private String errText;

  /**
   * Hook method for derived classes to configure the output thread.
   * A call to {@link Thread#setName(String)} is common.
   * 
   * @param forOut {@link Thread} to configure
   */
  protected abstract void configOutThread(Thread forOut);

  /**
   * Hook method for derived classes to configure the error thread.
   * A call to {@link Thread#setName(String)} is common.
   * 
   * @param forErr {@link Thread} to configure
   */
  protected abstract void configErrThread(Thread forErr);

  /**
   * Compute the effective POM for the supplied POM definition file.
   * After this method returns, various results from the execution can
   * be retrieved.  The available results include the effective pom and
   * the exit code from the process execution.
   * 
   * This is a Template method which relies on the methods
   * {@link #configOutThread(Thread)} and {@link #configErrThread(Thread)}.
   * Each derived class is expected to implement these hook methods.
   */
  protected void execProcess(ProcessBuilder builder)
      throws IOException, InterruptedException {
    Process process = builder.start();

    ReaderThread outThread = new ReaderThread(process.getInputStream());
    configOutThread(outThread);
    outThread.start();

    ReaderThread errThread = new ReaderThread(process.getErrorStream());
    configErrThread(errThread);
    errThread.start();

    exitCode = process.waitFor();
    outThread.shutdown();
    errThread.shutdown();

    outText = outThread.getResult();
    errText = errThread.getResult();
  }

  public int getExitCode() {
    return exitCode;
  }

  public String getOut() {
    return outText;
  }

  public String getErr() {
    return errText;
  }

  private static class ReaderThread extends Thread {
    private InputStream input;
    private StringBuilder result = new StringBuilder();

    public ReaderThread(InputStream input) {
      this.input = input;
    }

    public String getResult() {
      return result.toString();
    }

    public void shutdown() {
      if (isAlive()) {
        interrupt();
      }
    }

    @Override
    public void run() {
      Reader reader = new InputStreamReader(input);
      boolean ready = true;
      while (ready) {
        try {
          // It's OK if this blocks until a character is ready.
          int next = reader.read();
          if (next < 0) {
            ready = false;
          } else {
            result.append((char) next);
          }
        } catch (IOException e) {
          ready = false;
        }
      }
    }
  }
}
