/**
 * *****************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ***************************************************************************** 
 */
package org.apache.olingo.odata2.janos.processor.core.data.source;

import org.apache.olingo.odata2.api.annotation.edm.EdmKey;
import org.apache.olingo.odata2.api.annotation.edm.EdmMediaResourceContent;
import org.apache.olingo.odata2.api.annotation.edm.EdmMediaResourceMimeType;
import org.apache.olingo.odata2.api.annotation.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmMultiplicity;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.janos.processor.api.data.ReadOptions;
import org.apache.olingo.odata2.janos.processor.api.data.ReadResult;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStore;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreException;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationHelper;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationRuntimeException;
import org.apache.olingo.odata2.janos.processor.core.util.ClassHelper;

import java.lang.reflect.Field;
import java.util.*;

public class AnnotationDataSource implements DataSource {

  private static final AnnotationHelper ANNOTATION_HELPER = new AnnotationHelper();

  private final DataStoreManager dataStoreManager;

  public AnnotationDataSource(final Collection<Class<?>> annotatedClasses, final DataStoreManager dataStoreManager)
      throws ODataException {
    this.dataStoreManager = dataStoreManager;
    
    init(annotatedClasses);
  }

  public AnnotationDataSource(final String packageToScan, final DataStoreManager dataStoreManager)
          throws ODataException {
    this.dataStoreManager = dataStoreManager;

    List<Class<?>> foundClasses = ClassHelper.loadClasses(packageToScan, ANNOTATION_HELPER::isEdmAnnotated);

    init(foundClasses);
  }

