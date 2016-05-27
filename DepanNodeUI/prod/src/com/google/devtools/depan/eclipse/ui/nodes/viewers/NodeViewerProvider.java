package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.eclipse.ui.nodes.trees.ViewerRoot;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface NodeViewerProvider {

  ViewerSorter getViewSorter();

  void addMultiActions(IMenuManager manager);

  void addItemActions(IMenuManager manager, Object menuElement);

  ViewerRoot buildViewerRoots();
}
