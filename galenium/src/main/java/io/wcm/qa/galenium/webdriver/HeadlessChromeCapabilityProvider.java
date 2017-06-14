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
package io.wcm.qa.galenium.webdriver;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.wcm.qa.galenium.reporting.GaleniumReportUtil;
import io.wcm.qa.galenium.util.GaleniumConfiguration;
import io.wcm.qa.galenium.util.TestDevice;

class HeadlessChromeCapabilityProvider extends ChromeCapabilityProvider {

  private static final String[] ARGUMENTS_HEADLESS = new String[] {
      // main headless arg
      "headless",
      // workaround (https://developers.google.com/web/updates/2017/04/headless-chrome#cli)
      "disable-gpu" };
  private static final String[] ARGUMENTS_HEADLESS_WINDOWS_WORKAROUND = new String[] {
      // workaround for windows: there is still a window opened, so put it somewhere offscreen
      "window-position=10000,0"
  };
  private TestDevice device;


  public HeadlessChromeCapabilityProvider(TestDevice device) {
    setDevice(device);
  }

  public TestDevice getDevice() {
    return device;
  }

  public void setDevice(TestDevice device) {
    this.device = device;
  }

  @Override
  protected DesiredCapabilities getBrowserSpecificCapabilities() {
    GaleniumReportUtil.getLogger().debug("setting up headless chrome.");
    DesiredCapabilities capabilities = super.getBrowserSpecificCapabilities();
    addChromeOption(capabilities, OPTIONS_KEY_ARGS, ARGUMENTS_HEADLESS);
    if (GaleniumConfiguration.isChromeHeadlessWindowsWorkaround()) {
      addChromeOption(capabilities, OPTIONS_KEY_ARGS, ARGUMENTS_HEADLESS_WINDOWS_WORKAROUND);
    }
    Dimension screenSize = getDevice().getScreenSize();
    String width = String.format("%d", screenSize.getWidth());
    String height = String.format("%d", screenSize.getHeight());
    String[] argumentsBrowserWindowSize = new String[] { "--window-size="
        + width
        + "x"
        + height };
    addChromeOption(capabilities, OPTIONS_KEY_ARGS, argumentsBrowserWindowSize);
    return capabilities;
  }

}
