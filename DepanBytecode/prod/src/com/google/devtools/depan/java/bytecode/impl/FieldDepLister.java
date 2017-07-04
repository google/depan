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

package com.google.devtools.depan.java.bytecode.impl;

import org.objectweb.asm.FieldVisitor;

/**
 * Implements a visitor of the ASM package, to find the dependencies in a Field
 * and build the dependency tree. A single {@link FieldDepLister} is used
 * for each field found in a class.
 * 
 * To build the dependencies tree, it calls the methods of a
 * DependenciesListener.
 * 
 * A singleton instance is provided for reuse.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class FieldDepLister extends FieldVisitor {

  public FieldDepLister(int api) {
    super(api);
  }
}
