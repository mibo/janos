package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.processor.feature.ODataProcessorFeature;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
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
import java.util.Collection;
import java.util.List;

/**
 * Created by mibo on 27.02.16.
 */
public class ExtensionProcessor<T extends ODataProcessor> {

  private final ProcessorInvocationHandler handler;

  public ExtensionProcessor(T processor) {
    this.handler = new ProcessorInvocationHandler<>(this, processor);
  }

  public static <T extends ODataProcessor> Builder<T> wrap(T processor) {
    return new Builder<>(processor);
  }


  /**
   * Process call and checks for extensions
   *
   * @return
   * @throws Exception
   */
  public Object process() throws Exception {
    //
    ExtensionRegistry r = ExtensionRegistry.getInstance();
    // get uri info and map to according methods
    GetEntitySetUriInfo info = (GetEntitySetUriInfo) handler.getParameter(GetEntitySetUriInfo.class);
    if(info != null && info.getTargetEntitySet() != null) {
      ExtensionRegistry.ExtensionHolder ext = r.getExtension(Extension.Method.GET, info.getTargetEntitySet().getName());
      if(ext != null) {
        return ext.process(this);
      }
    }
    return handler.process();
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

    context.addParameter("~method", handler.getMethod());
    context.addParameter(ExtensionContext.PARA_URI_INFO, handler.getParameter(UriInfo.class));
    context.addParameter(ExtensionContext.PARA_ACCEPT_HEADER, handler.getParameter(String.class));
    context.addParameter(ExtensionContext.PARA_REQUEST_BODY, handler.getParameter(InputStream.class));

    return context;
  }

  InvocationHandler getInvocationHandler() {
    return handler;
  }

  public static class Builder<T extends ODataProcessor> {
    private final ExtensionProcessor processor;

    public Builder(T processor) {
//      this.processor = new ProcessorInvocationHandler<>(processor);
      this.processor = new ExtensionProcessor<>(processor);
    }

    public ODataProcessor finish() {
      return (ODataProcessor) Proxy.newProxyInstance(this.getClass().getClassLoader(),
          new Class[]{ODataProcessor.class}, processor.getInvocationHandler());

    }

    public Builder<T> extensions(Collection<Class<?>> extensions) {
      ExtensionRegistry r = ExtensionRegistry.getInstance();
      r.registerExtensions(extensions);
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
        invokeParameters = Arrays.copyOf(objects, objects.length);
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

    <P> void replaceInvokeParameter(P replacement) {
      if (replacement == null) {
        return;
      }

      List<Object> copy = new ArrayList<Object>();
      for (Object parameter : invokeParameters) {
        if (replacement.getClass() == parameter.getClass()) {
          copy.add(replacement);
        } else {
          copy.add(parameter);
        }
      }
      invokeParameters = copy.toArray();
    }

    T getWrappedInstance() {
      return this.wrappedInstance;
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
