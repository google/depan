/*
 * Copyright 2016 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.view_doc.layout.eclipse.ui.widgets;

import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlan;
import com.google.devtools.depan.view_doc.layout.model.LayoutPlanDocument;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A simple, listener free {@link Control} for displaying and editing
 * the basic attributes of one copy of a {@link LayoutPlan} in place.
 * 
 * Without listeners, user's need to collect the results form the control
 * when the edit commits changes.  These values are available via the
 * {@link #getLayoutPlanName()} and {@link #getLayoutPlanSummary()} methods.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class BasicLayoutPlanEditorControl extends Composite {

  private final Text nameText;

  private final Text summaryText;

  private LayoutPlanDocument<? extends LayoutPlan> plan;

  @SuppressWarnings("unused")
  public BasicLayoutPlanEditorControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(5));

    Label nameLabel = Widgets.buildCompactLabel(this, "Name: ");
    nameText = Widgets.buildGridBoxedText(this);
    nameText.setEditable(false);
    updateNameText();

    Label summaryLabel = Widgets.buildCompactLabel(this, "Summary: ");
    summaryText = Widgets.buildGridBoxedText(this);
    updateSummaryText();

    Button inferButton = Widgets.buildTrailPushButton(this, "Infer");
    inferButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        inferLayoutPlanSummary();
      }
    });
  }

  public void setInput(LayoutPlanDocument<? extends LayoutPlan> plan) {
    this.plan = plan;
    updateNameText();
    updateSummaryText();
  }

  public String getLayoutPlanSummary() {
    return summaryText.getText();
  }

  /////////////////////////////////////
  // Field management

  private void inferLayoutPlanSummary() {
    if (noLayoutPlan()) {
      return;
    }

    String summary = plan.buildSummary();
    plan.setSummary(summary);
    updateSummaryText();
  }

  private void updateNameText() {
    // TODO: visible indicator for empty name
    nameText.setText(fmtLayoutPlanName());
  }

  private void updateSummaryText() {
    summaryText.setText(fmtLayoutPlanSummary());
  }

  private String fmtLayoutPlanName() {
    if (noLayoutPlan()) {
      return "- no layout plan -";
    }
    return plan.getName();
  }

  private String fmtLayoutPlanSummary() {
    if (noLayoutPlan()) {
      return "- no layout plan -";
    }

    String result = plan.getSummary();
    if (null == result) {
      return "- missing summary -";
    }
    if (result.isEmpty()) {
      return "- empty summary -";
    }

    return result;
  }

  private boolean noLayoutPlan() {
    if (null != plan) {
      return false;
    }

    // Intent to be no filter unless it is detected and valid.
    return true;
  }
}
