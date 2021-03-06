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
package io.wcm.qa.glnm.hamcrest.baseline;

import org.hamcrest.Matcher;

import io.wcm.qa.glnm.differences.base.Difference;
import io.wcm.qa.glnm.differences.base.Differences;

/**
 * Combines {@link org.hamcrest.Matcher} with {@link io.wcm.qa.glnm.differences.base.Differences}.
 *
 * @param <T> matcher type
 * @since 5.0.0
 */
public interface DifferentiatingMatcher<T> extends Matcher<T>, Differences {

  /**
   * <p>add.</p>
   *
   * @param difference a {@link io.wcm.qa.glnm.differences.base.Difference} object.
   * @since 5.0.0
   */
  void add(Difference difference);

  /**
   * <p>prepend.</p>
   *
   * @param difference a {@link io.wcm.qa.glnm.differences.base.Difference} object.
   * @since 5.0.0
   */
  void prepend(Difference difference);
}
