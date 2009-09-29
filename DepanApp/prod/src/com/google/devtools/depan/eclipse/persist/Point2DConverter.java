/*
 * Copyright 2009 Google Inc.
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

package com.google.devtools.depan.eclipse.persist;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import java.awt.geom.Point2D;

/**
 * {@code XStream} converter to handle {@code Point2D}s.  Although that type
 * is just an interface, we serialize everything as the Double flavor.
 * 
 * @author <a href="mailto:leeca@google.com">Lee Carver</a>
 */
public class Point2DConverter implements Converter {

  public static final String POS_TAG = "pos";

  public Point2DConverter(Mapper mapper) {
  }

  @Override
  @SuppressWarnings("unchecked")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return Point2D.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    Point2D pos = (Point2D) source;
    double posX = pos.getX();
    double posY = pos.getY();
    
    writer.addAttribute("x", Double.toString(posX));
    writer.addAttribute("y", Double.toString(posY));
  }

  /**
   * {@inheritDoc}
   * <p>
   * This implementation assumes that a {@code GraphModel}, used to find nodes,
   * can be retrieved from the {@code UnmarshallingContext} with the key
   * {@code GraphModel.class}.
   */
  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      String textX = reader.getAttribute("x");
      String textY = reader.getAttribute("y");
      Double posX = Double.valueOf(textX);
      Double posY = Double.valueOf(textY);
      return new Point2D.Double(posX, posY);
    } catch (RuntimeException err) {
      // TODO Auto-generated catch block
      err.printStackTrace();
      throw err;
    }
  }
}
