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

package com.google.devtools.depan.relations.eclipse.ui.widgets;

import com.google.devtools.depan.platform.eclipse.ui.widgets.GenericSaveLoadControl;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import org.eclipse.swt.widgets.Composite;

/**
 * Control for saving and loading {@code RelationSet} documents
 * (a.k.a {@link RelationSetDescriptor}s).  The class defines the type-specific
 * strings and factories for the supplied generic type
 * {@link RelationSetDescriptor}.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public abstract class RelationSetSaveLoadControl
    extends GenericSaveLoadControl<RelationSetDescriptor> {

  public RelationSetSaveLoadControl(Composite parent) {
    super(parent, RelationSetSaveLoadConfig.CONFIG);
  }
}
