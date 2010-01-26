/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.devtools.depan.eclipse.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.devtools.depan.eclipse.Project;
import com.google.devtools.depan.eclipse.persist.ObjectXmlPersist;
import com.google.devtools.depan.eclipse.persist.XStreamFactory;
import com.google.devtools.depan.eclipse.plugins.SourcePlugin;
import com.google.devtools.depan.eclipse.plugins.SourcePluginRegistry;
import com.google.devtools.depan.model.RelationshipSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A drop-down widget showing a list of named set of relationships.
 *
 * Listener are notified whenever the selected relation set is changed.
 *
 * This control listens for resource changes, and updates the set of known
 * relation sets when a dpans file changes.
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class RelationshipSetPickerControl extends Composite
    implements ViewerObjectToString, IResourceChangeListener {

  private static final Logger logger =
      Logger.getLogger(RelationshipSetPickerControl.class.getName());

  /**
   * 
   */
  public static final String RELATION_SET_LABEL = "Relation Set: ";

  public static final String RELATION_SET_EXT = "dpans";

  /**
   * Content of the list.
   */
  private ListContentProvider<ContentNameProvider> relationshipSets = null;

  /**
   * Listener when the selection change.
   */
  private List<RelationshipSelectorListener> listeners = Lists.newArrayList();

  /**
   * The drop-down list itself.
   */
  private ComboViewer setsViewer = null;

  /**
   * A class used for displaying different name for each
   * {@link RelationshipSet}, not only using the
   * {@link RelationshipSet#toString()} method. This is useful because we want
   * to have a title containing information that is not in the
   * RelationshipSet itself, like its original file on the disk.
   * 
   * @author ycoppel@google.com (Yohann Coppel)
   * 
   */
  abstract static class ContentNameProvider {
    final RelationshipSet set;

    public ContentNameProvider(RelationshipSet set) {
      this.set = set;
    }

    @Override
    public abstract String toString();
  }

  /**
   * A {@link ContentNameProvider} that represents a {@link RelationshipSet}
   * saved on disk.
   * 
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  static class SavedContentNameProvider extends ContentNameProvider {
    final String path;

    public SavedContentNameProvider(String pathToFile, RelationshipSet set) {
      super(set);
      this.path = pathToFile;
    }

    @Override
    public String toString() {
      return "+ " + set.getName() + " (" + path + ")";
    }

    /**
     * Generate hash code based on name and path.
     */
    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (null == path ? 0 : path.hashCode());
      hash = hash * 31 + (null == set ? 0 : set.getName().hashCode());
      return hash;
    }

    /*
     * Determine equality based on name and path.
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SavedContentNameProvider) {
        SavedContentNameProvider that = (SavedContentNameProvider) obj;
        return that.path.equals(this.path)
            && that.set.getName().equals(this.set.getName());
      }
      return super.equals(obj);
    }
  }

  /**
   * A {@link ContentNameProvider} representing a temporary 
   * {@link RelationshipSet}; i.e. which is lost when the program is closed.
   * 
   * @author ycoppel@google.com (Yohann Coppel)
   *
   */
  static class TempContentNameProvider extends ContentNameProvider {
    public final String name;

    public TempContentNameProvider(String name, RelationshipSet set) {
      super(set);
      this.name = name;
    }

    @Override
    public String toString() {
      return "// " + name;
    }
  }

  /**
   * A {@link ContentNameProvider} representing a built-in
   * {@link RelationshipSet}
   * 
   * @author ycoppel@google.com (Yohann Coppel)
   * 
   */
  static class BuiltinContentNameProvider extends ContentNameProvider {
    public BuiltinContentNameProvider(RelationshipSet set) {
      super(set);
    }

    @Override
    public String toString() {
      return "* " + set.getName();
    }
  }

  public static Label createPickerLabel(Composite parent) {
    Label result = new Label(parent, SWT.NONE);
    result.setText(RELATION_SET_LABEL);
    result.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    return result;
  }

  /////////////////////////////////////
  // Relationship Set Selector itself

  /**
   * @param top
   * @param string
   */
  public RelationshipSetPickerControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());

    setsViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.FLAT);
    relationshipSets = new ListContentProvider<ContentNameProvider>(setsViewer);
    setsViewer.setContentProvider(relationshipSets);
    setsViewer.setSorter(new AlphabeticSorter(this));
    fillSets();

    setsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        RelationshipSet set = extractFromSelection(event.getSelection());
        if (null == set) {
          return;
        }

        // Notify interested parties about the change
        fireSelectionChange(set);
      }
    });

    ResourcesPlugin.getWorkspace().addResourceChangeListener(
        this, IResourceChangeEvent.POST_CHANGE);
  }

  /**
   * Select the given {@link RelationshipSet} on the list if it is present.
   * @param instanceSet the {@link RelationshipSet} to select.
   */
  public void selectSet(RelationshipSet instanceSet) {
    for (ContentNameProvider content : relationshipSets.getObjects()) {
      if (content.set == instanceSet) {
        setsViewer.setSelection(new StructuredSelection(content));
        fireSelectionChange(instanceSet);
        return;
      }
    }
  }

  /**
   * @return the currently selected RelationshipSet, or <code>null</code> if
   *         nothing is selected.
   */
  public RelationshipSet getSelection() {
    return extractFromSelection(setsViewer.getSelection());
  }

  /**
   * Add all the given sets as temporary sets.
   * 
   * @param sets a map from a relationshipSet to its "title".
   */
  public void addSets(Map<RelationshipSet, String> sets) {
    for (Map.Entry<RelationshipSet, String> entries : sets.entrySet()) {
      relationshipSets.add(
          new TempContentNameProvider(entries.getValue(), entries.getKey()));
    }
  }

  /**
   * Fill the drop down list with data found in the workspace, in any project.
   */
  private void fillSets() {
    relationshipSets.add(new BuiltinContentNameProvider(DefaultRelationshipSet.SET));
    for (SourcePlugin plugin: SourcePluginRegistry.getInstances()) {
      for (RelationshipSet set : plugin.getBuiltinRelationshipSets()) {
        relationshipSets.add(new BuiltinContentNameProvider(set));
      }
    }

    Collection<Project> projects = Project.getProjects();
    // for every opened project....
    for (Project project : projects) {
      // get the list of files ending with ".dpans"
      for (IResource resource : project.listFiles(RELATION_SET_EXT)) {
        loadResource(resource);
      }
    }
  }

  /**
   * return the {@link RelationshipSet} for the given selection, or
   * <code>null</code> if an error happens.
   * 
   * @param selection the selection to extract the {@link RelationshipSet} from.
   * @return the extracted {@link RelationshipSet} or <code>null</code> in
   *         case of error.
   */
  private RelationshipSet extractFromSelection(ISelection selection) {
    if (!(selection instanceof IStructuredSelection)) {
      return null;
    }
    IStructuredSelection select = (IStructuredSelection) selection;
    if (select.getFirstElement() instanceof ContentNameProvider) {
      return ((ContentNameProvider) select.getFirstElement()).set;
    }
    return null;
  }

  @Override // ViewerObjectToString
  public String getString(Object object) {
    return object.toString();
  }

  /////////////////////////////////////
  // Resource change support

  /**
   * Call back when a resource is changed on the workspace.
   * 
   * @see org.eclipse.core.resources.IResourceChangeListener
   *      #resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
   */
  public void resourceChanged(IResourceChangeEvent event) {
    try {
      // recursively visit the changed resources
      event.getDelta().accept(new IResourceDeltaVisitor() {
        public boolean visit(IResourceDelta delta) {
          IResource resource = ResourcesPlugin.getWorkspace().getRoot()
              .findMember(delta.getFullPath());
          // if this file is a set file (.dpans), reload it
          if ((resource instanceof IFile)
              && ("dpans".equals(resource.getFileExtension()))) {
            loadResource(resource);
          }
          // When a file is saved, its project, and even the workspace root
          // are resources which are also modified. we need to look into them
          // to finally get the file. Returning true makes the visitor
          // recursive.
          return true;
        }
      
      });
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load (or reload) the given resource, and add its content to the drop down
   * list. This must be called when changed are made to any relevant (.dpans)
   * resources, so that this view reflect the changes.
   * 
   * @param resource the resource to load
   */
  private void loadResource(IResource resource) {
    Collection<RelationshipSet> sets = fetchRelationshipSets(resource);

    // path for this resource
    String path = resource.getFullPath().toString();

    // ContentNameProviders read from the file
    Set<ContentNameProvider> existing = Sets.newHashSet();

    // add all the RelationshipSet to the list (with their name)
    for (RelationshipSet rel : sets) {
      SavedContentNameProvider nameProvider =
          new SavedContentNameProvider(path, rel);

      // remove the name provider if it exists.
      if (relationshipSets.getObjects().contains(nameProvider)) {
        relationshipSets.remove(nameProvider);
      }
      existing.add(nameProvider);
      relationshipSets.add(nameProvider);
    }

    // to avoid concurrent modification: items to remove
    List<ContentNameProvider> toRemove = Lists.newArrayList();

    // find ContentNameProvider removed from this file
    for (ContentNameProvider nameProvider : relationshipSets.getObjects()) {
      if ((nameProvider instanceof SavedContentNameProvider)
          && (((SavedContentNameProvider) nameProvider).path.equals(path))
          && (!existing.contains(nameProvider))) {
        toRemove.add(nameProvider);
      }
    }
    // effectively remove them
    for (ContentNameProvider contentNameProvider : toRemove) {
      relationshipSets.remove(contentNameProvider);
    }
  }

  /**
   * Attempt to load the relationships, returning an empty collection
   * if there are any failures.
   * 
   * @param resource
   * @return
   */
  private Collection<RelationshipSet> fetchRelationshipSets(
      IResource resource) {
    try {
      // TODO(leeca):  Is this configured with the correct XStream flavor?
      ObjectXmlPersist persist = 
          new ObjectXmlPersist(XStreamFactory.getSharedRefXStream());
      Collection<RelationshipSet> sets =
          loadRelationshipSets(persist, resource);
      return sets;
    } catch (IOException e) {
      logger.warning(
          "Failed to load relationshipSets from " + resource.getLocationURI());
      return ImmutableList.of();
    }
  }

  /**
   * Isolate unchecked conversion.
   */
  @SuppressWarnings("unchecked")
  private Collection<RelationshipSet> loadRelationshipSets(
      ObjectXmlPersist persist, IResource resource) throws IOException {
    return (Collection<RelationshipSet>) persist.load(
        resource.getLocationURI());
  }

  /////////////////////////////////////
  // Listener support

  /**
   * @param listener new listener for this selector
   */
  public void addChangeListener(RelationshipSelectorListener listener) {
    listeners.add(listener);
  }

  /**
   * @param listener new listener for this selector
   */
  public void removeChangeListener(RelationshipSelectorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Called when the selection changes to the given {@link ISelection}.
   * @param selection the new selection
   */
  protected void fireSelectionChange(RelationshipSet newSet) {
    for (RelationshipSelectorListener listener : listeners) {
      listener.selectedSetChanged(newSet);
    }
  }
}
