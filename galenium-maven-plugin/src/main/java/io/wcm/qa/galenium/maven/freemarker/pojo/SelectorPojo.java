/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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
package io.wcm.qa.galenium.maven.freemarker.pojo;

import io.wcm.qa.galenium.maven.freemarker.util.FormatUtil;
import io.wcm.qa.galenium.selectors.Selector;

public class SelectorPojo {

  private Selector selector;

  public SelectorPojo(Selector selector) {
    setSelector(selector);
  }

  public String getConstantName() {
    String elementName = getElementName();
    return FormatUtil.kebapToConstant(elementName.replaceAll("\\.", "__"));
  }

  public String getCss() {
    return getSelector().asString();
  }

  public String getElementName() {
    return getSelector().elementName();
  }

  public Selector getSelector() {
    return selector;
  }

  private void setSelector(Selector selector) {
    this.selector = selector;
  }

}