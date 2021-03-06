/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.qa.glnm.galen.validation;

import com.galenframework.specs.Spec;
import com.galenframework.specs.page.PageSection;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationListener;
import com.galenframework.validation.ValidationResult;

class NoOpValidationListener implements ValidationListener {

  /** {@inheritDoc} */
  @Override
  public void onAfterObject(PageValidation pageValidation, String objectName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onAfterPageAction(GalenPageAction action) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onAfterSection(PageValidation pageValidation, PageSection pageSection) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onAfterSpecGroup(PageValidation pageValidation, String specGroupName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onAfterSubLayout(PageValidation pageValidation, String objectName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onBeforePageAction(GalenPageAction action) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onBeforeSection(PageValidation pageValidation, PageSection pageSection) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onBeforeSpec(PageValidation pageValidation, String objectName, Spec spec) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onGlobalError(Exception e) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onObject(PageValidation pageValidation, String objectName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onSpecError(PageValidation pageValidation, String objectName, Spec spec, ValidationResult validationResult) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onSpecGroup(PageValidation pageValidation, String specGroupName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onSpecSuccess(PageValidation pageValidation, String objectName, Spec spec, ValidationResult validationResult) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public void onSubLayout(PageValidation pageValidation, String objectName) {
    // noop
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "NoOp Validation Listener";
  }
}
