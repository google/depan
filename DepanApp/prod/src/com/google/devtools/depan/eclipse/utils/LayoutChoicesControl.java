/*
 * Copyright 2013 The Depan Project Authors
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
package com.google.devtools.depan.eclipse.utils;

import com.google.devtools.depan.eclipse.visualization.layout.LayoutGenerator;

import com.google.devtools.edges.matchers.GraphEdgeMatcherDescriptor;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.util.List;

/**
 * A single composite for selecting the layout graph and relationship set
 * as a unified pair.
 * 
 * @author SERVICE-NOW\lee.carver
 */
public class LayoutChoicesControl extends Composite {

  public enum Style {
    LINEAR {
      @Override
      GridLayoutFactory getLayoutFactory() {
        return GridLayoutFactory.swtDefaults().numColumns(4);
      }
    },
    STACKED {
      @Override
      GridLayoutFactory getLayoutFactory() {
        return GridLayoutFactory.fillDefaults().numColumns(2);
      }
    },
    TIGHT {
      @Override
      GridLayoutFactory getLayoutFactory() {
        Point spacing = LayoutConstants.getSpacing();
        return GridLayoutFactory.fillDefaults().numColumns(2)
            .spacing(spacing.x, (2 * spacing.y) / 3 );
      }
    };
    abstract GridLayoutFactory getLayoutFactory();
  }

  private LayoutPickerControl layoutPicker;

  private GraphEdgeMatcherSelectorControl edgeMatcherSelector;

  public LayoutChoicesControl(Composite parent, Style style) {
    super(parent, SWT.NONE);
    style.getLayoutFactory().applyTo(this);

    GridDataFactory gridDataBldr = GridDataFactory.fillDefaults()
        .grab(true, true)
        .align(SWT.FILL, SWT.CENTER);

    Label layoutLabel = new Label(this, SWT.NONE);
    layoutLabel.setText("Layout graph: ");
    gridDataBldr.applyTo(layoutLabel);

    layoutPicker = new LayoutPickerControl(this);
    gridDataBldr.applyTo(layoutPicker);

    Label edgeMatcherLabel = new Label(this, SWT.NONE);
    edgeMatcherLabel.setText("Layout edges: ");
    gridDataBldr.applyTo(edgeMatcherLabel);

    edgeMatcherSelector = new GraphEdgeMatcherSelectorControl(this);
    gridDataBldr.applyTo(edgeMatcherSelector);
  }

  public void setLayoutChoices(List<String> layoutNames) {
    layoutPicker.setLayoutChoices(layoutNames);
  }

  public void selectLayout(String layoutName) {
    layoutPicker.selectLayout(layoutName);
  }

  public LayoutGenerator getLayoutGenerator() {
    return layoutPicker.getLayoutChoice();
  }

  public String getLayoutName() {
    return layoutPicker.getLayoutName();
  }

  public GraphEdgeMatcherDescriptor getEdgeMatcher() {
    return edgeMatcherSelector.getSelection();
  }

  /**
   * After changing the relation set choices, the parent for this control
   * often needs to be resized with a {@code layout()} call.
   */
  public void setEdgeMatcherInput(
      GraphEdgeMatcherDescriptor selectedRelSet, List<GraphEdgeMatcherDescriptor> choices) {
    edgeMatcherSelector.setInput(selectedRelSet, choices);
  }
}
