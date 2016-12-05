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

package com.google.devtools.depan.platform.eclipse.ui.wizards;

import org.eclipse.swt.widgets.Composite;

/**
 * Define the behavior on parts for a new wizard.
 * An {@link AbstractNewDocumentPage}
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public interface NewWizardOptionPart {

  /**
   * Create the visual elements for a Wizard option.
   * The composite result receives layout data from the caller.
   */
  Composite createPartControl(Composite container);

  /**
   * Indicate whether this part is ready for the wizard to finish.
   */
  boolean isComplete();

  /**
   * Provide an error message if the part is not complete.
   * A {@code null} result indicates no error, and is equivalent to
   * {@code isComplete() == true;}.
   */
  String getErrorMsg();

  public static abstract class Simple implements NewWizardOptionPart {

    public static boolean isPartComplete(NewWizardOptionPart part) {
      return null == part.getErrorMsg();
    }

    @Override
    public boolean isComplete() {
      return isPartComplete(this);
    }
  }
}
