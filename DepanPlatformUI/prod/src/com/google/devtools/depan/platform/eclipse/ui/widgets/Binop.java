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

package com.google.devtools.depan.platform.eclipse.ui.widgets;

import com.google.devtools.depan.platform.BinaryOperators;
import com.google.devtools.depan.platform.PlatformResources;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.WorkbenchPart;

import java.util.Map;


/**
 * A class to edit binary operations between diferent objects T. This class
 * extends Composite, and creates a TreeViewer and buttons usefull to edit
 * a tree of binary operations.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 * @param <T> types you want to combine with binary operations.
 */
public class Binop<T extends BinaryOperators<T>>
    extends Composite implements IAdapterFactory {

  private T obj1 = null;
  private T obj2 = null;

  private Text expression = null;
  private Root<T> rootTree = new Root<T>();
  private Tree<T> selectedTree = null;

  private TreeViewer treeViewer;
  private TCreator<T> creator;

  /**
   * Possible operators in the expression tree.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   */
  public enum Operators {
    AND("&"),
    OR("|"),
    XOR("^"),
    NOT("!");

    private String repr;

    private Operators(String repr) {
      this.repr = repr;
    }

    @Override
    public String toString() {
      return repr;
    }
  }

  private static Map<Operators, ImageDescriptor> OP_DESC = buildOpDesc();

  static Map<Operators, ImageDescriptor> buildOpDesc() {
    Map<Operators, ImageDescriptor> result = Maps.newHashMap();
    result.put(Operators.AND, PlatformResources.IMAGE_DESC_AND);
    result.put(Operators.OR, PlatformResources.IMAGE_DESC_OR);
    result.put(Operators.XOR, PlatformResources.IMAGE_DESC_XOR);
    result.put(Operators.NOT, PlatformResources.IMAGE_DESC_NOT);
    return result;
  }

  public Binop(Composite parent, int style, WorkbenchPart part,
      TCreator<T> creator) {
    super(parent, style);
    this.creator = creator;

    int numColumns = 2;

    GridLayout g = new GridLayout();
    g.numColumns = numColumns;
    g.makeColumnsEqualWidth = false;
    this.setLayout(g);

    // {{{ // buttons
    Composite buttons = new Composite(this, SWT.NONE);
    GridLayout buttonsLayout = new GridLayout();
    buttons.setLayout(buttonsLayout);
    buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

    Button and = new Button(buttons, SWT.PUSH);
    and.setText("AND");
    and.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    and.setImage(PlatformResources.IMAGE_AND);
    and.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        newTree(Operators.AND);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        newTree(Operators.AND);
      }
    });

    Button or = new Button(buttons, SWT.PUSH);
    or.setText("OR");
    or.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    or.setImage(PlatformResources.IMAGE_OR);
    or.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        newTree(Operators.OR);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        newTree(Operators.OR);
      }
    });

    Button xor = new Button(buttons, SWT.PUSH);
    xor.setText("XOR");
    xor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    xor.setImage(PlatformResources.IMAGE_XOR);
    xor.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        newTree(Operators.XOR);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        newTree(Operators.XOR);
      }
    });

    Button not = new Button(buttons, SWT.PUSH);
    not.setText("NOT");
    not.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    not.setImage(PlatformResources.IMAGE_NOT);
    not.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        newTree(Operators.NOT);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        newTree(Operators.NOT);
      }
    });

    Button delete = new Button(buttons, SWT.PUSH);
    delete.setText("Delete tree");
    delete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    delete.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
      }

      @Override
      @SuppressWarnings({ "rawtypes", "unchecked" })
      public void widgetSelected(SelectionEvent e) {
        Object o = ((ITreeSelection) treeViewer.getSelection())
          .getFirstElement();
        if (null != o && o instanceof Tree) {
          deleteTree((Tree) o);
        }
      }
    });
    // }}}

    // {{{
    Composite editor = new Composite(this, SWT.NONE);
    GridLayout g2 = new GridLayout();
    editor.setLayout(g2);
    editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));
    expression = new Text(editor, SWT.BORDER | SWT.MULTI);
    expression.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

    treeViewer = new TreeViewer(editor, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    treeViewer.getTree().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    part.getSite().setSelectionProvider(treeViewer);
    Platform.getAdapterManager().registerAdapters(this, Tree.class);
    treeViewer.setLabelProvider(new WorkbenchLabelProvider());
    treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
    treeViewer.setInput(rootTree);
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

      @Override
      @SuppressWarnings("unchecked")
      public void selectionChanged(SelectionChangedEvent event) {
        Object o = ((ITreeSelection) treeViewer.getSelection())
            .getFirstElement();
        if (null != o && o instanceof Tree) {
          selectedTree = (Tree<T>) o;
        }
      }
    });
    // }}}

    Button createView = new Button(this, SWT.PUSH);
    createView.setText("Create View");
    createView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,
        numColumns, 1));
    createView.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        apply();
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        apply();
      }
    });

  }

  private void updateTree() {
    expression.setText(rootTree.toString());
    treeViewer.refresh();
    treeViewer.expandAll();
  }

  protected void newTree(Operators op) {
    if (null != selectedTree && null != obj1 && null == obj2) {
      newTree(op, selectedTree, obj1);
    } else if (null != obj1 && null != obj2 && null == selectedTree) {
      rootTree.setFirstTree(new BinTree<T>(
          new Leaf<T>(obj1), new Leaf<T>(obj2), op));
      selectedTree = rootTree.firstTree;
    }
    updateTree();
  }

  private void newTree(Operators op, Tree<T> with, T obj) {
    Tree<T> parent = with.parent;
    BinTree<T> insert = new BinTree<T>(with, new Leaf<T>(obj), op);

    if (parent instanceof Root) {
      // we are at the root.
      ((Root<T>) parent).setFirstTree(insert);
    } else if (parent instanceof BinTree) {
      BinTree<T> binParent = (BinTree<T>) parent;
      binParent.replaceBranch(with, insert);
    }
    selectedTree = insert;
  }

  private void deleteTree(Tree<T> toDelete) {
    System.out.println("delete " + toDelete);
    Tree<T> parent = toDelete.parent;
    if (null == parent) {
      return;
    }
    if (parent instanceof BinTree) {
      System.out.println("    parent BinTree");
      Tree<T> superParent = parent.parent;
      if (superParent instanceof BinTree) {
        System.out.println("    parent.parent BinTree");
        ((BinTree<T>) superParent)
            .replaceBranch(parent, ((BinTree<T>) parent).getOther(toDelete));
      } else if (superParent instanceof Root) {
        System.out.println("    parent.parent Root");
        ((Root<T>) superParent)
            .setFirstTree(((BinTree<T>) parent).getOther(toDelete));
      }
    } else if (parent instanceof Root) {
      System.out.println("    parent�Root");
      ((Root<T>) parent).setFirstTree(null);
    }
    updateTree();
    selectedTree = null;
  }

  protected void apply() {
    T newT = build(rootTree);
    if (null != newT) {
      creator.create(newT);
    }
  }

  private T build(Tree<T> tree) {
    if (tree instanceof BinTree) {
      return build((BinTree<T>) tree);
    } else if (tree instanceof Leaf) {
      return build((Leaf<T>) tree);
    } else if (tree instanceof Root) {
      return build((Root<T>) tree);
    }
    return null;
  }

  private T build(BinTree<T> tree) {
    T t1 = build(tree.o1);
    T t2 = build(tree.o2);
    if (null == t1 || null == t2) {
      return null;
    }
    switch(tree.op) {
    case AND:
      return t1.and(t2);
    case OR:
      return t1.or(t2);
    case XOR:
      return t1.xor(t2);
    case NOT:
      return t1.not(t2);
    default:
      break;
    }
    return null;
  }

  private T build(Leaf<T> tree) {
    return tree.leaf;
  }

  private T build(Root<T> tree) {
    return build(tree.firstTree);
  }

  public void setFirst(T o1) {
    this.obj1 = o1;
    this.obj2 = null;
  }

  public void setBoth(T o1, T o2) {
    this.obj1 = o1;
    this.obj2 = o2;
  }


  /**
   * a super class for a Tree.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <T>
   */
  @SuppressWarnings("hiding")
  public abstract class Tree<T extends BinaryOperators<T>>
      extends PlatformObject {
    Tree<T> parent;
    public void setParent(Tree<T> parent) {
      this.parent = parent;
    }
  }

  /**
   * A class for representing a tree of binary operations.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <T>
   */
  @SuppressWarnings("hiding")
  public class BinTree<T extends BinaryOperators<T>> extends Tree<T> {
    Tree<T> o1;
    Tree<T> o2;
    Operators op;

    public BinTree(Tree<T> operand1, Tree<T> operand2, Operators operator) {
      this.o1 = operand1;
      this.o2 = operand2;
      this.op = operator;
      o1.setParent(this);
      o2.setParent(this);
    }

    public Tree<T> getOther(Tree<T> notThisOne) {
      if (o1 == notThisOne) {
        return o2;
      } else {
        return o1;
      }
    }

    public void replaceBranch(Tree<T> fromThat, Tree<T> toThat) {
      if (o1 == fromThat) {
        o1 = toThat;
      } else if (o2 == fromThat) {
        o2 = toThat;
      }
      toThat.setParent(this);
    }

    @Override
    public String toString() {
      return "(" + o1 + " " + op + " " + o2 + ")";
    }
  }

  /**
   * Class for a leaf tree.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <T>
   */
  @SuppressWarnings("hiding")
  public class Leaf<T extends BinaryOperators<T>> extends Tree<T> {
    T leaf;

    public Leaf(T leaf) {
      this.leaf = leaf;
    }

    @Override
    public String toString() {
      return leaf.toString();
    }
  }

  /**
   * A Root Element for the tree. Usefull to display the root tree corectly in
   * the TreeViewer.
   *
   * @author ycoppel@google.com (Yohann Coppel)
   *
   * @param <T>
   */
  @SuppressWarnings("hiding")
  public class Root<T extends BinaryOperators<T>> extends Tree<T> {
    private Tree<T> firstTree;

    public void setFirstTree(Tree<T> firstTree) {
      this.firstTree = firstTree;
      if (this.firstTree != null) {
        this.firstTree.setParent(this);
      }
    }

    public Tree<T> getFirstTree() {
      return firstTree;
    }

    @Override
    public String toString() {
      return (null == firstTree ? "" : "" + firstTree);
    }
  }

  private IWorkbenchAdapter treeAdapter = new IWorkbenchAdapter() {

    @Override
    @SuppressWarnings("rawtypes")
    public Object[] getChildren(Object o) {
      if (o instanceof Root && null != ((Root) o).getFirstTree()) {
        return new Object[] {((Root) o).getFirstTree()};
      } else if (o instanceof BinTree) {
        return new Object[] {((BinTree) o).o1, ((BinTree) o).o2};
      }
      return new Object[] {};
    }

    @Override
    @SuppressWarnings("rawtypes")
    public ImageDescriptor getImageDescriptor(Object object) {
      if (object instanceof BinTree) {
        return OP_DESC.get(((BinTree) object).op);
      }

      return PlatformResources.IMAGE_DESC_DEFAULT;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public String getLabel(Object o) {
      if (o instanceof BinTree) {
        return ((BinTree) o).op.toString() + " - " + o.toString();
      }
      return o.toString();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getParent(Object o) {
      return ((Tree) o).parent;
    }
  };

  // suppress the warning for the Class, which should be parameterized.
  // We can't here: getAdapter is not declared with a parameterized Class.
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Object adaptableObject, Class adapterType) {
    if (adapterType != IWorkbenchAdapter.class) {
      return null;
    }
    if (adaptableObject instanceof Tree) {
      return treeAdapter;
    }
    return null;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Class[] getAdapterList() {
    return new Class[] {IWorkbenchAdapter.class};
  }
}
