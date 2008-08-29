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

import com.google.devtools.depan.model.GraphModel;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.ResourceCache;

import org.apache.commons.collections15.Transformer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class PersistentView implements Transformer<GraphNode, Point2D> {

  /**
   * A Serializable class for representing a 2D Point with double.
   * Just because java.awt.Point.Double is not Serializable in java 1.5....
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  static class Point2DDouble implements Serializable {
    double x;
    double y;
    public Point2DDouble(Point2D p) {
      x = p.getX();
      y = p.getY();
    }
    public Point2D.Double getPoint() {
      return new Point2D.Double(x, y);
    }
    private static final long serialVersionUID = -477433527629533518L;
  }

  private static final String GRAPHASSOCIATE = "graph.txt";
  private static final String LOCATIONSFILE = "loc";

  private Map<String, Point2DDouble> locations = null;
  private ViewModel view;
  private String name;
  
  /**
   * URI for the filename of the graph associated with this view.
   */
  private URI parentUri;

  public PersistentView(ViewModel view, URI parentUri) {
    this.view = view;
    this.parentUri = parentUri;
    setLocations();
  }
  
  private PersistentView() {
    locations = null;
  }
  
  public ViewModel getViewModel() {
    return view;
  }

  public URI getParentUri() {
    return parentUri;
  }

  public void setLocations() {
    if (null == locations) {
      locations = new HashMap<String, Point2DDouble>(); 
    }
    this.locations.clear();
    for (Map.Entry<GraphNode, Point2D> e : view.getLayoutMap().entrySet()) {
      this.locations.put(e.getKey().getId().toString(),
          new Point2DDouble(e.getValue()));
    }
  }

  public void save(URI uri) {
    try {
      ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(
          uri)));
      out.setLevel(Deflater.DEFAULT_COMPRESSION);
      
      out.putNextEntry(new ZipEntry(GRAPHASSOCIATE));
      out.write(parentUri.toString().getBytes());
      out.closeEntry();
      
      // save locations
      out.putNextEntry(new ZipEntry(LOCATIONSFILE));
      ObjectOutputStream objOut = new ObjectOutputStream(out);
      objOut.writeObject(view.getName());
      for (Map.Entry<String, Point2DDouble> e : locations.entrySet()) {
        objOut.writeObject(e.getKey());
        objOut.writeObject(e.getValue());
      }
      out.closeEntry();
      objOut.close();
      
      out.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
    
  /**
   * Load a view previously saved at the given URI.
   * @param uri
   * @return a PersistentView containing the view, or null if loading failed.
   */
  public static PersistentView load (URI uri) {
//    System.out.println("PersistentView.load()");
//    XStream xstream = setupXStream();
    PersistentView reloaded = new PersistentView();
    reloaded.locations = new HashMap<String, Point2DDouble>();

    try {
      File f = new File(uri);
      ZipFile zip = new ZipFile(f);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while (entries.hasMoreElements()) {
        ZipEntry tmp = entries.nextElement();
        if (tmp.getName().equals(LOCATIONSFILE)) {
          ObjectInputStream in = new ObjectInputStream(zip.getInputStream(tmp));
          loadLocations(reloaded, in);
          in.close();
        } else if (tmp.getName().equals(GRAPHASSOCIATE)) {
          InputStream in = zip.getInputStream(tmp);
          loadGraphAssociate(reloaded, in);
          in.close();
        }
      }
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
    if (null == reloaded.parentUri || null == reloaded.name) return null;
    GraphModel graph = ResourceCache.fetchGraphModel(reloaded.parentUri);
    reloaded.view = new ViewModel(reloaded.name, graph);
    reloaded.view.setStringNodes(reloaded.locations.keySet());
    reloaded.view.setLocations(reloaded.view.getNodes(), reloaded);
    reloaded.view.clearDirty();
    return reloaded;
  }
  
  public Point2D transform(GraphNode node) {
    if (locations.containsKey(node.getId().toString())) {
      return locations.get(node.getId().toString()).getPoint();
    } else {
      return new Point(0, 0);
    }
  }

  @SuppressWarnings("unchecked")
  private static void loadLocations(
      PersistentView persist, ObjectInputStream in) {
    try {
      persist.name = (String) in.readObject();
      while (true) {
        String key = (String) in.readObject();
        Point2DDouble value = (Point2DDouble) in.readObject();
        persist.locations.put(key, value);
      }
    } catch (EOFException e) {
      // normal end of the stream...
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    }
  }
  
  private static void loadGraphAssociate(
      PersistentView reloaded, InputStream in) {
    StringBuilder b = new StringBuilder();
    try {
      byte read = 0;
      while ((read = (byte) in.read()) != -1) {
        b.append((char) read);
      }
    } catch (EOFException e) {
      // normal end of the stream
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    try {
      reloaded.parentUri = new URI(b.toString());
    } catch (URISyntaxException e) {
      // set to null will cause the loading to fail later.
      reloaded.parentUri = null;
      e.printStackTrace();
    }
  }
}
