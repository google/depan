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
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author ycoppel@google.com (Yohann Coppel)
 * 
 */
public class ClassPrinter implements ClassVisitor {

  /**
   * A buffer that can be used to create strings.
   */
  protected StringBuffer buf;

  public ClassPrinter() {
    buf = new StringBuffer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String[])
   */
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    appendln("Class " + name);
    append("    Dependency CLASS.extends ");
    appendln(superName);
    for (String s : interfaces) {
      append("    Dependency INTERFACES.implements ");
      appendln(s);
    }
    // TODO(ycoppel): print interfaces
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitAnnotation(java.lang.String,
   *      boolean)
   */
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor
   *      #visitAttribute(org.objectweb.asm.Attribute)
   */
  public void visitAttribute(Attribute attr) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitEnd()
   */
  public void visitEnd() {
    printBuf();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitField(int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.Object)
   */
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    append("    Dependency FIELD.type ");
    appendln(desc);
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitInnerClass(java.lang.String,
   *      java.lang.String, java.lang.String, int)
   */
  public void visitInnerClass(String name, String outerName,
      String innerName, int access) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String[])
   */
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {
    append("    Dependency METHOD_DECL.types ");
    appendln(desc);

    if (exceptions != null && exceptions.length > 0) {
      for (String ex : exceptions) {
        append("    Dependency THROW.types ");
        appendln(ex);
      }
    }
    printBuf();
    return new MethodPrinter();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitOuterClass(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void visitOuterClass(String owner, String name, String desc) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.objectweb.asm.ClassVisitor#visitSource(java.lang.String,
   *      java.lang.String)
   */
  public void visitSource(String source, String debug) {
  }

  /* ==== helpers ==== */
  private void printBuf() {
    System.out.println(buf.toString());
    buf = null;
    buf = new StringBuffer();
  }

  private void append(String s) {
    buf.append(s);
  }

  private void bufln() {
    buf.append("\n");
  }

  private void appendln(String s) {
    buf.append(s);
    bufln();
  }
}
