package org.apache.olingo.odata2.janos.processor.api.extension;

/**
 * Created by michael on 23.02.16.
 */
public interface ExtensionContext {
  ExtensionContext addParameter(String name, Object value);

  Object getParameter(String name);

  Object proceed();
}
