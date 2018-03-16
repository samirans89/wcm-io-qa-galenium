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
package io.wcm.qa.galenium.cookies;

import static io.wcm.qa.galenium.reporting.GaleniumReportUtil.assignCategory;
import static io.wcm.qa.galenium.reporting.GaleniumReportUtil.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import io.wcm.qa.galenium.util.GaleniumContext;

/**
 * Handles persisting fetched cookies so they can be used by all tests.
 */
public final class CookiePersistenceUtil {

  private static final Map<String, CookieProfile> PROFILES = new HashMap<>();
  private static final String CONTEXT_KEY_CURRENT_PROFILE = "currentCookieProfile";
  private static final String CATEGORY_PREFIX_PROFILE = "CP_";

  private CookiePersistenceUtil() {
    // do not instantiate
  }

  /**
   * @param profileToAdd
   */
  public static void addProfile(CookieProfile profileToAdd) {
    PROFILES.put(profileToAdd.getProfileName(), profileToAdd);
  }

  /**
   * Add and set profile to use in current thread.
   * @param profileToUse
   */
  public static void useProfile(CookieProfile profileToUse) {
    addProfile(profileToUse);
    useProfile(profileToUse.getProfileName());
  }

  /**
   * Set profile to use in current thread.
   * @param profileName
   */
  public static void useProfile(String profileName) {
    GaleniumContext.put(CONTEXT_KEY_CURRENT_PROFILE, profileName);
  }

  /**
   * @return profile set to be used in this thread
   */
  public static CookieProfile getCurrentProfile() {
    Object profileName = GaleniumContext.get(CONTEXT_KEY_CURRENT_PROFILE);
    CookieProfile currentProfile = PROFILES.get(profileName);
    return currentProfile;
  }

  /**
   * @return whether there is a profile set for this thread
   */
  public static boolean hasCurrentProfile() {
    return getCurrentProfile() != null;
  }

  /**
   * Sets all fetched cookies from profile in current driver.
   * @param profileToApply
   */
  public static void applyProfileToDriver(CookieProfile profileToApply) {
    if (profileToApply == null) {
      getLogger().warn("cookie profile is null.");
      return;
    }
    getLogger().debug("applying cookie profile: '" + profileToApply.getProfileName() + "'");
    WebDriver driver = GaleniumContext.getDriver();
    if (driver == null) {
      getLogger().error("driver is null, when trying to apply cookie profile: " + profileToApply.getProfileName());
      return;
    }
    Collection<Cookie> fetchedCookies = profileToApply.getFetchedCookies();
    for (Cookie cookie : fetchedCookies) {
      if (getLogger().isDebugEnabled()) {
        StringBuilder addCookieMessage = new StringBuilder();
        addCookieMessage.append("adding cookie to driver: '");
        addCookieMessage.append(cookie.getName());
        addCookieMessage.append("' (domain: '");
        addCookieMessage.append(cookie.getDomain());
        addCookieMessage.append("', path: '");
        addCookieMessage.append(cookie.getPath());
        addCookieMessage.append("')");
        getLogger().debug(addCookieMessage.toString());
      }
      try {
        driver.manage().addCookie(cookie);
      }
      catch (WebDriverException ex) {
        getLogger().warn("could not set cookie ('" + cookie.getName() + "') when applying profile '" + profileToApply.getProfileName() + "'", ex);
      }
    }
    assignCategory(CATEGORY_PREFIX_PROFILE + profileToApply.getProfileName());
  }

}
