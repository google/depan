package com.google.devtools.depan.platform;

import org.eclipse.ui.model.IWorkbenchAdapter;

@SuppressWarnings("rawtypes")
public class TypeAdapter {
  private final Class fromType;
  private final IWorkbenchAdapter adapter;

  public TypeAdapter(Class fromType, IWorkbenchAdapter adapter) {
    this.fromType = fromType;
    this.adapter = adapter;
  }

  public IWorkbenchAdapter getAdapter(Object adaptableObject) {
    if (adaptableObject.getClass().isAssignableFrom(fromType)) {
      return adapter;
    }
    return null;
  }

  public Class getFromType() {
    // TODO Auto-generated method stub
    return fromType;
  }
}
