package com.google.devtools.depan.platform;

import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Provides the adaptor for any type derived from  the supplied
 * {@link #fromType}.  Specifically, any object that appears to be
 * {@link Class#isAssignableFrom(Class)}.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
@SuppressWarnings("rawtypes")
public class TypeAdapter {
  private final Class<?> fromType;
  private final IWorkbenchAdapter adapter;

  public TypeAdapter(Class<?> fromType, IWorkbenchAdapter adapter) {
    this.fromType = fromType;
    this.adapter = adapter;
  }

  public IWorkbenchAdapter getAdapter(Object adaptableObject) {
    if (fromType.isAssignableFrom(adaptableObject.getClass())) {
    // if (adaptableObject.getClass().isAssignableFrom(fromType)) {
      return adapter;
    }
    return null;
  }

  public Class getFromType() {
    return fromType;
  }
}
