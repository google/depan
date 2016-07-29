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

package com.google.devtools.depan.nodes.filters.eclipse.ui.widgets;

import com.google.devtools.depan.nodes.filters.model.ContextualFilter;
import com.google.devtools.depan.nodes.filters.sequence.BasicFilter;
import com.google.devtools.depan.platform.eclipse.ui.widgets.Widgets;

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
 * the basic attributes of one copy of a {@link ContextualFilter} in place.
 * 
 * Without listeners, user's need to collect the results form the control
 * when the edit commits changes.  These values are available via the
 * {@link #getFilterName()} and {@link #getFilterSummary()} methods.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class BasicFilterEditorControl extends Composite {

  private final Text nameText;

  private final Text summaryText;

  private BasicFilter editFilter;

  @SuppressWarnings("unused")
  public BasicFilterEditorControl(FilterEditorControl<?> parent) {
    super(parent, SWT.NONE);
    setLayout(Widgets.buildContainerLayout(5));

    Label nameLabel = Widgets.buildCompactLabel(this, "Name: ");
    nameText = Widgets.buildGridBoxedText(this);
    updateNameText();

    Label summaryLabel = Widgets.buildCompactLabel(this, "Summary: ");
    summaryText = Widgets.buildGridBoxedText(this);
    updateSummaryText();

    Button inferButton = Widgets.buildTrailPushButton(this, "Infer");
    inferButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        inferFilterSummary();
      }
    });
  }

  public void setInput(BasicFilter editFilter) {
    this.editFilter = editFilter;
    updateNameText();
    updateSummaryText();
  }

  public String getFilterName() {
    return nameText.getText();
  }

  public String getFilterSummary() {
    return summaryText.getText();
  }

  /////////////////////////////////////
  // Field management

  private void inferFilterSummary() {
    if (noFilter()) {
      return;
    }

    // Use edited filter information to build summary.
    String summary = buildFilter().buildSummary();
    editFilter.setSummary(summary);
    updateSummaryText();
  }

  private BasicFilter buildFilter() {
    return ((FilterEditorControl<?>) getParent()).buildFilter();
  }

  private void updateNameText() {
    // TODO: visible indicator for empty name
    nameText.setText(fmtFilterName());
  }

  private void updateSummaryText() {
    summaryText.setText(fmtFilterSummary());
  }

  private String fmtFilterName() {
    if (noFilter()) {
      return "- no filter -";
    }
    return editFilter.getName();
  }

  private String fmtFilterSummary() {
    if (noFilter()) {
      return "- no filter -";
    }

    String result = editFilter.getSummary();
    if (null == result) {
      return "- missing summary -";
    }
    if (result.isEmpty()) {
      return "- empty summary -";
    }

    return result;
  }

  private boolean noFilter() {
    if (null != editFilter) {
      return false;
    }

    // Intent to be no filter unless it is detected and valid.
    return true;
  }
}
