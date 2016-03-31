package org.apache.olingo.odata2.janos.processor.api;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * The {@link JanosServiceFactory} is used to create thread safe {@link ODataService} instances
 * (which are linked with a {@link ODataContext}) for further request processing.
 */
public interface JanosServiceFactory {
  /**
   * Create a thread safe {@link ODataService} instance linked with the given
   * {@link ODataContext} for further request processing.
   *
   * @param context odata context of current request
   * @return new instance of a service to handle the request
   */
  ODataService createService(ODataContext context);
}
