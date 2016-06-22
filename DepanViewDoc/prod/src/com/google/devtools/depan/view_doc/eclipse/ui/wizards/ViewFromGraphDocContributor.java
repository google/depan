package com.google.devtools.depan.view_doc.eclipse.ui.wizards;

import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocContributor;
import com.google.devtools.depan.graph_doc.eclipse.ui.plugins.FromGraphDocWizard;

public class ViewFromGraphDocContributor implements FromGraphDocContributor {

  @Override
  public String getLabel() {
    return "New View";
  }

  @Override
  public FromGraphDocWizard newWizard() {
    return new ViewFromGraphDocWizard();
  }
}
