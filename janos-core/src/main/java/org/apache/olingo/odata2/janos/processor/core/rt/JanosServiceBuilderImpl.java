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

import org.apache.olingo.odata2.janos.processor.api.JanosService.JanosServiceBuilder;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataSource;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreFactory;
import org.apache.olingo.odata2.janos.processor.api.datasource.ValueAccess;
import org.apache.olingo.odata2.janos.processor.core.ListsProcessor;
import org.apache.olingo.odata2.janos.processor.core.datasource.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.datasource.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.datasource.DualDataStoreFactory;
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
  private ValueAccess valueAccess;
  private DataStoreFactory dataStoreFactory;
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

  public JanosServiceBuilder with(DataStoreFactory dataStoreFactory) {
    this.dataStoreFactory = dataStoreFactory;
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

  public ODataService build() throws ODataException {
    if(!annotatedClasses.isEmpty()) {
      edmProvider = new AnnotationEdmProvider(annotatedClasses);
      dataSource = new AnnotationDataSource(annotatedClasses, grantDataStoreFactory());
    } else if(modelPackage != null) {
      edmProvider = new AnnotationEdmProvider(modelPackage);
      dataSource = new AnnotationDataSource(modelPackage, grantDataStoreFactory());
    } else {
      throw new RuntimeException();
    }

    if(valueAccess == null) {
      valueAccess = new AnnotationValueAccess();
    }

    // Edm via Annotations and ListProcessor via AnnotationDS with AnnotationsValueAccess
    return RuntimeDelegate.createODataSingleProcessorService(edmProvider,
        new ListsProcessor(dataSource, valueAccess));
  }

  private DataStoreFactory grantDataStoreFactory() {
    if(dataStoreFactory == null) {
      dataStoreFactory = new DualDataStoreFactory();
      dataStoreFactory.setDefaultProperty(DataStoreFactory.KEEP_PERSISTENT, DEFAULT_PERSISTENCE);
    }
    return dataStoreFactory;
  }
}
