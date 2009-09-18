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

package com.google.devtools.depan.java.integration;

import com.google.devtools.depan.eclipse.persist.XStreamFactory.Config;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;

import com.thoughtworks.xstream.XStream;

public class JavaDefinitions {

  public static Config configXmlPersist = new Config() {
    public void  config(XStream xstream) {
      xstream.alias("java-field", FieldElement.class);
      xstream.alias("java-interface", InterfaceElement.class);
      xstream.alias("java-method", MethodElement.class);
      xstream.alias("java-package", PackageElement.class);
      xstream.alias("java-type", TypeElement.class);
      xstream.alias("java-relation", JavaRelation.class);

      xstream.useAttributeFor(TypeElement.class, "fullyQualifiedName");
      xstream.aliasAttribute(TypeElement.class, "fullyQualifiedName", "name");
    }
  };
}
