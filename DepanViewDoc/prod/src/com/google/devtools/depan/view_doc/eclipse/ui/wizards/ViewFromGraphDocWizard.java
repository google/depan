package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;
import com.google.devtools.depan.graph_doc.model.GraphModelReference;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.platform.PlatformTools;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditorInput;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.grid.GridLayoutGenerator;
import com.google.devtools.depan.view_doc.model.ViewDocument;
import com.google.devtools.depan.view_doc.model.ViewPreferences;

import java.text.MessageFormat;
import java.util.Collection;

public class ViewFromGraphDocWizard extends FromGraphDocWizard {

  private ViewFromGraphDocPage page;

  @Override
  public void addPages() {
    page = new ViewFromGraphDocPage(
        getGraphFile().getProject(), getGraphResources());
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
    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher =
        page.getLayoutMatcher();
    userPrefs.setLayoutMatcherRef(matcher);

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
    Collection<GraphNode> nodes = getNodes();
    if ((null == nodes) || (nodes.isEmpty())) {
      return "Empty View";
    }

    String srcBase = PlatformTools.getBaseName(getGraphFile());
    if (entireGraph()) {
      return srcBase;
    }

    return MessageFormat.format("{0}_{1}", srcBase, getDetail());
  }
}
