/*
 * Copyright 2015 The Depan Project Authors
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

import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.relations.models.RelationSetDescriptor;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Return the proper string label from {@link GraphEdgeMatcherDescriptor}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
class RelationSetLabelProvider extends BaseLabelProvider
    implements ILabelProvider {

  @Override
  public Image getImage(Object element) {
    return null;
  }

  @Override
  public String getText(Object element) {
    return ((RelationSetDescriptor) element).getName();
  }

  // Only need one instance
  public static final RelationSetLabelProvider PROVIDER =
      new RelationSetLabelProvider();
}