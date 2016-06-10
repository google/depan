package com.google.devtools.depan.view_doc.eclipse.ui.handlers;

import com.google.devtools.depan.view_doc.eclipse.ui.editor.ViewEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Toggles boolean options for ViewEditor option commands.
 * The command id must match the id for the view option.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class ViewEditorOptionHandler extends AbstractHandler {

  private boolean isEnabled = false;

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ViewEditor viewer = getViewEditor(event);

    Command command = event.getCommand();
    String optionId = command.getId();
    boolean value = viewer.isOptionChecked(optionId);
    viewer.setBooleanOption(optionId, !value);
    return null;
  }

  private ViewEditor getViewEditor(ExecutionEvent event) {
    IEditorPart editor = HandlerUtil.getActiveEditor(event);
    if (!(editor instanceof ViewEditor)) {
      return null;
    }
    ViewEditor viewer = (ViewEditor) editor;
    return viewer;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

  @Override
  public void setEnabled(Object evalContext) {
    if (null == evalContext) {
      isEnabled = false;
      return;
    }

    IEvaluationContext context = (IEvaluationContext) evalContext;
    Object editor = context.getVariable(ISources.ACTIVE_EDITOR_NAME);
    if (editor instanceof ViewEditor) {
      isEnabled = true;
      return;
    }

    isEnabled = false;
    return;
  }
}
