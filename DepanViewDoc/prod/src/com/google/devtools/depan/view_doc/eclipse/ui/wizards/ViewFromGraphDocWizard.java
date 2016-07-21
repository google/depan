package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditorInput;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutGenerator;
import com.google.devtools.depan.view_doc.model.GraphModelReference;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import java.text.MessageFormat;

public class ViewFromGraphDocWizard extends FromGraphDocWizard {

  private ViewFromGraphDocPage page;

  @Override
  public void addPages() {
    page = new ViewFromGraphDocPage(getGraphResources());
    addPage(page);
  }

  @Override
  public boolean performFinish() {
    ViewEditorInput viewInput = buildViewInput();
    ViewEditor.startViewEditor(viewInput);
    return true;
  }

  /**
   * Unpack wizard page controls into a {@link ViewEditorInput}.
   */
  private ViewEditorInput buildViewInput() {
    String basename = calcName();

    // Create ViewDocument elements
    GraphModelReference graphRef =
        new GraphModelReference(getGraphFile(), getGraphDoc());

    ViewPreferences userPrefs = new ViewPreferences();
    GraphEdgeMatcherDescriptor matcher = page.getLayoutMatcher();
    userPrefs.setLayoutFinder(matcher);

    ViewDocument viewInfo = new ViewDocument(graphRef, getNodes(), userPrefs);

    ViewEditorInput result = new ViewEditorInput(viewInfo, basename);
    result.setInitialLayout(calcInitialLayout());

    return result;
  }

  private LayoutGenerator calcInitialLayout() {
    LayoutGenerator layout = page.getLayoutGenerator();
    if (null != layout) {
      return layout;
    }
    return new GridLayoutGenerator();
  }

  private String calcName() {
    String srcBase = getSourceBase();
    if (entireGraph()) {
      return srcBase;
    }
    GraphNode node = getTopNode();
    if (null == node) {
      return "Empty Graph";
    }

    String detail = calcDetailName(node);
    return MessageFormat.format("{0}_{1}", srcBase, detail);
  }

  private String calcDetailName(GraphNode node) {
    String baseName = node.friendlyString();
    int period = baseName.lastIndexOf('.');
    if (period > 0) {
      String segment = baseName.substring(period + 1);
      if (segment.length() > 3) {
        return segment;
      }
    }

    return baseName;
  }

  /**
   * Indicate if the selected nodes match the nodes from the graph document.
   */
  private boolean entireGraph() {
    if (null == getTopNode()) {
      return false;
    }
    if (null == getNodes()) {
      return false;
    }
    return getNodes().size() == getGraphDoc().getGraph().getNodes().size();
  }

  private String getSourceBase() {
    String name = getGraphFile().getName();
    String ext = getGraphFile().getFileExtension();
    // If null, no period is present
    if (null == ext) {
      return name;
    }
    // remove period and extension from end
    return name.substring(0, name.length() - 1 - ext.length());
  }
}