  @SuppressWarnings("unchecked")
  private void init(final Collection<Class<?>> annotatedClasses) throws ODataException {
    try {
      for (Class<?> clz : annotatedClasses) {
        String entitySetName = ANNOTATION_HELPER.extractEntitySetName(clz);
        if(entitySetName != null) {
          dataStoreManager.grantDataStore(entitySetName, clz);
        } else if (!ANNOTATION_HELPER.isEdmAnnotated(clz)) {
          throw new ODataException("Found not annotated class during DataStore initialization of type: "
              + clz.getName());
        }
      }
    } catch (DataStoreException e) {
      throw new ODataException("Error in DataStore initialization with message: " + e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> DataStore<T> getDataStore(final Class<T> clazz) throws DataStoreException {
    String entitySetName = ANNOTATION_HELPER.extractEntitySetName(clazz);
    return dataStoreManager.getDataStore(entitySetName, clazz);
  }

  @Override
  public ReadResult<?> readData(final EdmEntitySet entitySet, ReadOptions readOptions) throws ODataNotImplementedException,
      ODataNotFoundException, EdmException, ODataApplicationException {

    DataStore<Object> holder = getDataStore(entitySet);
    if (holder != null) {
      return holder.read(readOptions);
    }

    throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
  }

  @Override
  public Object readData(final EdmEntitySet entitySet, final Map<String, Object> keys)
      throws ODataNotFoundException, EdmException, ODataApplicationException {

    DataStore<Object> store = getDataStore(entitySet);
    if (store != null) {
      Object keyInstance = store.createInstance();
      ANNOTATION_HELPER.setKeyFields(keyInstance, keys);

      Object result = store.read(keyInstance);
      if (result != null) {
        return result;
      }
    }

    throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
  }

  @Override
  public Object readRelatedData(final EdmEntitySet sourceEntitySet, final Object sourceData,
      final EdmEntitySet targetEntitySet,
      final Map<String, Object> targetKeys)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

    DataStore<?> sourceStore = dataStoreManager.getDataStore(sourceEntitySet.getName());
    DataStore<?> targetStore = dataStoreManager.getDataStore(targetEntitySet.getName());

    AnnotationHelper.AnnotatedNavInfo navInfo = ANNOTATION_HELPER.getCommonNavigationInfo(
        sourceStore.getDataTypeClass(), targetStore.getDataTypeClass());
    final Field sourceField;
    if (navInfo.getFromField() == null) {
      sourceField = navInfo.getToField();
    } else {
      sourceField = navInfo.getFromField();
    }
    if (sourceField == null) {
      throw new AnnotationRuntimeException("Missing source field for related data (sourceStore='" + sourceStore
          + "', targetStore='" + targetStore + "').");
    }

    List<Object> resultData = readResultData(targetStore, sourceData, sourceField, navInfo);
    return extractResultData(targetStore, targetKeys, navInfo, resultData);
  }

  /**
   * Read the result data from the target store based on <code>sourceData</code> and <code>sourceField</code>
   * 
   * @param targetStore
   * @param sourceData
   * @param sourceField
   * @return
   * @throws DataStoreException
   */
  private List<Object> readResultData(final DataStore<?> targetStore, final Object sourceData, 
          final Field sourceField, final AnnotationHelper.AnnotatedNavInfo navInfo)
      throws DataStoreException {
    Object navigationInstance = getValue(sourceField, sourceData);
    if (navigationInstance == null) {
      return Collections.emptyList();
    }
    
    List<Object> resultData = new ArrayList<>();
    for (Object targetInstance : targetStore.read()) {
      if (navigationInstance instanceof Collection) {
        Map<String, Object> keyName2Value = 
                ANNOTATION_HELPER.getValueForAnnotatedFields(sourceData, EdmKey.class);
        Field toField = navInfo.getToField();
        Object backInstance = ClassHelper.getFieldValue(targetInstance, toField);
        boolean keyMatch = ANNOTATION_HELPER.keyMatch(backInstance, keyName2Value);
        if(keyMatch) {
          resultData.add(targetInstance);
        }
      } else if (targetStore.isKeyEqualChecked(targetInstance, navigationInstance)) {
        resultData.add(targetInstance);
      }
    }
    return resultData;
  }

  /**
   * Extract the <code>result data</code> from the <code>resultData</code> list based on
   * <code>navigation information</code> and <code>targetKeys</code>.
   * 
   * @param targetStore
   * @param targetKeys
   * @param navInfo
   * @param resultData
   * @return
   * @throws DataStoreException
   */
  private Object extractResultData(final DataStore<?> targetStore, final Map<String, Object> targetKeys,
      final AnnotationHelper.AnnotatedNavInfo navInfo, final List<Object> resultData) throws DataStoreException {
    if (navInfo.getToMultiplicity() == EdmMultiplicity.MANY) {
      if (targetKeys.isEmpty()) {
        return resultData;
      } else {
        Object keyInstance = targetStore.createInstance();
        ANNOTATION_HELPER.setKeyFields(keyInstance, targetKeys);
        for (Object result : resultData) {
          if (targetStore.isKeyEqualChecked(result, keyInstance)) {
            return result;
          }
        }
        return null;
      }
    } else {
      if (resultData.isEmpty()) {
        return null;
      }
      return resultData.get(0);
    }
  }

  @Override
  public BinaryData readBinaryData(final EdmEntitySet entitySet, final Object mediaLinkEntryData)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

    Object data = ANNOTATION_HELPER.getValueForField(mediaLinkEntryData, EdmMediaResourceContent.class);
    Object mimeType = ANNOTATION_HELPER.getValueForField(mediaLinkEntryData, EdmMediaResourceMimeType.class);

    if (data == null && mimeType == null) {
      DataStore<Object> dataStore = getDataStore(entitySet);
      Object readEntry = dataStore.read(mediaLinkEntryData);
      if (readEntry != null) {
        data = ANNOTATION_HELPER.getValueForField(readEntry, EdmMediaResourceContent.class);
        mimeType = ANNOTATION_HELPER.getValueForField(readEntry, EdmMediaResourceMimeType.class);
      }
    }

    return new BinaryData((byte[]) data, String.valueOf(mimeType));
  }

  @Override
  public Object newDataObject(final EdmEntitySet entitySet)
      throws ODataNotImplementedException, EdmException, ODataApplicationException {

    DataStore<Object> dataStore = getDataStore(entitySet);
    if (dataStore != null) {
      return dataStore.createInstance();
    }

    throw new AnnotationRuntimeException("No DataStore found for entitySet with name: " + entitySet.getName());
  }

  @Override
  public void writeBinaryData(final EdmEntitySet entitySet, final Object mediaEntityInstance,
      final BinaryData binaryData)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

    try {
      DataStore<Object> dataStore = getDataStore(entitySet);
      Object readEntry = dataStore.read(mediaEntityInstance);
      if (readEntry == null) {
        throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
      } else {
        ANNOTATION_HELPER.setValueForAnnotatedField(
            mediaEntityInstance, EdmMediaResourceContent.class, binaryData.getData());
        ANNOTATION_HELPER.setValueForAnnotatedField(
            mediaEntityInstance, EdmMediaResourceMimeType.class, binaryData.getMimeType());
      }
    } catch (AnnotationHelper.ODataAnnotationException e) {
      throw new AnnotationRuntimeException("Invalid media resource annotation at entity set '" + entitySet.getName()
          + "' with message '" + e.getMessage() + "'.", e);
    }
  }

  /**
   * <p>Updates a single data object identified by the specified entity set and key fields of
   * the data object.</p>
   * @param entitySet the {@link EdmEntitySet} the object must correspond to
   * @param data the data object of the new entity
   * @return updated data object instance
   * @throws org.apache.olingo.odata2.api.exception.ODataNotImplementedException
   * @throws org.apache.olingo.odata2.api.edm.EdmException
   * @throws org.apache.olingo.odata2.api.exception.ODataApplicationException
   */
  public Object updateData(final EdmEntitySet entitySet, final Object data)
      throws EdmException, ODataApplicationException {

    DataStore<Object> dataStore = getDataStore(entitySet);
    return dataStore.update(data);
  }

