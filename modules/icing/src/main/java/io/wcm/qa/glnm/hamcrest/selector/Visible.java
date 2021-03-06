/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2020 wcm.io
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
package io.wcm.qa.glnm.hamcrest.selector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import io.wcm.qa.glnm.sampling.element.VisibilitySampler;
import io.wcm.qa.glnm.selectors.base.Selector;

/**
 * Matches if selector defines a visible element.
 *
 * @since 5.0.0
 */
public class Visible extends SelectorSamplerMatcher<Boolean> {

  /**
   * Constructor.
   *
   * @since 5.0.0
   */
  public Visible() {
    this(Matchers.is(true));
  }

  /**
   * <p>Constructor for Visible.</p>
   *
   * @param matcher boolean matcher (i.e. baselining matcher)
   * @since 5.0.0
   */
  public Visible(Matcher<Boolean> matcher) {
    super(matcher, VisibilitySampler.class);
  }

  /** {@inheritDoc} */
  @Override
  public void describeTo(Description description) {
    describeSelector(description);
    description.appendText(" visible");
  }

  /**
   * Is element defined by selector visible
   *
   * @return matcher visibility matcher
   * @since 5.0.0
   */
  public static Matcher<Selector> visible() {
    return new Visible();
  }

  /**
   * Is element defined by selector visibitily matched.
   *
   * @param matcher a {@link org.hamcrest.Matcher} object.
   * @return matcher visibility matcher
   * @since 5.0.0
   */
  public static Matcher<Selector> visible(Matcher<Boolean> matcher) {
    return new Visible(matcher);
  }
}
