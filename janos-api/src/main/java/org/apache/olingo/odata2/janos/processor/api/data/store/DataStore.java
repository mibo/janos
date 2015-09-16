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

import org.apache.olingo.odata2.janos.processor.api.data.ReadOptions;
import org.apache.olingo.odata2.janos.processor.api.data.ReadResult;

import java.util.Collection;

/**
 * The interface Data store.
 * @param <T>  the type parameter
 * @author michael
 */
public interface DataStore<T> {

  /**
   * Get class of objects which can be stored by this DataStore instance.
   *
   * @return class of objects which can be stored by this DataStore instance.
   */
  Class<T> getDataTypeClass();

  /**
   * Get name of this DataStore instance.
   *
   * @return name of this DataStore instance.
   */
  String getName();

  /**
   * Create a new object instance of the object type which can be stored by this DataStore instance.
   *
   * @return new object instance
   */
  T createInstance();

  /**
   * Store given object as new object in the DataStore
   *
   * @param object new object to be stored
   * @return the stored object
   * @throws DataStoreException the data store exception
   */
  T create(final T object) throws DataStoreException;

  /**
   * Read object which is key equal to given object (based on #isKeyEqualChecked method).
   * If no according object can be found <code>null</code> is returned
   *
   * @param object object with key fields set
   * @return according object or <code>null</code>
   */
  T read(final T object);

//  ReadResult<T> readOptimized(final T object);

  /**
   * Read all object of this DataStore.
   *
   * @return all object of this DataStore.
   */
  Collection<T> read();

  /**
   * Read read result.
   *
   * @param readOptions the read options
   * @return the read result
   */
  ReadResult<T> read(ReadOptions readOptions);

//  ReadResult<Collection<T>> readOptimzied();

  /**
   * Update object which is key equal to given object (based on #isKeyEqualChecked method).
   * If no according object can be found <code>null</code> is returned and nothing is updated.
   *
   * @param object object with key fields set
   * @return according object or <code>null</code>
   */
  T update(final T object);

  /**
   * Delete object which is key equal to given object (based on #isKeyEqualChecked method).
   * If no according object can be found <code>null</code> is returned and nothing is updated.
   *
   * @param object object with key fields set
   * @return according object or <code>null</code>
   */
  T delete(final T object);

  /**
   * Are the key values equal for both instances.
   * If all compared key values are <code>null</code> this also means equal.
   * Before object (keys) are compared it is validated that both object instance are NOT null
   * and that both are from the same class as this {@link DataStore} (see {@link #getDataTypeClass()}).
   *
   * @param first first instance to check for key equal
   * @param second second instance to check for key equal
   * @return  <code>true</code> if object instance have equal keys set.
   * @throws DataStoreException the data store exception
   */
  boolean isKeyEqualChecked(Object first, Object second) throws DataStoreException;

}
