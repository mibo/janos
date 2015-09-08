/*
 * Copyright 2014 The Apache Software Foundation.
 *
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
 */

package org.apache.olingo.odata2.janos.processor.api.data.store;

import java.util.Map;

/**
 * Manager (and factory) for DataStore instances.
 */
public interface DataStoreManager {
  String KEEP_PERSISTENT = "KEEP_PERSISTENT";

  /**
   * Set default properties which are used for all methods without explicit properties parameter.
   *
   * @param name name of property
   * @param value value of property
   */
  void setDefaultProperty(String name, String value);

  /**
   * Create a DataStore without managing (keeping a reference to it).
   *
   * @param clz class for which the DataStore is created
   * @param <T> type of class for which the DataStore is created
   * @return the created (and not managed) DataStore
   * @throws DataStoreException
   */
  <T> DataStore<T> createDataStore(Class<T> clz) throws DataStoreException;

  /**
   * Create a DataStore without managing (keeping a reference to it).
   *
   * @param clz class for which the DataStore is created
   * @param <T> type of class for which the DataStore is created
   * @param properties properties for creation
   * @return the created (and not managed) DataStore
   * @throws DataStoreException if something goes wrong
   */
  <T> DataStore<T> createDataStore(Class<T> clz, Map<String, String> properties) throws DataStoreException;

  /**
   * Create a DataStore and managing it (keeping a reference to it).
   * If a DataStore with given name already exists this will be returned.
   *
   * @param name unique name for the DataStore
   * @param clz class for which the DataStore is created
   * @param <T> type of class for which the DataStore is created
   * @return the created (and managed) DataStore
   * @throws DataStoreException if something goes wrong
   */
  <T> DataStore<T> grantDataStore(String name, Class<T> clz) throws DataStoreException;

  /**
   * Create a DataStore and managing it (keeping a reference to it).
   * If a DataStore with given name already exists this will be returned.
   *
   * @param name unique name for the DataStore
   * @param clz class for which the DataStore is created
   * @param <T> type of class for which the DataStore is created
   * @param properties properties for creation
   * @return the created (and managed) DataStore
   * @throws DataStoreException if something goes wrong
   */
  <T> DataStore<T> grantDataStore(String name, Class<T> clz, Map<String, String> properties) throws DataStoreException;

  /**
   * Get a reference to an already existing (managed) DataStore.
   *
   * @param <T> type of class for which the DataStore is created
   * @param name name of the DataStore which is requested
   * @param clz class for which the DataStore is created
   * @return the created (and managed) DataStore or <code>NULL</code> if no according DataStore exists
   * @throws DataStoreException if something goes wrong
   */
  <T> DataStore<T> getDataStore(String name, Class<T> clz) throws DataStoreException;

  /**
   * Get a reference to an already existing (managed) DataStore.
   *
   * @param name name of the DataStore which is requested
   * @return the created (and managed) DataStore or <code>NULL</code> if no according DataStore exists
   */
  DataStore<Object> getDataStore(String name);
}
