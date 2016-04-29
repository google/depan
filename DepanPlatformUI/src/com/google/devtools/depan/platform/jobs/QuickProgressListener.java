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
 * A {@link ProgressListener} that slows down a too fast
 * {@link ProgressListener}. Take a progressListener as a constructor, and
 * instead of sending each progress information, send one over x (x = slowDown).
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class QuickProgressListener implements ProgressListener {

  /**
   * The progressListener to slowDown
   */
  private ProgressListener listener;
  
  /**
   * slow down rate 
   */
  private int slowDown;
  
  /**
   * number of progress updates eaten without sending a real notification.
   */
  private int position = 0;
  
  /**
   * Create a {@link QuickProgressListener} monitoring and filtering the given
   * {@link ProgressListener}, slowing it down at the given rate.
   * 
   * @param listener progress listener to slowdown
   * @param slowDown slow down factor. n = send 1 notification over n.
   */
  public QuickProgressListener(ProgressListener listener, int slowDown) {
    this.listener = listener;
    this.slowDown = slowDown;
  }

  @Override
  public void progress(String curentJob, int n, int total) {
    position++;
    // send one progress notification over slowDown;
    // always send the last progress notification.
    if (position >= slowDown || n == total) {
      position = 0;
      listener.progress(curentJob, n, total);
    }
  }

}
