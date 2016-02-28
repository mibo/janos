package org.apache.olingo.odata2.janos.processor.core.extension;

import org.apache.olingo.odata2.janos.processor.api.extension.Extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExtensionRegistry {

  private static class Holder {
    private final static ExtensionRegistry INSTANCE = new ExtensionRegistry();
  }

  private Map<String, ExtensionHolder> extensionHolders = new HashMap<>();
  public static final String SEP = "_";

  private ExtensionRegistry() {}

  public static ExtensionRegistry getInstance() {
    return Holder.INSTANCE;
  }

  public void registerExtensions(Collection<Class<?>> clazzes) {
    for (Class<?> clazz : clazzes) {
      registerExtension(clazz);
    }
  }

  public void registerExtension(Class<?> clazz) {
    for (Method method : clazz.getDeclaredMethods()) {
      for (Annotation annotation : method.getDeclaredAnnotations()) {
        if (annotation instanceof Extension) {
          registerExtension(clazz, method, (Extension) annotation);
        }
      }
    }
  }

  private void registerExtension(Class<?> clazz, Method method, Extension extension) {
      for (String entitySetName : extension.entitySetNames()) {
        for (Extension.Method type : extension.methods()) {
          try {
            String extId = createExtensionId(type, entitySetName);
            Object instance = clazz.newInstance(); // TODO: create INSTANCE
            extensionHolders.put(extId, new ExtensionHolder(entitySetName, type, instance, method));
          } catch (IllegalAccessException | InstantiationException e) {
            // TODO
            e.printStackTrace();
          }
        }
      }
  }

  private String createExtensionId(Extension.Method requestType, String entitySetName) {
    return entitySetName + SEP + requestType.name();
  }

  public boolean isExtensionRegistered(Extension.Method requestType, String entitySetName) {
    return extensionHolders.containsKey(createExtensionId(requestType, entitySetName));
  }

  public ExtensionHolder getExtension(Extension.Method requestType, String entitySetName) {
    return extensionHolders.get(createExtensionId(requestType, entitySetName));
  }

  static class ExtensionHolder {
    private final String entitySetName;
    private final Extension.Method type;
    private final Method method;
    private final Object instance;

    public ExtensionHolder(String entitySetName, Extension.Method type, Object instance, Method method) {
      this.entitySetName = entitySetName;
      this.type = type;
      this.instance = instance;
      this.method = method;
    }

    public String getEntitySetName() {
      return entitySetName;
    }

    public Extension.Method getType() {
      return type;
    }

    public Object getInstance() {
      return instance;
    }

    public Method getMethod() {
      return method;
    }

    public Object process(ExtensionProcessor extProcessor) throws InvocationTargetException, IllegalAccessException {
//    return handler.process();
//      Object INSTANCE = extensionHolder.getInstance();
//      Method method = extensionHolder.getMethod();

      BasicExtensionContext context = new BasicExtensionContext(extProcessor);
      return method.invoke(instance, context);
      //
    }
  }
}
