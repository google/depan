/*
 * Copyright 2007 The Depan Project Authors
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

/**
 * Simple interface for a ProgressListener.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public interface ProgressListener {

  /**
   * inform the listener about the current job.
   * 
   * @param curentJob String representing the current job
   * @param n number of jobs executed
   * @param total total number of jobs to execute.
   */
  public void progress(String curentJob, int n, int total);
}
