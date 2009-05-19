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

package com.google.devtools.depan.java.bytecode.impl;

import com.google.devtools.depan.filesystem.elements.DirectoryElement;
import com.google.devtools.depan.filesystem.elements.FileElement;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.builder.DependenciesListener;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;

/**
 * Implements a visitor of the ASM package, to find the dependencies in a class
 * file and build the dependency tree. A single {@link ClassDepLister} is used
 * for each file found in the explored directory or jar file.
 *
 * To build the dependencies tree, it calls the methods of a
 * {@link DependenciesListener}.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class ClassDepLister implements ClassVisitor {

  /**
   * {@link DependenciesListener} called when a dependency is found.
   */
  private DependenciesListener dl;

  /**
   * class currently read class. (typically the class A when the file A.java is
   * read.
   */
  private TypeElement mainClass = null;

  /**
   * The class' package currently read.
   */
//  private PackageElement currentPackage = null;
  // FIXME(ycoppel): packages are not correctly handled right now.

  /**
   * Pointer to the directory containing the file currently read.
   */
  private DirectoryElement path;

  /**
   * constructor for new {@link ClassDepLister}.
   *
   * @param dl {@link DependenciesListener} implementing callbacks.
   * @param path path to the directory containing the explored file.
   */
  public ClassDepLister(DependenciesListener dl, DirectoryElement path) {
    this.dl = dl;
    this.path = path;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visit(int, int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String[])
   */
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    this.mainClass = TypeNameUtil.fromInternalName(name);
    dl.newDep(TypeNameUtil.fromInternalName(superName), mainClass,
        JavaRelation.EXTENDS);
    for (String s : interfaces) {
      InterfaceElement element = TypeNameUtil.fromInterfaceName(s);
      dl.newDep(element, mainClass, JavaRelation.IMPLEMENTS);
    }
    checkAnonymousType(name);
  }

  /**
   * Check if the given internal name is an anonymous class. If so, generate
   * the appropriate dependencies.
   *
   * @param name
   */
  private void checkAnonymousType(String name) {
    // anonymous classes names contains a $ followed by a digit
    if (name.contains("$")) {
      String superClass = name.substring(0, name.lastIndexOf('$'));

      // recursively check: maybe name is not an anonymous class, but is
      // an innerclass contained into an anonymous class.
      checkAnonymousType(superClass);

      // A digit must follow the $ in the name.
      if (Character.isDigit(name.charAt(name.lastIndexOf('$')+1))) {
        TypeElement superType = TypeNameUtil.fromInternalName(superClass);
        dl.newDep(superType, mainClass, JavaRelation.ANONYMOUS_TYPE);
      }

    }
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
    // TODO(ycoppel): Auto-generated method stub
    // System.err.println(attr.type);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visitEnd()
   */
  public void visitEnd() {
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visitField(int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.Object)
   */
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    TypeElement type = TypeNameUtil.fromDescriptor(desc);
    FieldElement field = new FieldElement(name, type, mainClass);

    // simple className
    dl.newDep(mainClass, type, JavaRelation.TYPE);

    // field
    JavaRelation r = null;
    if ((Opcodes.ACC_STATIC & access) != 0) {
      r = JavaRelation.STATIC_FIELD;
    } else {
      r = JavaRelation.MEMBER_FIELD;
    }
    dl.newDep(mainClass, field, r);

    // generic types in signature
    // TODO(ycoppel): how to get types in generics ? (signature)

    return new FieldDepLister();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visitInnerClass(java.lang.String,
   *      java.lang.String, java.lang.String, int)
   */
  public void visitInnerClass(String name, String outerName, String innerName,
      int access) {
    if ((null == outerName) || (null == innerName)) {
      //System.out.println("visitInnerClass()" + name + " - "
      //    + outerName + " - " + innerName + " - " + access + " @ "
      //    + this.mainClass);
      // FIXME(ycoppel): probably an enum. What to do ?
      return;
    }
    TypeElement inner = TypeNameUtil.fromInternalName(name);
    if (inner.equals(this.mainClass)) {
      // the visitInnerClass callback is called twice: once when visiting the
      // outer class (A in A$B), and once when visiting the A$B class. we
      // shortcut the second case so we don't add the dependency twice.
      return;
    }
    TypeElement parent = TypeNameUtil.fromInternalName(outerName);
    dl.newDep(parent, inner, JavaRelation.INNER_TYPE);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visitMethod(int, java.lang.String,
   *      java.lang.String, java.lang.String, java.lang.String[])
   */
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {

    // the method itself
    MethodElement m = new MethodElement(desc, name, this.mainClass);

    JavaRelation r = null;
    if ((Opcodes.ACC_STATIC & access) != 0) {
      r = JavaRelation.STATIC_METHOD;
    } else {
      r = JavaRelation.MEMBER_METHOD;
    }
    dl.newDep(this.mainClass, m, r);

    // arguments dependencies
    for (Type t : Type.getArgumentTypes(desc)) {
      dl.newDep(m, TypeNameUtil.fromDescriptor(t.getDescriptor()),
          JavaRelation.TYPE);
    }

    // return-type dependency
    TypeElement type = TypeNameUtil.fromDescriptor(
        Type.getReturnType(desc).getDescriptor());
    dl.newDep(m, type, JavaRelation.READ);

    return new MethodDepLister(dl, m);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.objectweb.asm.ClassVisitor#visitOuterClass(java.lang.String,
   *      java.lang.String, java.lang.String)
   */
  public void visitOuterClass(String owner, String name, String desc) {
    // nothing to do. We use the visitInnerClass callback instead.
  }

  /**
   * Create a source element using the full path-name of the source file.
   * If the mainClass is not a top-level class, assume it is an inner
   * class and its immediate container is not a source file.
   */
  public void visitSource(String source, String debug) {
    if (this.mainClass.getFullyQualifiedName().contains("$")) {
      // this is an inner class. We use the visitInnerClass() callback instead.
    } else {
      // this is a main class.
      File sourceFile = new File(path.getId(), source);
      FileElement se = new FileElement(sourceFile.getPath());
      dl.newDep(path, se, JavaRelation.FILE);
      dl.newDep(se, mainClass, JavaRelation.CLASS);
    }
  }

}
