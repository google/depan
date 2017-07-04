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

import com.google.devtools.depan.java.bytecode.eclipse.AsmFactory;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

/**
 * Implements a visitor of the ASM package, to find the dependencies in a method
 * and build the dependency tree. A single {@link MethodDepLister} is used for
 * each method found in a class.
 * 
 * To build the dependencies tree, it calls the methods of a
 * {@link DependenciesListener}.
 * 
 * Don't need to override visitLocalVariable() since a local variable must have
 * been instantiated at least once... we should already have its dependency set.
 * 
 * @author ycoppel@google.com (Yohann Coppel)
 */
public class MethodDepLister extends MethodVisitor {

  private final AsmFactory asmFactory;

  private DependenciesListener builder;

  private MethodElement thisElement;

  public MethodDepLister(
      AsmFactory asmFactory, DependenciesListener builder, MethodElement thisElem) {
    super(asmFactory.getApiLevel());
    this.asmFactory = asmFactory;
    this.builder = builder;
    this.thisElement = thisElem;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, thisElement, desc, visible);
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, thisElement, desc, visible);
    return null;
  }

  @Override
  public void visitFieldInsn(
      int opcode, String owner, String name, String desc) {
     // FIXME: is it a read ?
     builder.newDep(thisElement,
         new FieldElement(name, TypeNameUtil.fromDescriptor(desc),
           TypeNameUtil.fromInternalName(owner)),
         JavaRelation.READ);
  }

  @Override // ASM-5
  public void visitMethodInsn(
      int opcode, String owner, String name, String desc, boolean itf) {
    builder.newDep(thisElement, new MethodElement(desc, name,
      TypeNameUtil.fromInternalName(owner)), JavaRelation.CALL);
  }

  @Override // ASM-4
  public void visitMethodInsn(
      int opcode, String owner, String name, String desc) {
    builder.newDep(thisElement, new MethodElement(desc, name,
      TypeNameUtil.fromInternalName(owner)), JavaRelation.CALL);
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    builder.newDep(
        thisElement, TypeNameUtil.fromInternalName(type), JavaRelation.TYPE);
  }

  @Override
  public void visitTryCatchBlock(
      Label start, Label end, Label handler, String type) {
    
    // No type indicates a finally block, and adds no dependency
    if (null == type) {
      return;
    }
    builder.newDep(thisElement, TypeNameUtil.fromInternalName(type),
        JavaRelation.ERROR_HANDLING);
  }
}
