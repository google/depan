package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.resources.GraphResources;
import com.google.devtools.depan.matchers.eclipse.ui.widgets.EdgeMatcherSelectorControl;
import com.google.devtools.depan.matchers.models.GraphEdgeMatcherDescriptor;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.resources.PropertyDocumentReference;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets.LayoutGeneratorsControl;
import com.google.devtools.depan.view_doc.layout.plugins.LayoutGeneratorContributor;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class ViewFromGraphDocPage extends WizardPage {

  public static final String PAGE_NAME = "Create View Editor document";

  public static final String PAGE_DESCRIPTION =
      "Setup initial View Editor state";

  private final IProject project;

  private final GraphResources graphInfo;

  /////////////////////////////////////
  // UX Elements

  private LayoutGeneratorsControl layoutChoice;

  private EdgeMatcherSelectorControl matcherChoice;

  protected ViewFromGraphDocPage(IProject project, GraphResources graphInfo) {
    super(PAGE_NAME);
    this.project = project;
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
    Group result = Widgets.buildGridGroup(parent, "Diagram options", 2);

    Label layoutLabel = Widgets.buildCompactLabel(result, "Layout: ");
    layoutChoice = new LayoutGeneratorsControl(result);
    layoutChoice.setLayoutData(Widgets.buildHorzFillData());

    Label relSetLabel = Widgets.buildCompactLabel(result, "Edges: ");
    matcherChoice = new EdgeMatcherSelectorControl(result);
    matcherChoice.setLayoutData(Widgets.buildHorzFillData());

    PropertyDocumentReference<GraphEdgeMatcherDescriptor> matcher =
        graphInfo.getDefaultEdgeMatcher();
    matcherChoice.setInput(matcher, project);

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

  public PropertyDocumentReference<GraphEdgeMatcherDescriptor>
      getLayoutMatcher() {
    return matcherChoice.getSelection();
  }
}
