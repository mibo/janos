/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.janos.processor.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.apache.olingo.odata2.janos.processor.api.JanosService;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.testutil.fit.AbstractFitTest;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.server.ServletType;
import org.junit.Ignore;
import org.junit.runners.Parameterized;

/**
 * Abstract base class for tests employing the reference scenario.
 * 
 */
@Ignore("no test methods")
//@RunWith(Parameterized.class)
public class AbstractRefTest extends AbstractFitTest {

  private static final Logger LOG = Logger.getLogger(AbstractRefTest.class);

  public AbstractRefTest(final ServletType servletType, String modelPackage) {
    super(servletType);
    LOG.trace("Test servlet " + servletType + " with model " + modelPackage);
  }

  protected static final String IMAGE_JPEG = "image/jpeg";
  protected static final String IMAGE_GIF = "image/gif";

  final static String MODEL_PACKAGE = "org.apache.olingo.odata2.janos.processor.ref.model";
  final static String MODEL_PACKAGE_JPA = "org.apache.olingo.odata2.janos.processor.ref.jpa.model";


  @Override
  protected ODataService createService() throws ODataException {
    return JanosService.createFor(MODEL_PACKAGE).build();
  }

  @Parameterized.Parameters
  public static List<Object[]> modelPackage() {
    // If desired this can be made dependent on runtime variables
    String[] modelPackages = new String[]{MODEL_PACKAGE, MODEL_PACKAGE_JPA};
    ServletType[] servletTypes = new ServletType[]{ServletType.ODATA_SERVLET, ServletType.JAXRS_SERVLET};
    Object[][] a = new Object[modelPackages.length * servletTypes.length][2];

    int count = 0;
    for (String modelPackage : modelPackages) {
      for (ServletType servletType : servletTypes) {
        a[count][0] = servletType;
        a[count++][1] = modelPackage;
      }
    }

    return Arrays.asList(a);
  }

  protected HttpResponse callUri(
      final ODataHttpMethod httpMethod, final String uri,
      final String additionalHeader, final String additionalHeaderValue,
      final String requestBody, final String requestContentType,
      final HttpStatusCodes expectedStatusCode) throws Exception {

    HttpRequestBase request =
        httpMethod == ODataHttpMethod.GET ? new HttpGet() :
            httpMethod == ODataHttpMethod.DELETE ? new HttpDelete() :
                httpMethod == ODataHttpMethod.POST ? new HttpPost() :
                    httpMethod == ODataHttpMethod.PUT ? new HttpPut() : new HttpPatch();
    request.setURI(URI.create(getEndpoint() + uri));
    if (additionalHeader != null) {
      request.addHeader(additionalHeader, additionalHeaderValue);
    }
    if (requestBody != null) {
      ((HttpEntityEnclosingRequest) request).setEntity(new StringEntity(requestBody));
      request.setHeader(HttpHeaders.CONTENT_TYPE, requestContentType);
    }

    final HttpResponse response = getHttpClient().execute(request);

    assertNotNull(response);
    assertEquals(expectedStatusCode.getStatusCode(), response.getStatusLine().getStatusCode());

    if (expectedStatusCode == HttpStatusCodes.OK) {
      assertNotNull(response.getEntity());
      assertNotNull(response.getEntity().getContent());
    } else if (expectedStatusCode == HttpStatusCodes.CREATED) {
      assertNotNull(response.getEntity());
      assertNotNull(response.getEntity().getContent());
      assertNotNull(response.getFirstHeader(HttpHeaders.LOCATION));
    } else if (expectedStatusCode == HttpStatusCodes.NO_CONTENT) {
      assertTrue(response.getEntity() == null || response.getEntity().getContent() == null);
    }

    return response;
  }

  protected HttpResponse callUri(final String uri, final String additionalHeader, final String additionalHeaderValue,
      final HttpStatusCodes expectedStatusCode) throws Exception {
    return callUri(ODataHttpMethod.GET, uri, additionalHeader, additionalHeaderValue, null, null, expectedStatusCode);
  }

