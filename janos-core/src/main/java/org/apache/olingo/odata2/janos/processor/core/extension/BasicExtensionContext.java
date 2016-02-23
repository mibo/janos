package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.janos.processor.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mibo on 21.02.16.
 */
public final class BasicExtensionContext implements ExtensionContext {
  private Map<String, Object> parameters = new HashMap<>();

  @Override
  public ExtensionContext addParameter(String name, Object value) {
    parameters.put(name, value);
    return this;
  }

  @Override
  public Object getParameter(String name) {
    return parameters.get(name);
  }

  @Override
  public Object proceed() {
    return null;
  }
}
