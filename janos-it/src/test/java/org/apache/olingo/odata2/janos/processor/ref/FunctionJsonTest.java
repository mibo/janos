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

import junit.framework.Assert;
import org.apache.http.HttpResponse;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.junit.Test;

/**
 * Tests employing the reference scenario reading the service document in JSON format.
 * 
 */
public class FunctionJsonTest extends AbstractRefTest {
  public FunctionJsonTest(String modelPackage) {
    super(modelPackage);
  }

  @Test
  public void citySearch() throws Exception {
    final HttpResponse response = callUri("citySearch?cityName='pol'&$format=json");
     checkMediaType(response, HttpContentType.APPLICATION_JSON);
    String body = getBody(response);

    System.out.println("Model package: " + modelPackageUnderTest);
    System.out.println(body);
    System.out.println("---");

    Assert.assertTrue(jsonDataResponseContains(body, "RefScenario.c_City"));
    Assert.assertTrue(jsonDataResponseContains(body, "8392"));
    Assert.assertTrue(jsonDataResponseContains(body, "Northpole"));
    Assert.assertTrue("Body\n---[\n" + body + "\n]---\n is not as expected.",
        jsonDataResponseContains(body, "\"type\":\"RefScenario.c_City\"}," +
        "\"PostalCode\":\"8392\",\"CityName\":\"Northpole„\""));
  }

  private boolean jsonDataResponseContains(final String content, final String containingValue) {
    return content.contains(containingValue);
//    return content.matches("\\{\"d\":\\{\".*\":\\[.*"
//        + containingValue + ".*\"\\]\\}\\}");
  }
}