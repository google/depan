package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.GraphEdgeMatcherSelectorControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets.LayoutGeneratorsControl;
import com.google.devtools.depan.view_doc.layout.plugins.LayoutGeneratorContributor;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.util.List;

public class ViewFromGraphDocPage extends WizardPage {

  public static final String PAGE_NAME = "Create View Editor document";

  public static final String PAGE_DESCRIPTION =
      "Setup initial View Editor state";

  private LayoutGeneratorsControl layoutChoice;

  private GraphEdgeMatcherSelectorControl matcherChoice;

  private GraphResources graphInfo;

  protected ViewFromGraphDocPage(GraphResources graphInfo) {
    super(PAGE_NAME);
    this.graphInfo = graphInfo;

    setTitle(PAGE_NAME);
    setDescription(PAGE_DESCRIPTION);
 }

  @Override
  public void createControl(Composite parent) {
    Composite result = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    result.setLayout(layout);

    Composite optionsGroup = setupOptions(result);
    optionsGroup.setLayoutData(createHorzFillData());

    updateStatus(getPageErrorMsg());
    setControl(result);
  }

  @SuppressWarnings("unused")
  private Group setupOptions(Composite parent) {
    Group result = new Group(parent, SWT.NONE);

    GridLayout layout = new GridLayout(2, false);
    result.setLayout(layout);
    result.setText("Diagram options");

    Label layoutLabel = Widgets.buildCompactLabel(result, "Layout: ");
    layoutChoice = new LayoutGeneratorsControl(result);
    layoutChoice.setLayoutData(Widgets.buildHorzFillData());

    Label relSetLabel = Widgets.buildCompactLabel(result, "Edges: ");
    matcherChoice = new GraphEdgeMatcherSelectorControl(result);
    matcherChoice.setLayoutData(Widgets.buildHorzFillData());

    GraphEdgeMatcherDescriptor matcher = graphInfo.getDefaultEdgeMatcher();
    List<GraphEdgeMatcherDescriptor> choices =
        graphInfo.getEdgeMatcherChoices();
    matcherChoice.setInput(matcher, choices);

    return result;
  }

  /**
   * Provide a GridData instance that should expand to fill any available
   * horizontal space.
   */
  protected GridData createHorzFillData() {
    return new GridData(SWT.FILL, SWT.FILL, true, false);
  }

  protected String getPageErrorMsg() {
    LayoutGeneratorContributor layout = layoutChoice.getChoice();
    if (null == layout) {
      return "Initial layout must be specified";
    }
    return null;
  }

  protected void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete((null == message) && isPageComplete());
  }

  public LayoutGenerator getLayoutGenerator() {
    return layoutChoice.getChoice().getLayoutGenerator();
  }

  public GraphEdgeMatcherDescriptor getLayoutMatcher() {
    return matcherChoice.getSelection();
  }
}