  @Override
  public void deleteData(final EdmEntitySet entitySet, final Map<String, Object> keys)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
    DataStore<Object> dataStore = getDataStore(entitySet);
    Object keyInstance = dataStore.createInstance();
    ANNOTATION_HELPER.setKeyFields(keyInstance, keys);
    dataStore.delete(keyInstance);
  }

  @Override
  public Object createData(final EdmEntitySet entitySet, final Object data)
      throws ODataNotImplementedException, EdmException, ODataApplicationException {

    DataStore<Object> dataStore = getDataStore(entitySet);
    return dataStore.create(data);
  }

  @Override
  public void deleteRelation(final EdmEntitySet sourceEntitySet, final Object sourceData,
      final EdmEntitySet targetEntitySet,
      final Map<String, Object> targetKeys)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
    throw new ODataNotImplementedException(ODataNotImplementedException.COMMON);
  }

  @Override
  public void writeRelation(final EdmEntitySet sourceEntitySet, final Object sourceEntity,
      final EdmEntitySet targetEntitySet,
      final Map<String, Object> targetEntityValues)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {
    // get common data
    DataStore<Object> sourceStore = dataStoreManager.getDataStore(sourceEntitySet.getName());
    DataStore<Object> targetStore = dataStoreManager.getDataStore(targetEntitySet.getName());

    AnnotationHelper.AnnotatedNavInfo commonNavInfo = ANNOTATION_HELPER.getCommonNavigationInfo(
        sourceStore.getDataTypeClass(), targetStore.getDataTypeClass());

    // get and validate source fields
    Field sourceField = commonNavInfo.getFromField();
    if (sourceField == null) {
      throw new AnnotationRuntimeException("Missing source field for related data (sourceStore='" + sourceStore
          + "', targetStore='" + targetStore + "').");
    }

    // get related target entity
    Object targetEntity = targetStore.createInstance();
    ANNOTATION_HELPER.setKeyFields(targetEntity, targetEntityValues);
    targetEntity = targetStore.read(targetEntity);

    // set at source
    setValueAtNavigationField(sourceEntity, sourceField, targetEntity);
    // set at target
    Field targetField = commonNavInfo.getToField();
    if (targetField != null) {
      setValueAtNavigationField(targetEntity, targetField, sourceEntity);
    }
  }

  /**
   * Set (Multiplicity != *) or add (Multiplicity == *) <code>value</code> at <code>field</code>
   * of <code>instance</code>.
   * 
   * @param instance
   * @param field
   * @param value
   * @throws EdmException
   */
  private void setValueAtNavigationField(final Object instance, final Field field, final Object value) {
    Class<?> fieldTypeClass = field.getType();
    if (Collection.class.isAssignableFrom(fieldTypeClass)) {
      @SuppressWarnings("unchecked")
      Collection<Object> collection = (Collection<Object>) ANNOTATION_HELPER.getValueForField(
          instance, field.getName(), EdmNavigationProperty.class);
      if (collection == null) {
        collection = new ArrayList<>();
        setValue(instance, field, collection);
      }
      collection.add(value);
    } else if (fieldTypeClass.isArray()) {
      throw new AnnotationRuntimeException("Write relations for internal used arrays is not supported.");
    } else {
      setValue(instance, field, value);
    }
  }

  /**
   * Returns corresponding DataStore for EdmEntitySet or if no data store is registered an
   * AnnotationRuntimeException is thrown.
   * Never returns NULL.
   * 
   * @param entitySet for which the corresponding DataStore is returned
   * @return a DataStore object
   * @throws EdmException
   * @throws AnnotationRuntimeException if no DataStore is found
   */
  private DataStore<Object> getDataStore(final EdmEntitySet entitySet) throws EdmException {
    final String name = entitySet.getName();
    DataStore<Object> dataStore = dataStoreManager.getDataStore(name);
    if (dataStore == null) {
      throw new AnnotationRuntimeException("No DataStore found for entity set '" + entitySet + "'.");
    }
    return dataStore;
  }

  private Object getValue(final Field field, final Object instance) {
    try {
      boolean access = field.isAccessible();
      field.setAccessible(true);
      Object value = field.get(instance);
      field.setAccessible(access);
      return value;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new AnnotationRuntimeException("Error for getting value of field '"
          + field + "' at instance '" + instance + "'.", e);
    }
  }

  private void setValue(final Object instance, final Field field, final Object value) {
    try {
      boolean access = field.isAccessible();
      field.setAccessible(true);
      field.set(instance, value);
      field.setAccessible(access);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new AnnotationRuntimeException("Error for setting value of field: '"
          + field + "' at instance: '" + instance + "'.", e);
    }
  }
}
