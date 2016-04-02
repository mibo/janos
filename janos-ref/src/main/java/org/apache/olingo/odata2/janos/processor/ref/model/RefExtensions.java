package org.apache.olingo.odata2.janos.processor.ref.model;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
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
  private static final String EXTENSION_TEST = "ExtensionTest";

  @Extension(entitySetNames="Employees", methods={Method.GET})
  public Object logReadAccess(ExtensionContext context) throws Exception {
    UriInfo uriInfo = context.getUriInfo();
    ODataResponse res;
    if(uriInfo.getKeyPredicates().isEmpty()) {
      LOG.info("Start READ access for Employees.");
      res = context.proceed();
      res = ODataResponse.fromResponse(res).header(EXTENSION_TEST, "READ EMPLOYEES SET").build();
      LOG.info("Finished READ access for Employees.");
    } else {
      LOG.info("Start READ access for Employee.");
      res = context.proceed();
      res = ODataResponse.fromResponse(res).header(EXTENSION_TEST, "READ EMPLOYEE").build();
      LOG.info("Finished READ access for Employee.");
    }
    return res;
  }

  @Extension(entitySetNames="Employees", methods={Method.POST})
  public Object createEmployee(ExtensionContext context) throws Exception {
    LOG.info("Start CREATE for Employee.");
    ODataResponse res = context.proceed();
    res = ODataResponse.fromResponse(res).header(EXTENSION_TEST, "CREATE").build();
    LOG.info("Finished CREATE for Employee.");
    return res;
  }

  @Extension(entitySetNames="Employees", methods={Method.PUT})
  public Object updateEmployee(ExtensionContext context) throws Exception {
    LOG.info("Start UPDATE for Employee.");
    ODataResponse res = context.proceed();
    res = ODataResponse.fromResponse(res).header(EXTENSION_TEST, "UPDATE").build();
    LOG.info("Finished UPDATE for Employee.");
    return res;
  }

  @Extension(entitySetNames="Employees", methods={Method.DELETE})
  public Object deleteEmployee(ExtensionContext context) throws Exception {
    LOG.info("Start DELETE for Employee.");
    ODataResponse res = context.proceed();
    res = ODataResponse.fromResponse(res).header(EXTENSION_TEST, "DELETE").build();
    LOG.info("Finished DELETE for Employee.");
    return res;
  }
}
