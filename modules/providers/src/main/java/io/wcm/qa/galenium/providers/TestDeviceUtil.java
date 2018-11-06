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
package io.wcm.qa.galenium.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;

import io.wcm.qa.galenium.configuration.CsvUtil;
import io.wcm.qa.galenium.configuration.GaleniumConfiguration;
import io.wcm.qa.galenium.device.BrowserType;
import io.wcm.qa.galenium.device.DeviceProfile;
import io.wcm.qa.galenium.device.TestDevice;
import io.wcm.qa.galenium.exceptions.GaleniumException;
import io.wcm.qa.galenium.mediaquery.MediaQuery;
import io.wcm.qa.galenium.mediaquery.MediaQueryUtil;
import io.wcm.qa.galenium.reporting.GaleniumReportUtil;

/**
 * Convenience methods around test devices.
 */
public final class TestDeviceUtil {

  private static final File CSV_FILE_DEVICES = new File(GaleniumConfiguration.getDeviceCsvFilePath());
  private static final Integer MEDIA_QUERY_HEIGHT = GaleniumConfiguration.getMediaQueryHeight();

  private TestDeviceUtil() {
    // do not instantiate
  }

  /**
   * @return first of the configured test devices
   */
  public static List<Object> getSingleTestDevice() {
    Collection<TestDevice> testDevices = TestDeviceUtil.getTestDevicesForBrowsersAndMqs();
    if (testDevices == null || testDevices.isEmpty()) {
      throw new GaleniumException("no configured devices found, when trying to get single test device.");
    }
    int middle = testDevices.size() / 2;
    // CollectionUtils#get() is deprecated for everything except Object
    Object deviceCollectionAsObject = testDevices;
    Object singleDevice = CollectionUtils.get(deviceCollectionAsObject, middle);
    List<Object> singleDeviceList = Collections.singletonList(singleDevice);
    return singleDeviceList;
  }

  /**
   * Test device for upper bound of media query.
   * @param browserType browser to use
   * @param mediaQuery media query to get upper bound from
   * @return test device
   */
  public static TestDevice getTestDeviceForUpperBound(BrowserType browserType, MediaQuery mediaQuery) {
    int upperBound = mediaQuery.getUpperBound();
    String mediaQueryName = mediaQuery.getName();
    return TestDeviceUtil.getTestDevice(browserType, mediaQueryName, upperBound);
  }

  /**
   * Test devices using the configured Browsers and upper bounds of the configured media queries.
   * @return configured test devices
   */
  public static Collection<TestDevice> getTestDevicesForBrowsersAndMqs() {
    Collection<TestDevice> testDevices = new ArrayList<>();
    for (MediaQuery mediaQuery : MediaQueryUtil.getMediaQueries()) {
      for (BrowserType browserType : GaleniumConfiguration.getBrowserTypes()) {
        TestDevice testDevice = getTestDeviceForUpperBound(browserType, mediaQuery);
        testDevices.add(testDevice);
      }
    }
    if (testDevices.isEmpty()) {
      throw new GaleniumException("no test devices configured");
    }
    return testDevices;
  }

  /**
   * @return all test devices defined in CSV
   */
  public static Collection<TestDevice> getTestDevicesFromDevicesCsv() {
    Collection<TestDevice> testDevices = new ArrayList<>();
    Collection<DeviceProfile> profiles = CsvUtil.<DeviceProfile>parseToBeans(CSV_FILE_DEVICES, DeviceProfile.class);
    for (DeviceProfile deviceProfile : profiles) {
      if (isProfileMatchesBrowsers(deviceProfile)) {
        GaleniumReportUtil.getLogger().debug("adding device: " + deviceProfile);
        testDevices.add(getTestDevice(deviceProfile));
      }
      else {
        GaleniumReportUtil.getLogger().debug("skipping device: " + deviceProfile);
      }
    }
    return testDevices;
  }

  private static String getDeviceName(BrowserType browserType, int screenWidth) {
    return browserType.name() + "_" + screenWidth;
  }

  private static List<String> getExcludeTags(BrowserType includedBrowserType, String mediaQueryName) {
    List<String> tags = new ArrayList<>();
    for (MediaQuery mediaQuery : MediaQueryUtil.getMediaQueries()) {
      if (StringUtils.equals(mediaQueryName, mediaQuery.getName())) {
        continue;
      }
      tags.add(mediaQuery.getName());
    }
    BrowserType[] browsers = BrowserType.values();
    for (BrowserType browserType : browsers) {
      if (browserType != includedBrowserType) {
        tags.add(browserType.name());
      }
    }
    return tags;
  }

  private static List<String> getIncludeTags(BrowserType browserType, String mediaQueryName) {
    List<String> tags = new ArrayList<>();
    tags.add(mediaQueryName);
    tags.add(browserType.name());
    return tags;
  }

  private static Dimension getScreenSize(int width) {
    return new Dimension(width, TestDeviceUtil.MEDIA_QUERY_HEIGHT);
  }

  private static TestDevice getTestDevice(BrowserType browserType, String mediaQueryName, int width) {
    String name = getDeviceName(browserType, width);
    Dimension screenSize = getScreenSize(width);
    TestDevice testDevice = new TestDevice(name, browserType, screenSize);
    setIncludeAndExcludeTags(testDevice, browserType, mediaQueryName);
    return testDevice;
  }

  private static void setIncludeAndExcludeTags(TestDevice testDevice, BrowserType browserType, String mediaQueryName) {
    testDevice.setIncludeTags(getIncludeTags(browserType, mediaQueryName));
    testDevice.setExcludeTags(getExcludeTags(browserType, mediaQueryName));
  }

  private static TestDevice getTestDevice(DeviceProfile deviceProfile) {
    TestDevice testDevice = new TestDevice(deviceProfile);
    MediaQuery mediaQuery = MediaQueryUtil.getMatchingMediaQuery(testDevice);
    BrowserType browserType = testDevice.getBrowserType();
    setIncludeAndExcludeTags(testDevice, browserType, mediaQuery.getName());
    return testDevice;
  }

  private static boolean isProfileMatchesBrowsers(DeviceProfile deviceProfile) {
    List<BrowserType> browserTypes = GaleniumConfiguration.getBrowserTypes();
    return browserTypes.contains(deviceProfile.getBrowserType());
  }

}
