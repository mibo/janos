package org.apache.olingo.odata2.janos.processor.api.extension;

/**
 * An Extension is used to annotate a method which extends (intercepts) a generic
 * OData method (GET, POST, PUT, DELETE).
 *
 * Created by mibo on 21.02.16.
 */
public @interface Extension {
  enum Method {GET, POST, PUT, DELETE}
  String[] entitySetNames() default {};
//  Methods[] methods() default { Methods.GET, Methods.POST, Methods.PUT, Methods.DELETE };
  Method[] methods() default {};
}
