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
package org.apache.olingo.odata2.janos.processor.core.rt;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.olingo.odata2.janos.processor.api.JanosService;
import org.apache.olingo.odata2.janos.processor.api.JanosService.JanosServiceBuilder;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataSource;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.api.datasource.FunctionSource;
import org.apache.olingo.odata2.janos.processor.api.datasource.ValueAccess;
import org.apache.olingo.odata2.janos.processor.core.DataSourceProcessor;
import org.apache.olingo.odata2.janos.processor.core.datasource.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.datasource.AnnotationFunctionSource;
import org.apache.olingo.odata2.janos.processor.core.datasource.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.datasource.DualDataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;

/**
 * AnnotationServiceFactoryInstance (ODataServiceFactory) implementation based on ListProcessor
 * in combination with Annotation-Support-Classes for EdmProvider, DataSource and ValueAccess.
 */
public class JanosServiceBuilderImpl implements JanosServiceBuilder {
  private static final String DEFAULT_PERSISTENCE = Boolean.TRUE.toString();
  private EdmProvider edmProvider;
  private DataSource dataSource;
  private FunctionSource functionSource;
  private ValueAccess valueAccess;
  private DataStoreManager dataStoreManager;
  private String modelPackage;
  private Collection<Class<?>> annotatedClasses = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public JanosServiceBuilder createFor(final String modelPackage) throws ODataException {
    this.modelPackage = modelPackage;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JanosServiceBuilder createFor(final Collection<Class<?>> annotatedClasses) throws ODataException {
    this.annotatedClasses = annotatedClasses;
    return this;
  }

  public JanosServiceBuilder with(DataStoreManager dataStoreManager) {
    this.dataStoreManager = dataStoreManager;
    return this;
  }

  public JanosServiceBuilder with(ValueAccess valueAccess) {
    this.valueAccess = valueAccess;
    return this;
  }

  public JanosServiceBuilder with(DataSource dataSource) {
    this.dataSource = dataSource;
    return this;
  }

  public JanosServiceBuilder with(FunctionSource functionSource) {
    this.functionSource = functionSource;
    return this;
  }

  public ODataService build() throws ODataException {
    if(dataStoreManager == null) {
      dataStoreManager = new DualDataStoreManager();
      dataStoreManager.setDefaultProperty(DataStoreManager.KEEP_PERSISTENT, DEFAULT_PERSISTENCE);
    }

    if(!annotatedClasses.isEmpty()) {
      edmProvider = new AnnotationEdmProvider(annotatedClasses);
      dataSource = new AnnotationDataSource(annotatedClasses, dataStoreManager);
      functionSource = AnnotationFunctionSource.with(annotatedClasses).with(dataStoreManager).build();
    } else if(modelPackage != null) {
      edmProvider = new AnnotationEdmProvider(modelPackage);
      dataSource = new AnnotationDataSource(modelPackage, dataStoreManager);
      functionSource = AnnotationFunctionSource.with(modelPackage).with(dataStoreManager).build();
    } else {
      throw new RuntimeException("Unable to build " + JanosService.class);
    }

    if(valueAccess == null) {
      valueAccess = new AnnotationValueAccess();
    }

    // Edm via Annotations and ListProcessor via AnnotationDS with AnnotationsValueAccess
    return RuntimeDelegate.createODataSingleProcessorService(edmProvider,
        new DataSourceProcessor(dataSource, valueAccess, functionSource));
  }
}
