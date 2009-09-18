/*
 * Copyright 2008 Google Inc.
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

package com.google.devtools.depan.eclipse.views.tools;

import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.editors.ViewEditor;
import com.google.devtools.depan.eclipse.persist.XmlPersistentPathExpression;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.eclipse.utils.EditColTableDef;
import com.google.devtools.depan.eclipse.utils.RelationshipPicker;
import com.google.devtools.depan.eclipse.utils.Resources;
import com.google.devtools.depan.eclipse.utils.TableContentProvider;
import com.google.devtools.depan.filters.PathExpression;
import com.google.devtools.depan.filters.PathMatcher;
import com.google.devtools.depan.filters.PathMatcherTerm;
import com.google.devtools.depan.graph.basic.MultipleDirectedRelationFinder;
import com.google.devtools.depan.model.RelationshipSetAdapter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.SaveAsDialog;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * A <code>Composite</code> used to create filters for Path Expression analysis.
 *
 * @author tugrul@google.com (Tugrul Ince)
 */
public class PathExpressionEditorTool
    implements SelectionEditorTool.NodeSelectorPart {

  /**
   * The composite UI element that controls the screen space.
   */
  private Composite control;

  /**
   * The table that lists all selected <code>PathMatcherTerm</code>s.
   */
  private TableViewer pathMatchers;

  /**
   * The content of pathMatchers table.
   */
  private TableContentProvider<PathMatcherTerm>
      pathMatchersContent;

  /**
   * The <code>RelationshipPicker</code> object where users can select relations
   * to append to the list.
   */
  private RelationshipPicker relationshipPicker;

  /**
   * The model which applies filters to selected nodes to reach relevant nodes.
   */
  private PathMatcher pathExpression;

  /**
   * The checkbox that shows whether the selected set of relations has to be
   * applied recursively.
   */
  private Button recursive;

  /**
   * The checkbox that shows whether the selected set of relations has to
   * include its input in its output.
   */
  private Button cumulative;

  /*
   * String Constants used for initializing buttons and as labels
   */
  private static final String CUMULATIVE_LABEL = "Cumulative";
  private static final String NON_CUMULATIVE_LABEL = "Non-cumulative";
  private static final String RECURSIVE_LABEL = "Recursive";
  private static final String NON_RECURSIVE_LABEL = "Non-recursive";

  private static final String RECURSIVE_BUTTON_LABEL = RECURSIVE_LABEL;
  private static final String CUMULATIVE_BUTTON_LABEL = CUMULATIVE_LABEL;
  private static final String MOVE_UP_BUTTON_LABEL = "Move Up";
  private static final String MOVE_DOWN_BUTTON_LABEL = "Move Down";
  private static final String APPEND_BUTTON_LABEL = "Append";
  private static final String REMOVE_BUTTON_LABEL = "Remove";
  private static final String SAVE_BUTTON_LABEL = "Save";
  private static final String LOAD_BUTTON_LABEL = "Load";

  /**
   * Set of Strings used to create the structure of the list of
   * <code>PathMatcherTerm</code> objects.
   */
  private static final String COL_NAME = "Name";
  private static final String COL_RECURSIVE = RECURSIVE_LABEL;
  private static final String COL_CUMULATIVE = CUMULATIVE_LABEL;

  private static final String FILE_PREFIX = "file://";

  /**
   * Array of table definitions used to create the structure of the
   * <code>PathMatcherTerm</code> list
   */
  private final EditColTableDef[] TABLE_DEF = new EditColTableDef[] {
    new EditColTableDef(COL_NAME, false, COL_NAME, 150),
    new EditColTableDef(COL_RECURSIVE, true, COL_RECURSIVE, 120),
    new EditColTableDef(COL_CUMULATIVE, true, COL_CUMULATIVE, 120)
  };

  public PathExpressionEditorTool() {
  }

  // TODO(leeca): rename PathMatcher class to NodeSelector
  @Override
  public PathMatcher getNodeSelector() {
    PathExpression result = new PathExpression();
    for (int i = 0; i < pathMatchers.getTable().getItemCount(); i++) {
      PathMatcherTerm matchers =
          (PathMatcherTerm) pathMatchers.getElementAt(i);
      result.addPathMatcher(matchers);
    }
    return result;
  }

  @Override
  public void updateControl(ViewEditor viewEditor) {
  }

  /**
   * Constructs a Path Expression Editor that is itself a
   * <code>Composite</code>.
   *
   * @param parent Parent <code>Composite</code> object which holds this tool
   * @param style An integer value that determines the style of this Composite
   * through SWT constants
   */
  @Override
  public Composite createControl(
      Composite parent, int style, ViewEditor viewEditor) {
    control = new Composite(parent, style);
    control.setLayout(new GridLayout(4, false));

    // Column 1: Matcher picking area
    relationshipPicker = new RelationshipPicker();
    Control relationsPanel = relationshipPicker.getControl(control);
    relationsPanel.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, false, true));

    // Column 2: Matcher/path expression actions
    Composite column2 = new Composite(control, SWT.NONE);
    column2.setLayout(new GridLayout(1, false));
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
    gridData.verticalAlignment = GridData.CENTER;
    gridData.horizontalAlignment = GridData.CENTER;
    column2.setLayoutData(gridData);

    Button append = createPushButton(column2, APPEND_BUTTON_LABEL);
    append.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        appendSelection();
      }
    });

    recursive = createButton(column2, RECURSIVE_BUTTON_LABEL, SWT.CHECK);
    cumulative = createButton(column2, CUMULATIVE_BUTTON_LABEL, SWT.CHECK);

    // Column 3: Current path expression display
    setupPathMatchersList(control);

    // Column 4: Path expression actions
    Composite column4 = new Composite(control, SWT.NONE);
    column4.setLayout(new GridLayout(1, false));
    gridData = new GridData(SWT.FILL, SWT.FILL, false, true);
    gridData.verticalAlignment = GridData.CENTER;
    gridData.horizontalAlignment = GridData.CENTER;
    column4.setLayoutData(gridData);

    Button moveUp = createPushButton(column4, MOVE_UP_BUTTON_LABEL);
    moveUp.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveSelection(SWT.UP);
      }
    });

    Button moveDown = createPushButton(column4, MOVE_DOWN_BUTTON_LABEL);
    moveDown.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        moveSelection(SWT.DOWN);
      }
    });

    Button removeButton = createPushButton(column4, REMOVE_BUTTON_LABEL);
    removeButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        removeFromMatchers(getSelection());
      }
    });

    // create an empty composite to make some space between save/load part and
    // list controls
    Composite dummy = new Composite(column4, SWT.NONE);

    Composite saveLoadPanel = new Composite(column4, SWT.BORDER);
    saveLoadPanel.setLayout(new GridLayout(1, false));
    gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
    saveLoadPanel.setLayoutData(gridData);

    Button saveButton = createPushButton(saveLoadPanel, SAVE_BUTTON_LABEL);
    saveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        savePathExpression();
      }
    });

    Button loadButton = createPushButton(saveLoadPanel, LOAD_BUTTON_LABEL);
    loadButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        loadPathExpression();
      }
    });

    return control;
  }

  /**
   * Creates a new push button on the specified panel with the given text.
   *
   * @param owner The Composite this button belongs to.
   * @param label The text that appears on button.
   * @return A new push button on the given <code>Composite</code> with the
   * given text.
   */
  private Button createPushButton(Composite owner, String label) {
    return createButton(owner, label, SWT.PUSH);
  }

  /**
   * Creates a new button of any type on the specified panel with the given
   * text.
   *
   * @param owner The Composite this button belongs to.
   * @param label The text that appears on button.
   * @param style The type of button.
   * @return A new button on the given <code>Composite</code> with the given
   * text.
   */
  private Button createButton(Composite owner, String label, int style) {
    Button newButton = new Button(owner, style);
    newButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    newButton.setText(label);
    return newButton;
  }

  /**
   * Returns the selected row in list of <code>PathMatcherTerm</code>s.
   *
   * @return Selected <code>PathMatcherTerm</code> object in the table.
   */
  private PathMatcherTerm getSelection() {
    IStructuredSelection selection =
        (IStructuredSelection) pathMatchers.getSelection();
    return (PathMatcherTerm) selection.getFirstElement();
  }

  /**
   * Returns the index of the given element in list of
   * <code>PathMatcherTerm</code>s.
   *
   * @param element Element whose index is requested.
   * @return The index this element occupies. Returns -1 if the element is not
   * in the table.
   */
  private int getElementIndex(PathMatcherTerm element) {
    int size = pathMatchers.getTable().getItemCount();
    for (int i = 0; i < size; i++) {
      if (element == pathMatchers.getElementAt(i)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Moves the selected object up and down in list of
   * <code>PathMatcherTerm</code>s.
   *
   * @param direction SWT.UP or SWT.DOWN to represent up and down respectively.
   * @return True iff move is successful.
   */
  private boolean moveSelection(int direction) {
    PathMatcherTerm term = getSelection();

    int initialPosition = getElementIndex(term);
    if (initialPosition < 0) {
      return false;
    }

    if ((direction == SWT.UP) && (initialPosition > 0)) {
      moveWithinMatchers(term, initialPosition - 1);
      return true;
    }

    if ((direction == SWT.DOWN)
        && (initialPosition < (pathMatchers.getTable().getItemCount() - 1))) {
      moveWithinMatchers(term, initialPosition + 1);
      return true;
    }

    // Not in a movable position
    return false;
  }

  /**
   * Moves the given <code>PathMatcherTerm</code> to the given index.
   *
   * @param matcher The <code>PathMatcherTerm</code> that needs moving.
   * @param index The new index of this <code>PathMatcherTerm</code> object.
   */
  private void moveWithinMatchers(PathMatcherTerm matcher, int index) {
    removeFromMatchers(matcher);
    pathMatchers.insert(matcher, index);
    pathMatchers.getTable().setSelection(index);
  }

  /**
   * Removes the given element from the list of <code>PathMatcherTerm</code>s.
   *
   * @param object The element to be removed from the table.
   */
  private void removeFromMatchers(PathMatcherTerm object) {
    int index = getElementIndex(object);
    pathMatchers.remove(object);
    if (index < pathMatchers.getTable().getItemCount()) {
      pathMatchers.getTable().setSelection(index);
    } else {
      pathMatchers.getTable().setSelection(index - 1);
    }
  }

  /**
   * Appends the selection specified by the <code>RelationshipPicker</code>
   * object to the table.
   */
  private void appendSelection() {
    String setName = relationshipPicker.getSelectedRelationshipSet().getName();
    MultipleDirectedRelationFinder finder =
        relationshipPicker.getRelationShips();
    RelationshipSetAdapter setAdapterFromPicker =
        new RelationshipSetAdapter(setName, finder,
            SourcePluginRegistry.getRelations());
    boolean isRecursive = recursive.getSelection();
    boolean isCumulative = cumulative.getSelection();
    PathMatcherTerm termToAppend =
        new PathMatcherTerm(setAdapterFromPicker, isRecursive, isCumulative);
    pathMatchers.add(termToAppend);
    Table matcherTable = pathMatchers.getTable();
    matcherTable.setSelection(matcherTable.getItemCount() - 1);
  }

  /**
   * Creates a <code>TableViewer</code> object that holds an ordered list of
   * <code>PathMatcherTerm</code>s.
   *
   * @param parent The <code>Composite</code> that contains this table.
   */
  private void setupPathMatchersList(Composite parent) {
    // Create a table viewer and its content provider
    pathMatchers = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL);
    pathMatchersContent =
        new TableContentProvider<PathMatcherTerm>();
    pathMatchersContent.initViewer(pathMatchers);

    // set up label and provider
    PathMatcherLabelProvider pathMatchersLabelProvider =
        new PathMatcherLabelProvider();
    pathMatchers.setLabelProvider(pathMatchersLabelProvider);
    pathMatchers.setCellModifier(pathMatchersLabelProvider);
    pathMatchersLabelProvider.addChangeListener(pathMatchers);

    // Set up layout properties
    Control pathMatchersControl = pathMatchers.getTable();
    pathMatchersControl.setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));
    pathMatchersControl.setToolTipText("List of Path Matcher Terms");

    // initialize the table
    Table pathMatchersTable = pathMatchers.getTable();
    pathMatchersTable.setHeaderVisible(true);
    EditColTableDef.setupTable(TABLE_DEF, pathMatchersTable);

    CellEditor[] cellEditors = new CellEditor[3];
    cellEditors[0] = null;
    cellEditors[1] = new CheckboxCellEditor(pathMatchersTable);
    cellEditors[2] = new CheckboxCellEditor(pathMatchersTable);

    // cell content
    pathMatchers.setCellEditors(cellEditors);
    pathMatchers.setColumnProperties(EditColTableDef.getProperties(TABLE_DEF));
  }

  /**
   * Creates the <code>PathExpression</code> object used internally for
   * filtering nodes.
   */
  public void createPathMatcherModel() {
    pathExpression = new PathExpression();
    PathExpression pathExpressionRef = (PathExpression) pathExpression;
    for (int i = 0; i < pathMatchers.getTable().getItemCount(); i++) {
      PathMatcherTerm matchers =
          (PathMatcherTerm) pathMatchers.getElementAt(i);
      pathExpressionRef.addPathMatcher(matchers);
    }
  }

  /**
   * Accessor to the <code>PathMatcher</code> object used for filtering.
   *
   * @return <code>PathMatcher</code> object used for filtering.
   */
  public PathMatcher getPathMatcherModel() {
    return pathExpression;
  }

  /**
   * Lets the user select a filename and location to save current path
   * expression.
   */
  private void savePathExpression() {
    // Create an array objects to be written from the table
    int count = pathMatchers.getTable().getItemCount();
    PathMatcherTerm[] list = new PathMatcherTerm[count];
    for (int i = 0; i < count; i++) {
      list[i] = (PathMatcherTerm) pathMatchers.getElementAt(i);
    }

    // pop up a dialog box so that user can select a filename and location
    SaveAsDialog saveas = new SaveAsDialog(control.getShell());
    if (saveas.open() == SaveAsDialog.OK) {
      IPath filePath = saveas.getResult();
      String fullPath = FILE_PREFIX
          + Platform.getLocation().append(filePath).toOSString();
      XmlPersistentPathExpression persist = new XmlPersistentPathExpression();

      try {
        persist.save(new URI(fullPath), list);
      } catch (IOException e) {
        System.err.println("I/O Exception.");
        e.printStackTrace();
      } catch (URISyntaxException e) {
        System.err.println("Malformed URI! Check the filename!");
        e.printStackTrace();
      }

      IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
      try {
        file.refreshLocal(1,null);
      } catch (CoreException e) {
        System.err.println("Unable to refresh");
        e.printStackTrace();
      }
    }
  }

  /**
   * Pops up a dialog box to select a file that contains the path expression
   * which will be loaded.
   */
  private void loadPathExpression() {
    // ask for the file path
    FileDialog loadFileDialog = new FileDialog(control.getShell());
    loadFileDialog.setText("Select a File to Load a Path Expression");
    loadFileDialog.setFilterPath(Platform.getLocation().toOSString());
    String filePath = loadFileDialog.open();
    if (filePath == null) {
      // user must have canceled load action
      return;
    }

    XmlPersistentPathExpression persist = new XmlPersistentPathExpression();
    PathMatcherTerm[] list = new PathMatcherTerm[0];
    try {
      list = persist.load(new URI("file://" + filePath));
    } catch (URISyntaxException e) {
      System.err.println("Malformed URI! Check the filename!");
      e.printStackTrace();
    }

    // finally, load objects into the list
    for (PathMatcherTerm finder : list) {
      pathMatchers.add(finder);
    }
  }

  /**
   * Inner class that is used to provide labels for
   * <code>PathMatcherTerm</code> entries in the table.
   */
  private class PathMatcherLabelProvider
      extends LabelProvider implements ITableLabelProvider, ICellModifier {
    /**
     * The collection of listeners that will be notified upon a modification.
     * Listeners have to register through <code>addChangeListener</code> method.
     */
    private Collection<StructuredViewer> listeners = Sets.newHashSet();

    /**
     * Returns an image object that matches the current state of the object for
     * the column specified by the <code>columnIndex</code>.
     *
     * @param element The object that appears in the list.
     * @param columnIndex The index of the column that the image will be
     * displayed.
     * @return Returns null for all cases.
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof PathMatcherTerm) {
        PathMatcherTerm tableEntry = ((PathMatcherTerm) element);
        switch (columnIndex) {
        case 0:
          return null;
        case 1:
          return Resources.getOnOff(tableEntry.isRecursive());
        case 2:
          return Resources.getOnOff(tableEntry.isCumulative());
        default:
          break;
        }
      }
      return null;
    }

    /**
     * Returns a String representation of this element. Not intended for
     * external use.
     *
     * @param element Element whose String representation is requested.
     * @param columnIndex The index of the column that this text will be
     * displayed.
     * @return Text that is associated with this element for the given index.
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof PathMatcherTerm) {
        PathMatcherTerm item = (PathMatcherTerm) element;
        switch (columnIndex) {
        case 0:
          // return the displayName of the path matcher term, trim just in case
          return item.getDisplayName().trim();
        case 1:
          if (item.isRecursive()) {
            return RECURSIVE_LABEL;
          }
          return NON_RECURSIVE_LABEL;
        case 2:
          if (item.isCumulative()) {
            return CUMULATIVE_LABEL;
          }
          return NON_CUMULATIVE_LABEL;
        default:
          break;
        }
      }
      return "";
    }

    /**
     * Checks whether the given property of the given element can be
     * modified.
     *
     * @param element The object that lies on the list.
     * @param property The <code>String</code> representation of the column
     * whose information is requested.
     * @return <code>true</code> if the property can be modified, and
     * <code>false</code> if it is not modifiable
     */
    @Override
    public boolean canModify(Object element, String property) {
      return EditColTableDef.get(TABLE_DEF, property).isEditable();
    }

    /**
     * Returns the value for the given property of the given element.
     * Returns <code>null</code> if the element does not have the given
     * property.
     *
     * @param element The object that lies on the list.
     * @param property The <code>String</code> representation of the column
     * whose information is requested.
     * @return the property value
     */
    @Override
    public Object getValue(Object element, String property) {
      if (element instanceof PathMatcherTerm) {
        PathMatcherTerm relation = ((PathMatcherTerm) element);
        if (property.equals(COL_RECURSIVE)) {
          return relation.isRecursive();
        } else if (property.equals(COL_CUMULATIVE)) {
          return relation.isCumulative();
        }
      }
      return null;
    }

    /**
     * Modifies the value for the given property of the given element.
     *
     * @param element The object that lies on the list.
     * @param property The <code>String</code> representation of the column
     * which will be modified.
     * @param value The new property value.
     */
    @Override
    public void modify(Object element, String property, Object value) {
      if (!(value instanceof Boolean)) {
        return;
      }
      if (!(element instanceof TableItem)) {
        return;
      }
      Object o = ((TableItem) element).getData();

      if (!(o instanceof PathMatcherTerm)) {
        return;
      }

      PathMatcherTerm relation = ((PathMatcherTerm) o);

      if (property.equals(COL_RECURSIVE)) {
        relation.setRecursive((Boolean) value);
      } else if (property.equals(COL_CUMULATIVE)) {
        relation.setCumulative((Boolean) value);
      }

      // update the column / line we just modified
      fireSelectionChange(o, new String[] {property});
    }

    /**
     * Adds the given listener to the set of <code>StructuredViewer</code>s
     * listening this object
     *
     * @param listener New listener for this label provider.
     */
    public void addChangeListener(StructuredViewer listener) {
      listeners.add(listener);
    }

    /**
     * Removes the given listener from the set of <code>StructuredViewer</code>s
     * listening this object
     *
     * @param listener Listener that will be removed.
     */
    public void removeChangeListener(StructuredViewer listener) {
      listeners.remove(listener);
    }

    /**
     * Fires update events for all listener of this object.
     *
     * @param object The modified object.
     * @param properties Properties that are changed.
     */
    protected void fireSelectionChange(Object object, String[] properties) {
      for (StructuredViewer listener : listeners) {
        listener.update(object, properties);
      }
    }
  } // end inner class PathMatcherLabelProvider

}
