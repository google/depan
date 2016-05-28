package com.google.devtools.depan.graph_doc.eclipse.ui.registry;

public interface FromGraphDocContributor {

  String getLabel();

  FromGraphDocWizard newWizard();
}
