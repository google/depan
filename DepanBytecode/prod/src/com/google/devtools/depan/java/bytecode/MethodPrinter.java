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

package com.google.devtools.depan.java.bytecode;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 * Visit a method and print each class used, which implies a dependency
 */
public class MethodPrinter implements MethodVisitor {

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitAnnotation(java.lang.String,
   *      boolean)
   */
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitAnnotationDefault()
   */
  public AnnotationVisitor visitAnnotationDefault() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor
   *      #visitAttribute(org.objectweb.asm.Attribute)
   */
  public void visitAttribute(Attribute attr) {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitCode()
   */
  public void visitCode() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitEnd()
   */
  public void visitEnd() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitFieldInsn(int, java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void visitFieldInsn(int opcode, String owner, String name,
      String desc) {
    // TODO Auto-generated method stub
//    System.out.println("    Dependency CODE.field " + desc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitIincInsn(int, int)
   */
  public void visitIincInsn(int var, int increment) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitInsn(int)
   */
  public void visitInsn(int opcode) {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitIntInsn(int, int)
   */
  public void visitIntInsn(int opcode, int operand) {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitJumpInsn(int,
   *      org.objectweb.asm.Label)
   */
  public void visitJumpInsn(int opcode, Label label) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitLabel(org.objectweb.asm.Label)
   */
  public void visitLabel(Label label) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitLdcInsn(java.lang.Object)
   */
  public void visitLdcInsn(Object cst) {
    // TODO Auto-generated method stub
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitLineNumber(int,
   *      org.objectweb.asm.Label)
   */
  public void visitLineNumber(int line, Label start) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitLocalVariable(java.lang.String,
   *      java.lang.String, java.lang.String, org.objectweb.asm.Label,
   *      org.objectweb.asm.Label, int)
   */
  public void visitLocalVariable(String name, String desc, String signature,
      Label start, Label end, int index) {
//    System.out.println("    Dependency METHOD.localvariable " + desc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor
   *      #visitLookupSwitchInsn(org.objectweb.asm.Label, int[],
   *      org.objectweb.asm.Label[])
   */
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitMaxs(int, int)
   */
  public void visitMaxs(int maxStack, int maxLocals) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitMethodInsn(int, java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void visitMethodInsn(int opcode, String owner, String name,
      String desc) {
//    System.out.println("    Dependency METHOD.instruction " + desc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor
   *      #visitMultiANewArrayInsn(java.lang.String, int)
   */
  public void visitMultiANewArrayInsn(String desc, int dims) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitParameterAnnotation(int,
   *      java.lang.String, boolean)
   */
  public AnnotationVisitor visitParameterAnnotation(int parameter,
      String desc, boolean visible) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitTableSwitchInsn(int, int,
   *      org.objectweb.asm.Label, org.objectweb.asm.Label[])
   */
  public void visitTableSwitchInsn(int min, int max, Label dflt,
      Label[] labels) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor
   *      #visitTryCatchBlock(org.objectweb.asm.Label, org.objectweb.asm.Label,
   *      org.objectweb.asm.Label, java.lang.String)
   */
  public void visitTryCatchBlock(Label start, Label end, Label handler,
      String type) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitTypeInsn(int, java.lang.String)
   */
  public void visitTypeInsn(int opcode, String type) {
//    System.out.println(type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitVarInsn(int, int)
   */
  public void visitVarInsn(int opcode, int var) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.MethodVisitor#visitFrame(int, int,
   *      java.lang.Object[], int, java.lang.Object[])
   */
  public void visitFrame(int type, int nLocal, Object[] local, int nStack,
      Object[] stack) {
  }

}
