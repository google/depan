/*
 * Copyright 2010 The Depan Project Authors
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

package com.google.devtools.depan.eclipse.utils.relsets;

import com.google.devtools.depan.model.RelationshipSet;

/**
 * A type used for pairing a name with each {@link RelationshipSet}.
 * This is used to provide a title containing information that is not in the
 * RelationshipSet itself, like its original file on the disk.
 * 
 * <p>In the future, it would be nice to add icon support.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
//TODO(leeca): Add some icon support?
public interface RelSetDescriptor {

  String getName();

  RelationshipSet getRelSet();
}
