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

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory.JanosServiceFactoryBuilder;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.source.FunctionSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.JanosODataServiceFactoryFactory;
import org.apache.olingo.odata2.janos.processor.core.data.access.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationFunctionSource;
import org.apache.olingo.odata2.janos.processor.core.data.store.DualDataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.janos.processor.core.extension.ExtensionRegistry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * AnnotationServiceFactoryInstance (ODataServiceFactory) implementation based on ListProcessor
 * in combination with Annotation-Support-Classes for EdmProvider, DataSource and ValueAccess.
 */
public class JanosServiceFactoryBuilderImpl implements JanosServiceFactoryBuilder {
  private static final String DEFAULT_PERSISTENCE = Boolean.TRUE.toString();
  private DataSource dataSource;
  private FunctionSource functionSource;
  private ValueAccess valueAccess;
  private DataStoreManager dataStoreManager;
  private String modelPackage;
  private Collection<Class<?>> annotatedClasses = new ArrayList<>();
  private Collection<Class<?>> extensions = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public JanosServiceFactoryBuilder createFor(final String modelPackage) throws ODataException {
    this.modelPackage = modelPackage;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JanosServiceFactoryBuilder createFor(final Collection<Class<?>> annotatedClasses) throws ODataException {
    this.annotatedClasses = annotatedClasses;
    return this;
  }

  public JanosServiceFactoryBuilder with(DataStoreManager dataStoreManager) {
    this.dataStoreManager = dataStoreManager;
    return this;
  }

  public JanosServiceFactoryBuilder with(ValueAccess valueAccess) {
    this.valueAccess = valueAccess;
    return this;
  }

  public JanosServiceFactoryBuilder with(DataSource dataSource) {
    this.dataSource = dataSource;
    return this;
  }

  public JanosServiceFactoryBuilder with(FunctionSource functionSource) {
    this.functionSource = functionSource;
    return this;
  }

  public JanosServiceFactoryBuilder extensions(Collection<Class<?>> extensions) {
    this.extensions = extensions;
    return this;
  }

  public JanosServiceFactory build() throws ODataException {
    if(dataStoreManager == null) {
      dataStoreManager = new DualDataStoreManager();
      dataStoreManager.setDefaultProperty(DataStoreManager.KEEP_PERSISTENT, DEFAULT_PERSISTENCE);
    }

    AnnotationEdmProvider edmProvider;
    if(!annotatedClasses.isEmpty()) {
      edmProvider = new AnnotationEdmProvider(annotatedClasses);
      dataSource = new AnnotationDataSource(annotatedClasses, dataStoreManager);
      functionSource = AnnotationFunctionSource.with(annotatedClasses).with(dataStoreManager).build();
    } else if(modelPackage != null) {
      edmProvider = new AnnotationEdmProvider(modelPackage);
      dataSource = new AnnotationDataSource(modelPackage, dataStoreManager);
      functionSource = AnnotationFunctionSource.with(modelPackage).with(dataStoreManager).build();
    } else {
      throw new RuntimeException("Unable to build " + JanosServiceFactory.class);
    }

    if(valueAccess == null) {
      valueAccess = new AnnotationValueAccess();
    }

    ExtensionRegistry registry = ExtensionRegistry.getInstance().registerExtensions(extensions);

    return new JanosODataServiceFactoryFactory(edmProvider, dataSource, valueAccess, functionSource, registry);
  }


}
