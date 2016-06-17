package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AnalysisOutputPart;
import com.google.devtools.depan.platform.WorkspaceTools;
import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets.LayoutGeneratorsControl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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

  private AnalysisOutputPart outputPart;

  private IContainer defaultContainer;

  private String defaultFilename;

  private LayoutGeneratorsControl layoutChoice;

  protected ViewFromGraphDocPage(
      IContainer defaultContainer, String defaultFilename) {
    super(PAGE_NAME);
    this.defaultContainer = defaultContainer;
    this.defaultFilename = defaultFilename;

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

    String outputFilename = WorkspaceTools.guessNewFilename(
        defaultContainer, defaultFilename, 1, 10);

    outputPart = new AnalysisOutputPart(
        this, defaultContainer, outputFilename);
    Composite outputGroup = outputPart.createControl(result);
    outputGroup.setLayoutData(createHorzFillData());

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
    String result = outputPart.getErrorMsg();
    if (null != result) {
      return result;
    }
    return null;
  }

  protected void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(isPageComplete());
  }

  public String getFilename() {
    return outputPart.getFileName();
  }

  public IFile getOutputFile() throws CoreException {
    return outputPart.getOutputFile();
  }

  public LayoutGenerator getLayoutGenerator() {
    return layoutChoice.getChoice().getLayoutGenerator();
  }
}
