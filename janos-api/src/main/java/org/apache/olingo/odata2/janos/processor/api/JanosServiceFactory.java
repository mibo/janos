package org.apache.olingo.odata2.janos.processor.api;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.processor.ODataContext;

/**
 * Created by mibo on 30.03.16.
 */
public interface JanosServiceFactory {
  ODataService createService(ODataContext context);

}
