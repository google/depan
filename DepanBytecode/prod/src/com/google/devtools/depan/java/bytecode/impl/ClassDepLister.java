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

import com.google.devtools.depan.filesystem.graph.FileElement;
import com.google.devtools.depan.java.bytecode.eclipse.AsmFactory;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaRelation;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.builder.chain.DependenciesListener;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

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
 */
public class ClassDepLister extends ClassVisitor {

  private final AsmFactory asmFactory;

  /**
   * {@link DependenciesListener} called when a dependency is found.
   */
  private final DependenciesListener builder;

  /**
   * File element for source code.
   */
  private final FileElement fileNode;

  /**
   * class currently read class. (typically the class A when the file A.java is
   * read.
   */
  private TypeElement mainClass = null;

  /**
   * constructor for new {@link ClassDepLister}.
   *
   * @param builder {@link DependenciesListener} implementing callbacks
   * @param fileNode node for the .class file containing this class
   */
  public ClassDepLister(
      AsmFactory asmFactory, DependenciesListener builder, FileElement fileNode) {
    super(asmFactory.getApiLevel());
    this.asmFactory = asmFactory;
    this.builder = builder;
    this.fileNode = fileNode;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    mainClass = TypeNameUtil.fromInternalName(name);

    PackageElement packageNode = installPackageForTypeName(name);
    builder.newDep(packageNode, mainClass, JavaRelation.CLASS);

    builder.newDep(TypeNameUtil.fromInternalName(superName), mainClass,
        JavaRelation.EXTENDS);
    for (String s : interfaces) {
      InterfaceElement element = TypeNameUtil.fromInterfaceName(s);
      builder.newDep(element, mainClass, JavaRelation.IMPLEMENTS);
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
        builder.newDep(superType, mainClass, JavaRelation.ANONYMOUS_TYPE);
      }
    }
  }

  @Override
  public FieldVisitor visitField(
      int access, String name, String desc, String signature, Object value) {
    TypeElement type = TypeNameUtil.fromDescriptor(desc);
    FieldElement field = new FieldElement(name, type, mainClass);

    // simple className
    builder.newDep(mainClass, type, JavaRelation.TYPE);

    // field
    JavaRelation r = null;
    if ((Opcodes.ACC_STATIC & access) != 0) {
      r = JavaRelation.STATIC_FIELD;
    } else {
      r = JavaRelation.MEMBER_FIELD;
    }
    builder.newDep(mainClass, field, r);

    // generic types in signature
    // TODO(ycoppel): how to get types in generics ? (signature)

    return asmFactory.getGenericFieldVisitor();
  }

  @Override
  public void visitInnerClass(
      String name, String outerName, String innerName, int access) {
    if ((null == outerName) || (null == innerName)) {
      //System.out.println("visitInnerClass()" + name + " - "
      //    + outerName + " - " + innerName + " - " + access + " @ "
      //    + mainClass);
      // FIXME(ycoppel): probably an enum. What to do ?
      return;
    }
    TypeElement inner = TypeNameUtil.fromInternalName(name);
    if (inner.equals(mainClass)) {
      // the visitInnerClass callback is called twice: once when visiting the
      // outer class (A in A$B), and once when visiting the A$B class. we
      // shortcut the second case so we don't add the dependency twice.
      return;
    }
    TypeElement parent = TypeNameUtil.fromInternalName(outerName);
    builder.newDep(parent, inner, JavaRelation.INNER_TYPE);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, mainClass, desc, visible);
    return null;
  }

  @Override
  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String desc, boolean visible) {
    TypeNameUtil.buildAnnotationDep(builder, mainClass, desc, visible);
    return null;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {

    // the method itself
    MethodElement m = new MethodElement(desc, name, mainClass);

    JavaRelation r = null;
    if ((Opcodes.ACC_STATIC & access) != 0) {
      r = JavaRelation.STATIC_METHOD;
    } else {
      r = JavaRelation.MEMBER_METHOD;
    }
    builder.newDep(mainClass, m, r);

    // arguments dependencies
    for (Type t : Type.getArgumentTypes(desc)) {
      builder.newDep(m, TypeNameUtil.fromDescriptor(t.getDescriptor()),
          JavaRelation.TYPE);
    }

    // return-type dependency
    TypeElement type = TypeNameUtil.fromDescriptor(
        Type.getReturnType(desc).getDescriptor());
    builder.newDep(m, type, JavaRelation.READ);

    return asmFactory.buildMethodVisitor(builder, m);
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    // nothing to do. We use the visitInnerClass callback instead.
  }

  /**
   * Create a source element using the full path-name of the source file.
   * If the mainClass is not a top-level class, assume it is an inner
   * class and its immediate container is not a source file.
   */
  @Override
  public void visitSource(String source, String debug) {
    if (mainClass.getFullyQualifiedName().contains("$")) {
      // this is an inner class. We use the visitInnerClass() callback instead.
      return;
    }

    // Link the main class to it's containing file
    builder.newDep(fileNode, mainClass, JavaRelation.CLASSFILE);
  }

  /**
   * Install a package hierarchy and a matching directory hierarchy for
   * the full path name of the type.
   * 
   * @param typePath full path name of a type
   * @return PackageElement that contains the type
   */
  private PackageElement installPackageForTypeName(String typePath) {
    // TODO(leeca): Add short-circuit early exit if package is already defined.
    // This would avoid a fair bit of unnecessary object creation.
    File packageFile = new File(typePath).getParentFile();
    if (null == packageFile) {
      return new PackageElement("<unnamed>");
    }
    File treeFile = createTreeFile();
    PackageTreeBuilder packageBuilder = new PackageTreeBuilder(builder);

    return packageBuilder.installPackageTree(packageFile, treeFile);
  }

  /**
   * Define the tree for the file node, even if it doesn't have a directory.
   * This can happen for class files at the top of the analysis tree, such as
   * classes in the unnamed package at the top of a Jar file.
   *
   * @return valid directory tree reference
   */
  private File createTreeFile() {
    File result = new File(fileNode.getPath()).getParentFile();
    if (null == result) {
      return new File("");
    }
    return result;
  }
}
