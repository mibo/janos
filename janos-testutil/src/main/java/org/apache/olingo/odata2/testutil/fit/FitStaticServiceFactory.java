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
package org.apache.olingo.odata2.testutil.fit;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.apache.olingo.odata2.testutil.server.TestServer;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 *  
 */
public class FitStaticServiceFactory extends ODataServiceFactory {

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ODataCallback> T getCallback(final Class<T> callbackInterface) {
    if (callbackInterface.isAssignableFrom(FitErrorCallback.class)) {
      return (T) new FitErrorCallback();
    }

    return super.getCallback(callbackInterface);
  }

  private static Map<String, JanosServiceFactory> PORT_2_SERVICE = Collections
      .synchronizedMap(new HashMap<>());

  public static void unbindService(final String key) {
    PORT_2_SERVICE.remove(key);
  }

  public static void bindService(final TestServer server, final JanosServiceFactory service) {
    PORT_2_SERVICE.put(createId(server), service);
  }

  public static void unbindService(final TestServer server) {
    PORT_2_SERVICE.remove(createId(server));
  }

  @Override
  public ODataService createService(final ODataContext ctx) throws ODataException {

    assertNotNull(ctx);
    assertNotNull(ctx.getAcceptableLanguages());
    assertNotNull(ctx.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT));

    final Map<String, List<String>> requestHeaders = ctx.getRequestHeaders();
    final String host = requestHeaders.get("Host").get(0);

    String tmp[] = host.split(":", 2);
    String port = (tmp.length == 2 && tmp[1] != null) ? tmp[1] : "80";

    // access and validation in synchronized block
    final JanosServiceFactory service = PORT_2_SERVICE.get(port);
    return service.createService(ctx);
  }

  private static String createId(final TestServer server) {
    final URI endpoint = server.getEndpoint();
    if (endpoint == null) {
      throw new IllegalArgumentException("Got TestServer without endpoint.");
    }
    return "" + endpoint.getPort();
  }
}
