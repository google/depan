/*
 * Copyright 2017 The Depan Project Authors
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

package com.google.devtools.depan.java.bytecode.eclipse;

import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.java.bytecode.impl.ClassDepLister;
import com.google.devtools.depan.java.bytecode.impl.FieldDepLister;
import com.google.devtools.depan.java.bytecode.impl.MethodDepLister;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Abstract Factory for Java bytecode analysis elements.
 * 
 * @author <a href="mailto:leeca@pnambic.com">Lee Carver</a>
 */
public interface AsmFactory {

  /**
   * Provide the ASM OpCode Level for this factory.
   * 
   * Mostly used within constructors for generated visitors.
   */
  int getApiLevel();

  /**
   * Provide a reusable {@link FieldVisitor} instantce.
   */
  FieldVisitor getGenericFieldVisitor();

  /**
   * Provide a new {@link ClassVisitor} instance.
   */
  ClassVisitor buildClassVisitor(
      DependenciesListener builder, FileElement fileNode);

  /**
   * Provide a new {@link MethodVisitor} instance.
   */
  MethodVisitor buildMethodVisitor(
      DependenciesListener builder, MethodElement method);

  /////////////////////////////////////
  // Abstract Factory provides the standard factory methods.

  public static abstract class AbstractAsmFactory implements AsmFactory {

    @Override
    public ClassVisitor buildClassVisitor(
        DependenciesListener builder, FileElement fileNode) {
      return new ClassDepLister(this, builder, fileNode);
    }

    @Override
    public MethodVisitor buildMethodVisitor(
        DependenciesListener dl, MethodElement m) {
      return new MethodDepLister(this, dl, m);
    }
  }

  /////////////////////////////////////
  // ASM-4

  public static final AsmFactory ASM4_FACTORY = new Asm4Factory();

  public static class Asm4Factory extends AbstractAsmFactory {
    private static final int ASM_LEVEL = Opcodes.ASM4;

    private static final FieldDepLister GENERIC_FIELD_LISTER =
        new FieldDepLister(ASM_LEVEL);

    @Override
    public int getApiLevel() {
      return ASM_LEVEL;
    }

    @Override
    public FieldVisitor getGenericFieldVisitor() {
      return GENERIC_FIELD_LISTER;
    }
  }

  /////////////////////////////////////
  // ASM-5

  public static final AsmFactory ASM5_FACTORY = new Asm5Factory();

  public static class Asm5Factory extends AbstractAsmFactory {
    private static final int ASM_LEVEL = Opcodes.ASM5;

    private static final FieldDepLister GENERIC_FIELD_LISTER =
        new FieldDepLister(ASM_LEVEL);

    @Override
    public int getApiLevel() {
      return ASM_LEVEL;
    }

    @Override
    public FieldVisitor getGenericFieldVisitor() {
      return GENERIC_FIELD_LISTER;
    }
  }
}
