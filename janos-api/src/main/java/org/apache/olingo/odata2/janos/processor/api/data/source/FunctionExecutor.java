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

import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;

/**
 * A FunctionExecutor defines a class which is used to execute a function and
 * wants to get the DataStoreManager.
 *
 * Created by mibo on 23.08.15.
 */
public interface FunctionExecutor {

  /**
   * Init method must be called from a FunctionSource which prepare
   * a class which has methods with @EdmFunctionImport annotation
   *
   * @param dataStore data store manager which is used by DataSources
   */
  void init(DataStoreManager dataStore);
}
