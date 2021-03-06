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
package io.wcm.qa.glnm.sampling.jsoup;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wcm.qa.glnm.exceptions.GaleniumException;
import io.wcm.qa.glnm.sampling.jsoup.base.JsoupBasedSampler;

/**
 * Uses {@link org.jsoup.Jsoup} to fetch HTML from a URL.
 *
 * @since 3.0.0
 */
public class JsoupDocumentSampler extends JsoupBasedSampler<Document> {

  private static final Logger LOG = LoggerFactory.getLogger(JsoupDocumentSampler.class);

  /**
   * <p>Constructor for JsoupDocumentSampler.</p>
   *
   * @param url to fetch HTML from
   * @since 3.0.0
   */
  public JsoupDocumentSampler(String url) {
    super(url);
  }

  /** {@inheritDoc} */
  @Override
  public Document freshSample() {
    return getDocument();
  }

  private void rethrowException(IOException ex) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Could not fetch Document: " + getUrl(), ex);
    }
    throw new GaleniumException("When trying to fetch URL: '" + getUrl() + "'", ex);
  }

  /**
   * @return document from URL or rethrows {@link IOException} as {@link GaleniumException}
   */
  protected Document getDocument() {
    try {
      @SuppressWarnings("PMD.CloseResource")
      Connection connection = getJsoupConnection();
      if (connection == null) {
        throw new GaleniumException("cannot get document from null connection.");
      }
      if (LOG.isInfoEnabled()) {
        LOG.info("fetching document from '" + connection.request().url() + "'");
      }
      Document document = connection.get();
      return document;
    }
    catch (HttpStatusException ex) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("STATUS: " + ex.getStatusCode());
      }
      rethrowException(ex);
    }
    catch (IOException ex) {
      rethrowException(ex);
    }
    // will never be reached, but static code analysis doesn't know
    return null;
  }

}
