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
package io.wcm.qa.glnm.sampling.network.base;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import io.wcm.qa.glnm.sampling.base.CachingBasedSampler;

/**
 * Functionality that helps building Jsoup based samplers.
 * @param <T> type of sample returned by sampler
 */
public abstract class JsoupBasedSampler<T> extends CachingBasedSampler<T> {

  private String url;
  private Map<String, String> requestCookies = new HashMap<String, String>();

  /**
   * @param url to connect to
   */
  public JsoupBasedSampler(String url) {
    setUrl(url);
  }

  public Map<String, String> getRequestCookies() {
    return requestCookies;
  }

  public String getUrl() {
    return url;
  }

  public void setRequestCookies(Map<String, String> cookies) {
    this.requestCookies = cookies;
  }

  /**
   * @return {@link JsoupConnectionProvider} to use for connection
   */
  protected JsoupConnectionProvider getConnectionProvider() {
    return new JsoupConnectionProvider() {
      @Override
      public Connection getConnection() {
        return Jsoup.connect(getUrl());
      }
    };
  }

  /**
   * @return connection used to fetch document
   */
  protected Connection getJsoupConnection() {
    Connection connection = getConnectionProvider().getConnection();
    connection.cookies(getRequestCookies());
    return connection;
  }

  protected JsoupBasedSampler<T> setUrl(String newUrl) {
    this.url = newUrl;
    return this;
  }

}