package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets.LayoutGeneratorsControl;
import com.google.devtools.depan.view_doc.layout.plugins.LayoutGeneratorContributor;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ViewFromGraphDocPage extends WizardPage {

  public static final String PAGE_NAME = "Create View Editor document";

  public static final String PAGE_DESCRIPTION =
      "Setup initial View Editor state";

  private LayoutGeneratorsControl layoutChoice;

  protected ViewFromGraphDocPage() {
    super(PAGE_NAME);
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

  private Group setupOptions(Composite parent) {
    Group result = new Group(parent, SWT.NONE);

    GridLayout layout = new GridLayout(2, false);
    result.setLayout(layout);
    result.setText("Diagram options");

    // TODO: Add edge selections ..

    Text label = new Text(result, SWT.NULL);
    label.setText("Layout:");

    layoutChoice = new LayoutGeneratorsControl(result);
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
    setPageComplete(isPageComplete());
  }

  public LayoutGenerator getLayoutGenerator() {
    return layoutChoice.getChoice().getLayoutGenerator();
  }
}
