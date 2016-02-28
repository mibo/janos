package org.apache.olingo.odata2.janos.processor.api.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Extension is used to annotate a method which extends (intercepts) a generic
 * OData method (GET, POST, PUT, DELETE).
 *
 * Created by mibo on 21.02.16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Extension {
  enum Method {GET, POST, PUT, DELETE}
  String[] entitySetNames() default {};
//  Methods[] methods() default { Methods.GET, Methods.POST, Methods.PUT, Methods.DELETE };
  Method[] methods() default {};
}
