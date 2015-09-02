package org.apache.olingo.odata2.janos.processor.core.datasource;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.api.datasource.FunctionExecutor;
import org.apache.olingo.odata2.janos.processor.api.datasource.FunctionSource;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationHelper;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationRuntimeException;
import org.apache.olingo.odata2.janos.processor.core.util.ClassHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 */
public class AnnotationFunctionSource implements FunctionSource {

  private AnnotationHelper annotationHelper = new AnnotationHelper();
  private Map<String, FunctionHolder> functionHolders = new HashMap<>();
  private DataStoreManager dataStoreManager;

  private AnnotationFunctionSource() { /* private ctor because of builder */ }

  private void init(Collection<Class<?>> annotatedClasses) {
    for (Class<?> annotatedClass : annotatedClasses) {
      List<Method> methods = annotationHelper.getAnnotatedMethods(annotatedClass, EdmFunctionImport.class, false);

      for (Method method : methods) {
        initFunctionHolder(annotatedClass, method);
      }
    }
  }

  private void initFunctionHolder(Class<?> annotatedClass, Method method) {
    try {
      EdmFunctionImport efi = method.getAnnotation(EdmFunctionImport.class);
      String name = efi.name();
      if(name == null || name.isEmpty()) {
        name = method.getName();
      }

      LinkedHashMap<String, EdmFunctionImportParameter> parameters = extractParameters(method.getParameters());

      FunctionHolder holder = new FunctionHolder();
      holder.method = method;
      holder.executor = annotatedClass.newInstance();
      holder.functionImport = efi;
      holder.functionParameters = parameters;
      //
      callInitMethods(holder.executor);

      functionHolders.put(name, holder);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }

  }

  private void callInitMethods(Object functionInstance) {
    if(functionInstance instanceof FunctionExecutor) {
      ((FunctionExecutor) functionInstance).init(dataStoreManager);
    }
  }

  private LinkedHashMap<String, EdmFunctionImportParameter> extractParameters(Parameter[] parameters) {
    LinkedHashMap<String, EdmFunctionImportParameter> result = new LinkedHashMap<>();
    for (Parameter parameter : parameters) {
      EdmFunctionImportParameter efip = parameter.getAnnotation(EdmFunctionImportParameter.class);
      result.put(efip.name(), efip);
    }
    return result;
  }

//  @Override
//  public FunctionExecutor getFunctionExecutor(String functionName) {
//    FunctionHolder holder = functionHolders.get(functionName);
//    if(holder == null) {
//      return null;
//    }
//    return holder.executor;
//  }

  @Override
  public Object executeFunction(org.apache.olingo.odata2.api.edm.EdmFunctionImport function,
                                Map<String, Object> parameters, Map<String, Object> keys)
      throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

    return executeFunction(function.getName(), parameters);
  }

  public Object executeFunction(String functionName, Map<String, Object> parameters) {
    FunctionHolder holder = functionHolders.get(functionName);
    if(holder != null) {
      Object[] exParameters = mapParameters(holder, parameters);
      return holder.execute(exParameters);
    }
    return null;
  }

  private Object[] mapParameters(FunctionHolder holder, Map<String, Object> parameters) {
    List<Object> resParameters = new ArrayList<>();
    for (Map.Entry<String, EdmFunctionImportParameter> s : holder.functionParameters.entrySet()) {
      Object paraInstance = parameters.get(s.getKey());
      resParameters.add(paraInstance);
    }

    return resParameters.toArray(new Object[resParameters.size()]);
  }


  static class FunctionHolder {
    Object executor;
    Method method;
    EdmFunctionImport functionImport;
    LinkedHashMap<String, EdmFunctionImportParameter> functionParameters;

    public Object execute(Object... parameters) {
      try {
        return method.invoke(executor, parameters);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new AnnotationRuntimeException("Exception in methods access.", e);
      }
    }
  }

  /*
   * Builder below
   */

  public static AnnotationFunctionSourceBuilder with(Collection<Class<?>> annotatedClasses) {
    return new AnnotationFunctionSourceBuilder(annotatedClasses);
  }

  public static AnnotationFunctionSourceBuilder with(final String packageToScan) {
    return new AnnotationFunctionSourceBuilder(packageToScan);
  }

  public static class AnnotationFunctionSourceBuilder {
    private final String packageToScan;
    private final Collection<Class<?>> annotatedClasses;

    private AnnotationFunctionSource afs = new AnnotationFunctionSource();

    public AnnotationFunctionSourceBuilder(Collection<Class<?>> annotatedClasses) {
      this(null, annotatedClasses);
    }

    public AnnotationFunctionSourceBuilder(String packageToScan) {
      this(packageToScan, null);
    }

    private AnnotationFunctionSourceBuilder(String packageToScan, Collection<Class<?>> annotatedClasses) {
      this.packageToScan = packageToScan;
      this.annotatedClasses = annotatedClasses;
    }

    public AnnotationFunctionSourceBuilder with(AnnotationHelper helper) {
      afs.annotationHelper = helper;
      return this;
    }

    public AnnotationFunctionSourceBuilder with(DataStoreManager dataStoreManager) {
      afs.dataStoreManager = dataStoreManager;
      return this;
    }

    public AnnotationFunctionSource build() {
      if(annotatedClasses != null) {
        afs.init(annotatedClasses);
      } else if(packageToScan != null) {
        List<Class<?>> foundClasses = ClassHelper.loadClasses(packageToScan,
            afs.annotationHelper::hasEdmFunction);

        afs.init(foundClasses);
      } else {
        throw new IllegalArgumentException("No annotated classes defined in builder.");
      }
      return afs;
    }
  }
}
