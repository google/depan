/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.pushjson;

/**
 * Do the right thing for documents.  Users of the {@link PushDownJsonHandler}
 * should derive a integration class and need only implement the factory method
 * for the top-level document node.  An instance derived from this type
 * should be passed to
 * {@code PushDownJsonHandler#parseDocument(ElementHandler)}.
 */
public class DocumentHandler extends DefaultElementHandler {

  @Override
  public final void endObject() {
    throw new UnsupportedOperationException(
        "Attempt to exit document as an element");
  }
}
