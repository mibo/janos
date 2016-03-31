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
package de.mirb.olingo.janos.sampleds;

import de.mirb.olingo.janos.sampleds.ds.FileStore;
import de.mirb.olingo.janos.sampleds.model.Address;
import de.mirb.olingo.janos.sampleds.model.Person;
import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataDebugCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.janos.processor.api.JanosService;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * ODataServiceFactory implementation based on ListProcessor
 * in combination with Annotation-Support-Classes for EdmProvider, DataSource and ValueAccess.
 */
public class SampleDsServiceFactory extends ODataServiceFactory {

  /**
   * Instance holder for all annotation relevant instances which should be used as singleton
   * instances within the ODataApplication (ODataService)
   */
  private static class AnnotationInstances {
    final static Set<Class<?>> ANNOTATED_MODEL_CLASSES = new HashSet<Class<?>>();
    static {
      ANNOTATED_MODEL_CLASSES.add(Address.class);
      ANNOTATED_MODEL_CLASSES.add(Person.class);
    }
    final static JanosServiceFactory ANNOTATION_ODATA_SERVICE;

    static {
      try {
        ANNOTATION_ODATA_SERVICE = JanosService.createFor(ANNOTATED_MODEL_CLASSES)
            .with(new FileStore())
            .build();
        initializeSampleData();
      } catch (ODataApplicationException ex) {
        throw new RuntimeException("Exception during sample data generation.", ex);
      } catch (ODataException ex) {
        throw new RuntimeException("Exception during data source initialization generation.", ex);
      }
    }

    private static void initializeSampleData() {

    }
  }

  @Override
  public ODataService createService(final ODataContext context) throws ODataException {
    // Edm via Annotations and ListProcessor via AnnotationDS with AnnotationsValueAccess
    return AnnotationInstances.ANNOTATION_ODATA_SERVICE.createService(context);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends ODataCallback> T getCallback(final Class<T> callbackInterface) {
    return (T) (callbackInterface.isAssignableFrom(ScenarioErrorCallback.class)
        ? new ScenarioErrorCallback() : callbackInterface.isAssignableFrom(ODataDebugCallback.class)
            ? new ScenarioDebugCallback() : null);
  }

  /*
   * Helper classes and methods
   */

  /**
   * Callback class to enable debugging.
   */
  private final class ScenarioDebugCallback implements ODataDebugCallback {
    public boolean isDebugEnabled() {
      return true;
    }
  }

  /**
   * Callback class for error handling.
   */
  private class ScenarioErrorCallback implements ODataErrorCallback {

    private final Logger LOG = LoggerFactory.getLogger(ScenarioErrorCallback.class);

    public ODataResponse handleError(final ODataErrorContext context) throws ODataApplicationException {
      if (context.getHttpStatus() == HttpStatusCodes.INTERNAL_SERVER_ERROR) {
        LOG.error("Internal Server Error", context.getException());
      }

      return EntityProvider.writeErrorDocument(context);
    }

  }
}
