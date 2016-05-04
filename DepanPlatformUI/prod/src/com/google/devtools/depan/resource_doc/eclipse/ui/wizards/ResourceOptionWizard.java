package com.google.devtools.depan.resource_doc.eclipse.ui.wizards;

import org.eclipse.swt.widgets.Composite;

/**
 * Extend the {@link AbstractResouceWizardPage} with options for
 * resource construction.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface ResourceOptionWizard {

  /**
   * Construct a control for resource options.
   * Return {@code null} if no option UI is necessary. 
   */
  Composite createOptionsControl(Composite container);

  /**
   * Error message for user display, or null if everything is fine.
   */
  String getErrorMsg();

  /**
   * Have a consistent set of options been generated?
   * Part of the answer to {@link WizardPage#isPageComplete().}
   * Often implemented as {@code return null == }{@link #getErrorMsg()}.
   */
  boolean isComplete();

  /**
   * Standard implementation framework for {@link ResourceOptionWizard}.
   * It does nothing, gracefully.
   */
  public static class Basic implements ResourceOptionWizard {

    @Override
    public Composite createOptionsControl(Composite container) {
      return null;
    }

    @Override
    public String getErrorMsg() {
      return null;
    }

    /**
     * {@inheritDoc}
     * 
     * Implemented here as {@code return null == }{@link #getErrorMsg();}
     */
    @Override
    public boolean isComplete() {
      return null == getErrorMsg();
    }
  }

  /**
   * Resuable simple {@link ResourceOptionWizard} for the common case
   * of no options added to the standard {@link AbstractResouceWizardPage}.
   */
  public static ResourceOptionWizard NO_OPTIONS = new Basic();
}
