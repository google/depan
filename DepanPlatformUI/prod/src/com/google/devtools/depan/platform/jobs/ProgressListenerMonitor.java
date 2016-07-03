/*
 * Copyright 2009 The Depan Project Authors
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

package com.google.devtools.depan.platform.jobs;

import com.google.devtools.depan.platform.jobs.ProgressListener;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Report incremental dependency loading progress by writing task steps
 * to an Eclipse progress monitor's task name.
 * 
 * @author <a href="leeca@google.com">Lee Carver</a>
 */
public class ProgressListenerMonitor implements ProgressListener {

  /**
   * Progress monitor for reporting progress.
   */
  private IProgressMonitor progressMonitor;

  /**
   * Build a progress listener that updates the {@code IProgressMonitor}'s
   * task name.
   * 
   * @param progressMonitor progress monitor to update
   */
  public ProgressListenerMonitor(IProgressMonitor progressMonitor) {
    this.progressMonitor = progressMonitor;
  }

  @Override
  public void progress(String curentJob, int n, int total) {
    progressMonitor.setTaskName(
        "Loading " + n + "/" + total + ": " + curentJob);
  }
}
