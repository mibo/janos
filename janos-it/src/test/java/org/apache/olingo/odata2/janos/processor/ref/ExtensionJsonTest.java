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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Tests employing the reference scenario reading the service document in JSON format.
 * 
 */
public class ExtensionJsonTest extends AbstractRefTest {
  String anEmployee = "{\n" +
//        "    \"EmployeeId\": \"1\",\n" +
      "    \"EmployeeName\": \"first Employee\",\n" +
      "    \"Age\": 42,\n" +
      "    \"ImageUrl\": \"http://localhost/image/first.png\",\n" +
      "    \"EntryDate\": null,\n" +
      "    \"Location\": {\n" +
      "      \"__metadata\": {\n" +
      "        \"type\": \"RefScenario.c_Location\"\n" +
      "      },\n" +
      "      \"Country\": \"Nörge\",\n" +
      "      \"City\": {\n" +
      "        \"__metadata\": {\n" +
      "          \"type\": \"RefScenario.c_City\"\n" +
      "        },\n" +
      "        \"PostalCode\": \"8392\",\n" +
      "        \"CityName\": \"Northpole„\"\n" +
      "      }\n" +
      "    }}";
  private static final String EXTENSION_TEST = "ExtensionTest";

  public ExtensionJsonTest(String modelPackage) {
    super(modelPackage);
  }

  @Test
  public void readEmployees() throws Exception {
    final HttpResponse response = callUri("Employees?$format=json");
    checkMediaType(response, HttpContentType.APPLICATION_JSON);
    String body = getBody(response);

    Header functionTest = response.getFirstHeader(EXTENSION_TEST);
    assertEquals("READ EMPLOYEES SET", functionTest.getValue());
    assertTrue(jsonDataResponseContains(body, "Employees"));
  }

  @Test
  public void readEmployee() throws Exception {
    final HttpResponse response = callUri("Employees('1')?$format=json");
    checkMediaType(response, HttpContentType.APPLICATION_JSON);
    String body = getBody(response);

    Header functionTest = response.getFirstHeader(EXTENSION_TEST);
    assertEquals("READ EMPLOYEE", functionTest.getValue());
    assertTrue(jsonDataResponseContains(body, "Employees"));
  }

  @Test
  public void createEmployee() throws Exception {
    final String requestBody = "{\"TEST\":\"createme\"}";
    final HttpResponse response = createPost("Employees")
        .addHeader(HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON)
        .requestBody(requestBody, HttpContentType.APPLICATION_JSON)
        .executeValidated(HttpStatusCodes.CREATED);
    checkMediaType(response, HttpContentType.APPLICATION_JSON);
    String body = getBody(response);

    Header functionTest = response.getFirstHeader(EXTENSION_TEST);
    assertEquals("CREATE", functionTest.getValue());
    assertTrue(jsonDataResponseContains(body, "Employees"));
  }

  @Test
  public void updateEmployee() throws Exception {
    final HttpResponse response = createPut("Employees('1')")
        .addHeader(HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON)
        .requestBody(anEmployee, HttpContentType.APPLICATION_JSON)
        .executeValidated(HttpStatusCodes.NO_CONTENT);

    Header functionTest = response.getFirstHeader(EXTENSION_TEST);
    assertEquals("UPDATE", functionTest.getValue());
  }

  @Test
  public void deleteEmployee() throws Exception {
    final HttpResponse response = createDelete("Employees('1')")
        .addHeader(HttpHeaders.ACCEPT, HttpContentType.APPLICATION_JSON)
        .executeValidated(HttpStatusCodes.NO_CONTENT);

    Header functionTest = response.getFirstHeader(EXTENSION_TEST);
    assertEquals("DELETE", functionTest.getValue());
  }

  private boolean jsonDataResponseContains(final String content, final String containingValue) {
    return content.contains(containingValue);
  }
}