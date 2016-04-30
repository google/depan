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

package com.google.devtools.depan.platform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Provide a generic form of listener support.  Instances manage the list
 * of listeners.  To fire an event, pass a {@link Dispatcher} that calls
 * the correct notification methods (e.g. {@code xxxYyyChanged()}) on
 * each listener.
 *
 * @param <L> Class for listeners
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class ListenerManager<L> {
  private List<L> listeners = Lists.newArrayList();

  /**
   * Define how events are dispatched to listeners.  Normally the
   * {@link #dispatch(Object)} will call an appropriate {@code xxxYyyChanged()})
   * method on a series of listener objects.  Typical
   * {@link #captureException(RuntimeException)} behavior is to ignore errors,
   * or maybe to log them.  It also feasible to collect all the
   * {@code Exceptions} and provide them as a result after all listeners have
   * been fired.
   *
   * @param <L> Class for listeners
   */
  public interface Dispatcher<L> {
    void dispatch(L listener);

    void captureException(RuntimeException errAny);
  }

  public void dispose() {
    listeners = null;
  }

  public void addListener(L listener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeListener(L listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener);
    }
  }

  public void fireEvent(Dispatcher<L> dispatcher) {
    // Allow listeners to remove themselves
    ImmutableList<L> snapshot = ImmutableList.copyOf(listeners);
    for (L listener : snapshot) {
      try {
        dispatcher.dispatch(listener);
      } catch (RuntimeException errAny) {
        dispatcher.captureException(errAny);
      }
    }
  }
}
