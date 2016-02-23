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

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.processor.part.*;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;
import org.apache.olingo.odata2.janos.processor.api.JanosService;
import org.apache.olingo.odata2.janos.processor.api.JanosService.JanosServiceBuilder;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.api.data.source.FunctionSource;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.core.DataSourceProcessor;
import org.apache.olingo.odata2.janos.processor.core.JanosODataProcessor;
import org.apache.olingo.odata2.janos.processor.core.JanosODataService;
import org.apache.olingo.odata2.janos.processor.core.ODataProcessor;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationFunctionSource;
import org.apache.olingo.odata2.janos.processor.core.data.access.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.data.store.DualDataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.edm.AnnotationEdmProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

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
  private Collection<Class<?>> extensions = new ArrayList<>();

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

  public JanosServiceBuilder extensions(Collection<Class<?>> extensions) {
    this.extensions = extensions;
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

//    ODataProcessor.class.getInterfaces()
    DataSourceProcessor p = new DataSourceProcessor(dataSource, valueAccess, functionSource);
    ProcessorInvocationHandler handler = new ProcessorInvocationHandler(p);
    Object proxyInstance = Proxy.newProxyInstance(this.getClass().getClassLoader(),
        JanosODataProcessor.class.getInterfaces(), handler);

//    return RuntimeDelegate.createODataSingleProcessorService(edmProvider, (ODataSingleProcessor) proxyInstance);
    return new JanosODataService(edmProvider, (ODataProcessor) proxyInstance);
  }

  /**
   * InvocationHandler which is used as proxy for the Processor method.
   */
  private static class ProcessorInvocationHandler implements InvocationHandler {
    private final Object wrappedInstance;
    private Method invokeMethod;
    private Object[] invokeParameters;

    public ProcessorInvocationHandler(Object wrappedInstance) {
      this.wrappedInstance = wrappedInstance;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      // XXX: change
      if (ODataProcessor.class.isAssignableFrom(method.getDeclaringClass())) {
        invokeMethod = method;
        invokeParameters = Arrays.copyOf(objects, objects.length);
      } else {
        throw new RuntimeException("Invalid class '" + method.getDeclaringClass() +
            "' can not wrapped for asynchronous processing.");
      }

      return null;
    }

    Object process() throws InvocationTargetException, IllegalAccessException {
      return invokeMethod.invoke(wrappedInstance, invokeParameters);
    }

    <P> void replaceInvokeParameter(P replacement) {
      if (replacement == null) {
        return;
      }

      List<Object> copy = new ArrayList<Object>();
      for (Object parameter : invokeParameters) {
        if (replacement.getClass() == parameter.getClass()) {
          copy.add(replacement);
        } else {
          copy.add(parameter);
        }
      }
      invokeParameters = copy.toArray();
    }

    Object getWrappedInstance() {
      return this.wrappedInstance;
    }

    <P> P getParameter(Class<P> parameterClass) {
      for (Object parameter : invokeParameters) {
        if (parameter != null && parameterClass == parameter.getClass()) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }
  }

}
