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
package org.apache.olingo.odata2.janos.processor.api;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;

import java.lang.reflect.Constructor;
import java.util.Collection;

/**
 * AnnotationServiceFactory which provides an AnnotationService which handles java beans (classes)
 * annotated with annotation from <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
 * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>).
 */
public abstract class JanosService {

  private static final String IMPLEMENTATION =
      "org.apache.olingo.odata2.janos.processor.core.rt.JanosServiceBuilderImpl";
  private static final String EDM_PROVIDER_IMPLEMENTATION =
      "org.apache.olingo.odata2.janos.processor.core.edm.AnnotationEdmProvider";

  /**
   * Create a runtime delegate instance from the core library. The core
   * library (org.apache.olingo.odata2.janos.processor.core) needs to be included into the classpath
   * of the using application.
   * @return implementation instance
   */
  private static JanosServiceBuilder getInstance() {
    return getInstance(IMPLEMENTATION, JanosServiceBuilder.class);
  }

  private static <T> T getInstance(String className, Class<T> clz) {
    return getInstance(className, clz, null);
  }

  private static <T> T getInstance(String className, Class<T> clz, Object[] parameters) {
    try {
      final Class<?> clazz = Class.forName(className);
      /*
       * We explicitly do not use the singleton pattern to keep the server state free
       * and avoid class loading issues also during hot deployment.
       */
      if(parameters == null || parameters.length == 0) {
        final Object object = clazz.newInstance();
        return clz.cast(object);
      } else {
        Class[] paraClasses = toClass(parameters);
        Constructor<?> ctor = clazz.getConstructor(paraClasses);
        final Object object = ctor.newInstance(parameters);
        return clz.cast(object);
      }
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Class[] toClass(Object[] parameters) {
    Class[] classes = new Class[parameters.length];
    for (int i = 0; i < classes.length; i++) {
      classes[i] = parameters[i].getClass();
      if(Collection.class.isAssignableFrom(classes[i])) {
        classes[i] = Collection.class;
      }
    }
    return classes;
  }

  /**
   * Create an <code>EdmProvider</code> based on annotated classes in given package (and sub-packages).
   *
   * @param packageToScan package (and sub-packages) which are scanned for annotated classes
   * @return according <code>EdmProvider</code>
   */
  public static EdmProvider createEdmProvider(String packageToScan) {
    return getInstance(EDM_PROVIDER_IMPLEMENTATION, EdmProvider.class, new Object[]{packageToScan});
  }

  /**
   * Create an <code>EdmProvider</code> based on given annotated classes.
   *
   * @param annotatedClasses all classes which are annotated and are used as Edm
   * @return according <code>EdmProvider</code>
   */
  public static EdmProvider createEdmProvider(Collection<Class<?>> annotatedClasses) {
    return getInstance(EDM_PROVIDER_IMPLEMENTATION, EdmProvider.class, new Object[]{annotatedClasses});
  }

  /**
   * Interface to be implemented for an instance of a {@link JanosService.JanosServiceBuilder} which
   * provides an {@link ODataService} based on annotation from
   * <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
   * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>).
   */
  public interface JanosServiceBuilder {
    /**
     * Create an {@link ODataService} which is based on an EDM and Processor which are using the annotations from
     * <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
     * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>) to define the model and access the data.
     *
     * @param modelPackage package name which is scanned for annotated classes
     * @return service an
     * based on on an EDM and Processor which are using annotations
     * for model definition and data access.
     * @throws ODataException if an error during initialization occurs
     */
    JanosServiceBuilder createFor(String modelPackage) throws ODataException;

    /**
     * Create an {@link ODataService} which is based on an EDM and Processor which are using the annotations from
     * <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
     * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>) to define the model and access the data.
     *
     * @param annotatedClasses the annotated classes
     * @return service an
     * based on on an EDM and Processor which are using annotations
     * for model definition and data access.
     * @throws ODataException if an error during initialization occurs
     */
    JanosServiceBuilder createFor(Collection<Class<?>> annotatedClasses) throws ODataException;

    /**
     * Sets the DataStoreManager used by the JanosService (ODataService)
     * @param dataStore DataStoreManager to be used
     * @return this builder (fluent builder)
     */
    JanosServiceBuilder with(DataStoreManager dataStore);

    /**
     * Sets the ValueAccess used by the JanosService (ODataService)
     * @param valueAccess ValueAccess to be used
     * @return this builder (fluent builder)
     */
    JanosServiceBuilder with(ValueAccess valueAccess);

    /**
     * Sets the DataSource used by the JanosService (ODataService)
     * @param dataSource DataSource to be used
     * @return this builder (fluent builder)
     */
    JanosServiceBuilder with(DataSource dataSource);


    /**
     * Set classes which have with @Extension annotated methods.
     *
     * @param extensions classes with @Extension annotated methods.
     * @return this builder (fluent builder)
     */
    JanosServiceBuilder extensions(Collection<Class<?>> extensions);

    /**
     * Finish building and build/create the configured JanosService (ODataService)
     * @return an instance of a JanosService (ODataService)
     * @throws ODataException the o data exception
     */
    ODataService build() throws ODataException;
  }

  /**
   * Create an {@link ODataService} which is based on an EDM and Processor which are using the annotations from
   * <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
   * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>) to define the model and access the data.
   *
   * @param modelPackage package name which is scanned for annotated classes
   * @return service an
   * based on on an EDM and Processor which are using annotations
   * for model definition and data access.
   * @throws ODataException if an error during initialization occurs
   */
  public static JanosServiceBuilder createFor(final String modelPackage) throws ODataException {
    return getInstance().createFor(modelPackage);
  }

  /**
   * Create an {@link ODataService} which is based on an EDM and Processor which are using the annotations from
   * <code>org.apache.olingo.olingo-odata2-api-annotation</code> module
   * (see package <code>org.apache.olingo.odata2.api.annotation.edm</code>) to define the model and access the data.
   *
   * @param annotatedClasses the annotated classes
   * @return service an
   * based on on an EDM and Processor which are using annotations
   * for model definition and data access.
   * @throws ODataException if an error during initialization occurs
   */
  public static JanosServiceBuilder createFor(final Collection<Class<?>> annotatedClasses)
      throws ODataException {
    return getInstance().createFor(annotatedClasses);
  }
}
