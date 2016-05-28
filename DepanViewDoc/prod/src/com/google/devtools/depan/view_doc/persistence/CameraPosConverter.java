/*
 * Copyright 2014 The Depan Project Authors
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
package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.view_doc.model.CameraPosPreference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@code XStream} converter to handle {@code Point2D}s.  Although that type
 * is just an interface, we serialize everything as the Double flavor.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class CameraPosConverter implements Converter {

  public static final String CAMERA_POS_TAG = "camera-pos";

  public CameraPosConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(CAMERA_POS_TAG, CameraPosPreference.class);
    xstream.registerConverter(new CameraPosConverter());
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return CameraPosPreference.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    CameraPosPreference pos = (CameraPosPreference) source;
    Float posX = pos.getX();
    Float posY = pos.getY();
    Float posZ = pos.getZ();

    writer.addAttribute("x", Float.toString(posX));
    writer.addAttribute("y", Float.toString(posY));
    writer.addAttribute("z", Float.toString(posZ));
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      String textX = reader.getAttribute("x");
      String textY = reader.getAttribute("y");
      String textZ = reader.getAttribute("z");
      Float posX = Float.valueOf(textX);
      Float posY = Float.valueOf(textY);
      Float posZ = Float.valueOf(textZ);
      return new CameraPosPreference(posX, posY, posZ);
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }
}
