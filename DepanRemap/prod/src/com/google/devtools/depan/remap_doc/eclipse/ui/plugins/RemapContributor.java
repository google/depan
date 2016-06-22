package com.google.devtools.depan.remap_doc.eclipse.ui.plugins;

import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.remap_doc.plugins.ElementEditor;
import com.google.devtools.depan.view_doc.eclipse.ui.plugins.ElementClassTransformer;

import java.util.Collection;

public interface RemapContributor {

  ElementClassTransformer<Class<? extends ElementEditor>> getElementEditorProvider();

  Collection<Class<? extends Element>> getElementClasses();
}
