package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.wizards.AnalysisOutputPart;
import com.google.devtools.depan.platform.WorkspaceTools;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ViewFromGraphDocPage extends WizardPage {

  public static final String PAGE_NAME = "Create View Editor document";

  public static final String PAGE_DESCRIPTION =
      "Setup initial View Editor state";

  private AnalysisOutputPart outputPart;

  private IContainer defaultContainer;

  private String defaultFilename;

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
    Composite container = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, true);
    layout.marginWidth = 0;
    layout.verticalSpacing = 9;
    container.setLayout(layout);

    String outputFilename = WorkspaceTools.guessNewFilename(
        defaultContainer, defaultFilename, 1, 10);

    outputPart = new AnalysisOutputPart(
        this, defaultContainer, outputFilename);
    Composite outputGroup = outputPart.createControl(container);
    outputGroup.setLayoutData(createHorzFillData());

    updateStatus(getPageErrorMsg());
    setControl(container);
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
}
