package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.janos.processor.api.extension.ExtensionContext;
import org.omg.CORBA.portable.InputStream;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mibo on 21.02.16.
 */
public final class BasicExtensionContext implements ExtensionContext {
  private Map<String, Object> parameters = new HashMap<>();
  private final ExtensionProcessor extensionProcessor;

  public BasicExtensionContext(ExtensionProcessor extensionProcessor) {
    this.extensionProcessor = extensionProcessor;
  }

  @Override
  public ExtensionContext addParameter(String name, Object value) {
    parameters.put(name, value);
    return this;
  }

  @Override
  public Object getParameter(String name) {
    return parameters.get(name);
  }

  public <T> T getParameter(String name, Class<T> clazz) {
    Object o = getParameter(name);
    if(clazz.isInstance(o)) {
      return (T) o;
    }
    return null;
  }

  @Override
  public UriInfo getUriInfo() {
    return getParameter(PARA_URI_INFO, UriInfo.class);
  }

  @Override
  public String getAcceptHeader() {
    return getParameter(PARA_ACCEPT_HEADER, String.class);
  }

  @Override
  public InputStream getRequestBody() {
    return getParameter(PARA_REQUEST_BODY, InputStream.class);
  }

  @Override
  public ODataResponse proceed() throws Exception {
    return extensionProcessor.proceed();
  }
}
