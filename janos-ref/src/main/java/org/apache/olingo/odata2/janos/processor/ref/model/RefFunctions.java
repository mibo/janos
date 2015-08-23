package org.apache.olingo.odata2.janos.processor.ref.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;

import static org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.*;

/**
 * Created by mibo on 23.08.15.
 *
 *
 */
public class RefFunctions {

  @EdmFunctionImport(returnType = @ReturnType(type = ReturnType.Type.SIMPLE))
  public String citySearch(@EdmFunctionImportParameter(name = "cityName", type = EdmType.STRING) String name) {
    // System.out.println("Search city: " + name);
    return name;
  }
}
