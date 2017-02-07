package com.google.devtools.depan.view_doc.model;

import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ViewExtension;

public interface ExtensionDataListener {
  void extensionDataChanged(
      ViewExtension ext, Object instance, Object propertyId, Object updates);

}
