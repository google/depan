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

package com.google.devtools.depan.java;

import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.persistence.plugins.XStreamConfig;

import com.thoughtworks.xstream.XStream;

import org.osgi.framework.Bundle;

import java.util.Collection;
import java.util.Collections;

public class JavaXStreamConfig implements XStreamConfig {

  @Override
  public void config(XStream xstream) {
    xstream.alias("java-field", FieldElement.class);
    xstream.alias("java-interface", InterfaceElement.class);
    xstream.alias("java-method", MethodElement.class);
    xstream.alias("java-package", PackageElement.class);
    xstream.alias("java-type", TypeElement.class);
    xstream.alias("java-relation", JavaRelation.class);

    xstream.useAttributeFor(TypeElement.class, "fullyQualifiedName");
    xstream.aliasAttribute(TypeElement.class, "fullyQualifiedName", "name");
  }

  @Override
  public Collection<? extends Bundle> getDocumentBundles() {
    return Collections.emptyList();
  }
}
