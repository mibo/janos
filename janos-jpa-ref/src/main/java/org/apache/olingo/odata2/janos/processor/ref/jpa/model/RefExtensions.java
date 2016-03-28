package org.apache.olingo.odata2.janos.processor.ref.jpa.model;

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

  @Extension(entitySetNames="Employees", methods={Method.GET})
  public Object logReadAccess(ExtensionContext context) throws Exception {
    LOG.info("Start READ access for Employee.");
    UriInfo uri = context.getUriInfo();
    Object res = context.proceed();
    LOG.info("Finished READ access for Employee.");
    return res;
  }
}
