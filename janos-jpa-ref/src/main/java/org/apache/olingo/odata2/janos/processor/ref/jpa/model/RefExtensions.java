package org.apache.olingo.odata2.janos.processor.ref.jpa.model;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.janos.processor.api.extension.Extension;
import org.apache.olingo.odata2.janos.processor.api.extension.Extension.Method;
import org.apache.olingo.odata2.janos.processor.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mibo on 21.02.16.
 */
public class RefExtensions {

  private static final Logger LOG = LoggerFactory.getLogger(RefExtensions.class);

  @Extension(entitySetNames="Employees", methods={Method.GET, Method.POST, Method.PUT})
  public Object logAllAccess(ExtensionContext context) throws Exception {
    String mapped = mapMethod(context.getRequestType());
    LOG.info("Start " + mapped + " access for Employee.");
    ODataResponse res = context.proceed();
    res = ODataResponse.fromResponse(res).header("ExtensionTest", mapped).build();
    LOG.info("Finished " + mapped + " access for Employee.");
    return res;
  }

  private String mapMethod(Method requestType) {
    switch (requestType) {
      case GET: return "READ";
      case POST: return "CREATE";
      case PUT: return "UPDATE";
      case DELETE: return "DELETE";
    }
    if(requestType == null) {
      return "NULL request type";
    }
    return "Not mappable request type: " + requestType.name();
  }
}
