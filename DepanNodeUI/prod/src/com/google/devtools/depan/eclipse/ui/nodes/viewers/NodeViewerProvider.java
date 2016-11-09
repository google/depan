package com.google.devtools.depan.eclipse.ui.nodes.viewers;

import com.google.devtools.depan.model.GraphNode;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface NodeViewerProvider {

  /**
   * Define a generous limit for the number of tree elements to automatically
   * open for display.  A more refined implementation might offer a user
   * configured choice, but this prevents the most egregious problems from
   * over-zealous expansion of tree elements.
   * 
   * [2007] Approx 8000 nodes takes ~8sec to expand and display.
   * So a limit of 1000 should keep the initial open time down to ~1sec.
   */
  public static final int AUTO_EXPAND_LIMIT = 1000;

  void addMultiActions(IMenuManager manager);

  void addItemActions(IMenuManager manager, Object menuElement);

  PlatformObject buildViewerRoots();

  Object findNodeObject(GraphNode node);

  void updateExpandState(TreeViewer viewer);
}
