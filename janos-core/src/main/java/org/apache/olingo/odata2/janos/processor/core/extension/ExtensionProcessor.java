package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.feature.ODataProcessorFeature;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.janos.processor.api.extension.Extension;
import org.apache.olingo.odata2.janos.processor.api.extension.ExtensionContext;
import org.apache.olingo.odata2.janos.processor.core.ODataProcessor;
import org.omg.CORBA.portable.InputStream;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mibo on 27.02.16.
 */
public class ExtensionProcessor<T extends ODataProcessor> {

  private final ProcessorInvocationHandler handler;
  private final ExtensionRegistry extensionRegistry;
  private final ODataContext context;

  ExtensionProcessor(T processor, ExtensionRegistry extensionRegistry, ODataContext context) {
    this.extensionRegistry = extensionRegistry;
    this.handler = new ProcessorInvocationHandler<>(this, processor);
    this.context = context;
  }

  public static <T extends ODataProcessor> Builder<T> wrap(T processor) {
    return new Builder<>(processor);
  }

  /**
   * Wrapped processor call and checks for extensions
   *
   * @return
   * @throws Exception
   */
  Object process() throws Exception {
    //
    // get uri info and map to according methods
    UriInfo info = (UriInfo) handler.getParameter(UriInfo.class);
    if(info != null && info.getTargetEntitySet() != null) {
      if(context != null) {
        String httpMethod = context.getHttpMethod();
        return dispatch(httpMethod, info);
      }
    }
    return handler.process();
  }

  private Object dispatch(String httpMethod, UriInfo info)
      throws InvocationTargetException, IllegalAccessException, EdmException {

    Extension.Method method = mapMethod(httpMethod);
    ExtensionRegistry.ExtensionHolder ext = extensionRegistry.getExtension(method,
        info.getTargetEntitySet().getName());
    if(ext != null) {
      return ext.process(this);
    }
    return handler.process();
  }

  private Extension.Method mapMethod(String httpMethod) {
    switch (httpMethod) {
      case "GET": return Extension.Method.GET;
      case "POST": return Extension.Method.POST;
      case "PUT": return Extension.Method.PUT;
      case "DELETE": return Extension.Method.DELETE;
    }
    throw new RuntimeException("Not mappable/supported HTTP method: " + httpMethod);
  }

  /**
   * Proceed (process) to wrapped processor instance
   *
   * @return
   * @throws Exception
   */
  public ODataResponse proceed() throws Exception {
    Object o = this.handler.process();
    if(o instanceof ODataResponse) {
      return (ODataResponse) o;
    }
    // TODO: change with 'better/concrete' exception
    throw new RuntimeException("Could not cast to ODataResponse");
  }

  public ExtensionContext createContext() {
    BasicExtensionContext context = new BasicExtensionContext(this);

    context.addParameter(ExtensionContext.PARA_REQUEST_TYPE, handler.getMethod());
    context.addParameter(ExtensionContext.PARA_URI_INFO, handler.getParameter(UriInfo.class));
    context.addParameter(ExtensionContext.PARA_ACCEPT_HEADER, handler.getParameter(String.class));
    context.addParameter(ExtensionContext.PARA_REQUEST_BODY, handler.getParameter(InputStream.class));

    return context;
  }

  InvocationHandler getInvocationHandler() {
    return handler;
  }

  public static class Builder<T extends ODataProcessor> {
    private T processor;
    private ExtensionRegistry extensionRegistry;

    Builder(T processor) {
      this.processor = processor;
    }

    public ODataProcessor finish(ODataContext context) {
      if(extensionRegistry == null) {
        extensionRegistry = ExtensionRegistry.getInstance();
      }
      ExtensionProcessor extProc = new ExtensionProcessor<>(processor, extensionRegistry, context);
      return (ODataProcessor) Proxy.newProxyInstance(this.getClass().getClassLoader(),
          new Class[]{ODataProcessor.class}, extProc.getInvocationHandler());
    }

    public Builder<T> extensions(ExtensionRegistry extensionRegistry) {
      this.extensionRegistry = extensionRegistry;
      return this;
    }
  }

  /**
   * InvocationHandler which is used as proxy for the Processor method.
   */
  private static class ProcessorInvocationHandler<T extends ODataProcessor> implements InvocationHandler {
    private final T wrappedInstance;
    private final ExtensionProcessor<T> extensionProcessor;
    private Method invokeMethod;
    private Object[] invokeParameters;

    public ProcessorInvocationHandler(ExtensionProcessor<T> extensionProcessor, T wrappedInstance) {
      this.extensionProcessor = extensionProcessor;
      this.wrappedInstance = wrappedInstance;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      // XXX: change
      if (isValid(method)) {
        invokeMethod = method;
        if (objects != null) {
        	invokeParameters = Arrays.copyOf(objects, objects.length);
        } else {
        	invokeParameters = new Object[0];
        }
      } else {
        throw new RuntimeException("Invalid class '" + method.getDeclaringClass() +
            "' can not wrapped for asynchronous processing.");
      }

      return extensionProcessor.process();
    }

    private boolean isValid(Method method) {
      return org.apache.olingo.odata2.api.processor.ODataProcessor.class.isAssignableFrom(method.getDeclaringClass())
          || ODataProcessorFeature.class.isAssignableFrom(method.getDeclaringClass());
    }


    Object process() throws InvocationTargetException, IllegalAccessException {
      return invokeMethod.invoke(wrappedInstance, invokeParameters);
    }

    <P> P getParameter(Class<P> parameterClass) {
      for (Object parameter : invokeParameters) {
        if (parameter != null && parameterClass.isAssignableFrom(parameter.getClass())) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }

    /**
     * Method which was invoked (and wrapped by this ProcessorInvocationHandler instance).
     * @return Method which was invoked
     */
    public Object getMethod() {
      return this.invokeMethod;
    }
  }

}
