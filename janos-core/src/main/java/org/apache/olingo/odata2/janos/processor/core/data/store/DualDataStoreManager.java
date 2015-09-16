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

package org.apache.olingo.odata2.janos.processor.core.data.store;

import org.apache.olingo.odata2.janos.processor.api.data.store.DataStore;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreException;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DualDataStoreManager implements DataStoreManager {

  private final Map<String, String> properties = new HashMap<>();

  private final Map<String, DataStore<Object>> dataStores = new HashMap<>();


  @Override
  public <T> DataStore<T> createDataStore(Class<T> clz) throws DataStoreException {
    return createDataStore(clz, properties);
  }

  @Override
  public <T> DataStore<T> createDataStore(Class<T> clz, Map<String, String> properties) throws DataStoreException {
    boolean keepPersistent = Boolean.parseBoolean(properties.get(KEEP_PERSISTENT));
    return createInstance(clz, keepPersistent);
  }

  @Override
  public <T> DataStore<T> grantDataStore(String name, Class<T> clz) throws DataStoreException {
    return grantDataStore(name, clz, properties);
  }

  @Override
  public <T> DataStore<T> grantDataStore(String name, Class<T> clz, Map<String, String> properties) throws DataStoreException {
    DataStore<T> ds = getDataStore(name, clz);
    if(ds == null) {
      ds = createDataStore(clz);
      dataStores.put(name, (DataStore<Object>) ds);
    }
    return ds;
  }

  @Override
  public DataStore<Object> getDataStore(String name) {
    return dataStores.get(name);
  }

  @Override
  public <T> DataStore<T> getDataStore(String name, Class<T> clz) throws DataStoreException {
    DataStore<?> ds = getDataStore(name);
    if(ds != null && clz != ds.getDataTypeClass()) {
      throw new DataStoreException("Unable to cast required class.");
    }
    return (DataStore<T>) ds;
  }

  @Override
  public void setDefaultProperty(String name, String value) {
    properties.put(name, value);
  }
  
  public <T> DataStore<T>  createInstance(Class<T> clz, boolean keepPersistent) throws DataStoreException {
    if(isJpaAnnotated(clz)) {
      String persistenceName = System.getProperty(JpaAnnotationDataStore.PERSISTENCE_NAME);
      if(persistenceName == null) {
        return JpaAnnotationDataStore.createInstance(clz);
      }
      return JpaAnnotationDataStore.createInstance(clz, persistenceName);
    }
    return InMemoryDataStore.createInMemory(clz, keepPersistent);
  }

  private boolean isJpaAnnotated(Class<?> clz) {
    return clz.getAnnotation(Entity.class) != null;
  }
}
