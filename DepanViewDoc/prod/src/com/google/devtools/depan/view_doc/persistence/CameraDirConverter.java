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
package com.google.devtools.depan.view_doc.persistence;

import com.google.devtools.depan.view_doc.model.CameraDirPreference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * {@code XStream} converter to handle {@code CameraDirPreference}s.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public class CameraDirConverter implements Converter {

  public static final String CAMERA_DIR_TAG = "camera-dir";

  public CameraDirConverter() {
  }

  public static void configXStream(XStream xstream) {
    xstream.aliasType(CAMERA_DIR_TAG, CameraDirPreference.class);
    xstream.registerConverter(new CameraDirConverter());
  }

  @Override
  @SuppressWarnings("rawtypes")  // Parent type uses raw type Class
  public boolean canConvert(Class type) {
    return CameraDirPreference.class.isAssignableFrom(type);
  }

  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    CameraDirPreference dir = (CameraDirPreference) source;
    Float dirX = dir.getX();
    Float dirY = dir.getY();
    Float dirZ = dir.getZ();

    writer.addAttribute("x", Float.toString(dirX));
    writer.addAttribute("y", Float.toString(dirY));
    writer.addAttribute("z", Float.toString(dirZ));
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    try {
      String textX = reader.getAttribute("x");
      String textY = reader.getAttribute("y");
      String textZ = reader.getAttribute("z");
      Float dirX = Float.valueOf(textX);
      Float dirY = Float.valueOf(textY);
      Float dirZ = Float.valueOf(textZ);
      return new CameraDirPreference(dirX, dirY, dirZ);
    } catch (RuntimeException err) {
      err.printStackTrace();
      throw err;
    }
  }
}
