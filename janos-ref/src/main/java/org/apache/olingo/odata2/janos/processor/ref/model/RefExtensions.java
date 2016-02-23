package org.apache.olingo.odata2.janos.processor.ref.model;

import org.apache.olingo.odata2.janos.processor.api.extension.Extension.Method;
import org.apache.olingo.odata2.janos.processor.api.extension.Extension;
import org.apache.olingo.odata2.janos.processor.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mibo on 21.02.16.
 */
public class RefExtensions {

  private static final Logger LOG = LoggerFactory.getLogger(RefExtensions.class);

  @Extension(entitySetNames="Employee", methods={Method.GET})
  public Object logReadAccess(ExtensionContext context) {
    LOG.info("Start READ access for Employee.");
    Object res = context.proceed();
    LOG.info("Finished READ access for Employee.");
    return res;
  }
}
