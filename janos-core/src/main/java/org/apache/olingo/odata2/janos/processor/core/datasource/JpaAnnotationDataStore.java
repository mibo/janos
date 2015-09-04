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
package org.apache.olingo.odata2.janos.processor.core.datasource;

import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStore;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreException;
import org.apache.olingo.odata2.janos.processor.api.datasource.ReadOptions;
import org.apache.olingo.odata2.janos.processor.api.datasource.ReadResult;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationHelper;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationRuntimeException;

import javax.persistence.*;
import java.util.Collection;

/**
 *
 */
public class JpaAnnotationDataStore<T> implements DataStore<T> {

  public static final String DEFAULT_PERSISTENCE_NAME = "JpaAnnotationDataStorePersistence";
  public static final String PERSISTENCE_NAME = "JpaAnnotationDataStorePersistenceNameSystemProperty";

  private static final AnnotationHelper ANNOTATION_HELPER = new AnnotationHelper();

  protected Class<T> dataTypeClass;
  protected EntityManager entityManager;

  public static <T> DataStore<T> createInstance(Class<T> clz) {
    return createInstance(clz, DEFAULT_PERSISTENCE_NAME);
  }

  public static <T> DataStore<T> createInstance(Class<T> clz, String persistenceName) {
    return new JpaAnnotationDataStore<>(clz, persistenceName);
  }

  private JpaAnnotationDataStore(final Class<T> clz, String persistenceName) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceName);
    entityManager = emf.createEntityManager();
    this.dataTypeClass = clz;
  }

  @Override
  public Class<T> getDataTypeClass() {
    return dataTypeClass;
  }

  @Override
  public String getName() {
    return ANNOTATION_HELPER.extractEntityTypeName(dataTypeClass);
  }

  @Override
  public T createInstance() {
    try {
      return dataTypeClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new AnnotationRuntimeException("Unable to create instance of class '" + dataTypeClass + "'.", e);
    }
  }

  @Override
  public T create(T object) throws DataStoreException {
    EntityTransaction t = this.entityManager.getTransaction();
    try {
      t.begin();
      this.entityManager.persist(object);
      this.entityManager.flush();
      t.commit();
    } catch(Exception e) {
      if(t != null && t.isActive()) {
        t.rollback();
      }
    }
    
    return object;
  }

  @Override
  public T delete(T object) {
    EntityTransaction t = this.entityManager.getTransaction();
    try {
      t.begin();
      object = this.entityManager.merge(object);
      this.entityManager.remove(object);
      this.entityManager.flush();
      t.commit();

      return object;
    } catch(Exception e) {
      if(t != null && t.isActive()) {
        t.rollback();
      }
    }
    return null;
  }

  @Override
  public boolean isKeyEqualChecked(Object first, Object second) throws DataStoreException {
    return ANNOTATION_HELPER.keyMatch(first, second);
  }

  @Override
  public T read(T object) {
    Object key = ANNOTATION_HELPER.getValueForField(object, EdmKey.class);
    return this.entityManager.find(dataTypeClass, key);
  }

  @Override
  public Collection<T> read() {
    Query query = entityManager.createQuery("SELECT t FROM " + dataTypeClass.getSimpleName() + " t");
    return query.getResultList();
  }

  @Override
  public ReadResult<T> read(ReadOptions readOptions) {
    Query query = entityManager.createQuery("SELECT t FROM " + dataTypeClass.getSimpleName() + " t");
    return GenericReadResult.forResult(query.getResultList()).build();
  }

  @Override
  public T update(T object) {
    EntityTransaction t = this.entityManager.getTransaction();
    try {
      t.begin();
      object = this.entityManager.merge(object);
      this.entityManager.flush();
      t.commit();

      return object;
    } catch(Exception e) {
      if(t != null && t.isActive()) {
        t.rollback();
      }
    }
    return null;
  }
}
