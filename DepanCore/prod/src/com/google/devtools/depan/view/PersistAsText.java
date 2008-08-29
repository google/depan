/*
 * Copyright 2007 Google Inc.
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

package com.google.devtools.depan.view;

import com.google.devtools.depan.model.GraphNode;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

/**
 * @author <a href='mailto:leeca@google.com'>Lee Carver</a>
 */
// TODO(leeca): Link this to an "Export .." editor action.
public class PersistAsText {

  private static final String GRAPHASSOCIATE = "graph.txt";
  private static final String LOCATIONSFILE = "loc";

  private ViewModel viewModel;

  /**
   * URI for the filename of the graph associated with this view.
   */
  private URI parentUri;

  public PersistAsText(ViewModel viewModel, URI parentUri) {
    this.viewModel = viewModel;
    this.parentUri = parentUri;
  }

  public ViewModel getViewModel() {
    return viewModel;
  }

  public URI getParentUri() {
    return parentUri;
  }

  public void save(URI uri) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(
          new File(uri)));

      out.write(GRAPHASSOCIATE + " " + parentUri.toString());
      out.newLine();

      out.write(LOCATIONSFILE + " " + viewModel.getName());
      out.newLine();

      for (GraphNode node : viewModel.getNodes()) {
        Point2D location = viewModel.getNodeLocation(node);
        out.write(node.getId().toString() +
            " " + location.getX() +
            " " + location.getY());
        out.newLine();
      }

      out.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load a view previously saved at the given URI.
   * @param uri
   * @return a PersistentView containing the view, or null if loading failed.
   */
  public static PersistAsText load (URI uri) {
    throw new UnsupportedOperationException();
  }
}
