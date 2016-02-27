package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.janos.processor.core.JanosODataProcessor;
import org.apache.olingo.odata2.janos.processor.core.ODataProcessor;
import org.apache.olingo.odata2.api.processor.feature.ODataProcessorFeature;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mibo on 27.02.16.
 */
public class ExtensionProcessor {
  public static <T extends ODataProcessor> Builder<T> wrap(T processor) {
    return new Builder<>(processor);
  }

  public static class Builder<T extends ODataProcessor> {
    private final ProcessorInvocationHandler<T> processor;

    public Builder(T processor) {
      this.processor = new ProcessorInvocationHandler<>(processor);
    }

    public ODataProcessor finish() {
      return (ODataProcessor) Proxy.newProxyInstance(this.getClass().getClassLoader(),
          new Class[]{ODataProcessor.class}, processor);

    }
  }

  /**
   * InvocationHandler which is used as proxy for the Processor method.
   */
  private static class ProcessorInvocationHandler<T extends ODataProcessor> implements InvocationHandler {
    private final T wrappedInstance;
    private Method invokeMethod;
    private Object[] invokeParameters;

    public ProcessorInvocationHandler(T wrappedInstance) {
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

      return process();
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
        if (parameter != null && parameterClass == parameter.getClass()) {
          return parameterClass.cast(parameter);
        }
      }
      return null;
    }
  }

}
