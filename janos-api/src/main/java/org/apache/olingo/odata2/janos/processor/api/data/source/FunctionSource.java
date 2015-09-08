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
package org.apache.olingo.odata2.janos.processor.api.data.source;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;

import java.util.Map;

/**
 * A FunctionSource contains functionality to execute functions for
 * given EdmFunctionImports.
 *
 * Created by mibo on 23.08.15.
 */
public interface FunctionSource {

//  FunctionExecutor getFunctionExecutor(String functionName);

  /**
   * <p>Retrieves data for the specified function import and key.</p>
   * <p>This method is called also for function imports that have defined in
   * their metadata an other HTTP method than <code>GET</code>.</p>
   * @param function the requested {@link EdmFunctionImport}
   * @param parameters the parameters of the function import
   * as map of parameter names to parameter values
   * @param keys the key of the returned entity set, as map of key names to key values,
   * if the return type of the function import is a collection of entities
   * (optional)
   * @return the requested data object, either a list or a single object;
   * if the function import's return type is of type <code>Binary</code>,
   * the returned object(s) must be of type {@link BinaryData}
   */
  Object executeFunction(EdmFunctionImport function, Map<String, Object> parameters, Map<String, Object> keys)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException;

}