  protected HttpResponse callUri(final String uri, final String additionalHeader, final String additionalHeaderValue)
      throws Exception {
    return callUri(ODataHttpMethod.GET, uri, additionalHeader, additionalHeaderValue, null, null, HttpStatusCodes.OK);
  }

  protected HttpResponse callUri(final String uri, final HttpStatusCodes expectedStatusCode) throws Exception {
    return callUri(uri, null, null, expectedStatusCode);
  }

  protected HttpResponse callUri(final String uri) throws Exception {
    return callUri(uri, HttpStatusCodes.OK);
  }

  protected void checkUri(final String uri) throws Exception {
    assertNotNull(getBody(callUri(uri)));
  }

  protected void badRequest(final String uri) throws Exception {
    final HttpResponse response = callUri(uri, HttpStatusCodes.BAD_REQUEST);
    assertNotNull(getBody(response));
  }

  protected void notFound(final String uri) throws Exception {
    final HttpResponse response = callUri(uri, HttpStatusCodes.NOT_FOUND);
    assertNotNull(getBody(response));
  }

  protected void deleteUri(final String uri, final HttpStatusCodes expectedStatusCode)
      throws Exception, AssertionError {
    final HttpResponse response = callUri(ODataHttpMethod.DELETE, uri, null, null, null, null, expectedStatusCode);
    if (expectedStatusCode != HttpStatusCodes.NO_CONTENT) {
      response.getEntity().getContent().close();
    }
  }

  protected void deleteUriOk(final String uri) throws Exception {
    deleteUri(uri, HttpStatusCodes.NO_CONTENT);
  }

  protected HttpResponse postUri(final String uri, final String requestBody, final String requestContentType,
      final HttpStatusCodes expectedStatusCode) throws Exception {
    return callUri(ODataHttpMethod.POST, uri, null, null, requestBody, requestContentType, expectedStatusCode);
  }

  protected HttpResponse postUri(final String uri, final String requestBody, final String requestContentType,
      final String additionalHeader, final String additionalHeaderValue, final HttpStatusCodes expectedStatusCode)
      throws Exception {
    return callUri(ODataHttpMethod.POST, uri, additionalHeader, additionalHeaderValue, requestBody, requestContentType,
        expectedStatusCode);
  }

  protected void putUri(final String uri,
      final String requestBody, final String requestContentType,
      final HttpStatusCodes expectedStatusCode) throws Exception {
    final HttpResponse response =
        callUri(ODataHttpMethod.PUT, uri, null, null, requestBody, requestContentType, expectedStatusCode);
    if (expectedStatusCode != HttpStatusCodes.NO_CONTENT) {
      response.getEntity().getContent().close();
    }
  }

  protected void putUri(final String uri, final String acceptHeader,
      final String requestBody, final String requestContentType,
      final HttpStatusCodes expectedStatusCode) throws Exception {
    final HttpResponse response =
        callUri(ODataHttpMethod.PUT, uri,
            org.apache.olingo.odata2.api.commons.HttpHeaders.ACCEPT, acceptHeader, requestBody, requestContentType,
            expectedStatusCode);
    if (expectedStatusCode != HttpStatusCodes.NO_CONTENT) {
      response.getEntity().getContent().close();
    }
  }

  protected String getBody(final HttpResponse response) throws Exception {
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertNotNull(response.getEntity().getContent());
    return StringHelper.inputStreamToString(response.getEntity().getContent());
  }

  protected void checkMediaType(final HttpResponse response, final String expectedMediaType) {
    assertEquals(expectedMediaType, response.getFirstHeader(HttpHeaders.CONTENT_TYPE).getValue());
  }

  protected void checkEtag(final HttpResponse response, final String expectedEtag) {
    assertNotNull(response.getFirstHeader(HttpHeaders.ETAG));
    final String entityTag = response.getFirstHeader(HttpHeaders.ETAG).getValue();
    assertNotNull(entityTag);
    assertEquals(expectedEtag, entityTag);
  }
}