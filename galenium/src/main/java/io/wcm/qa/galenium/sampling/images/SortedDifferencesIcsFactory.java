/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
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
package io.wcm.qa.galenium.sampling.images;

import java.util.Comparator;

import io.wcm.qa.galenium.sampling.differences.Difference;
import io.wcm.qa.galenium.sampling.differences.SortedDifferences;
import io.wcm.qa.galenium.selectors.Selector;

public class SortedDifferencesIcsFactory extends DifferenceAwareIcsFactory {

  private SortedDifferences differences = new SortedDifferences();

  public SortedDifferencesIcsFactory(Selector selector) {
    super(selector);
  }

  public SortedDifferencesIcsFactory(Selector selector, String elementName) {
    super(selector, elementName);
  }

  public Comparator<Difference> getComparator() {
    return this.differences.getComparator();
  }

  @Override
  public SortedDifferences getDifferences() {
    return differences;
  }

  public void setComparator(Comparator<Difference> comparator) {
    this.differences.setComparator(comparator);
  }

  public void setDifferences(SortedDifferences differences) {
    this.differences = differences;
  }

}
