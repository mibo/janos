package org.apache.olingo.odata2.janos.processor.ref.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.janos.processor.api.datasource.DataStoreFactory;
import org.apache.olingo.odata2.janos.processor.api.datasource.FunctionExecutor;

import static org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.*;

/**
 * Created by mibo on 23.08.15.
 *
 *
 */
public class RefFunctions implements FunctionExecutor {

  private DataStoreFactory dataStoreFactory;

  @Override
  public void init(DataStoreFactory dataStore) {
    dataStoreFactory = dataStore;
  }

  @EdmFunctionImport(returnType = @ReturnType(type = ReturnType.Type.SIMPLE))
  public String citySearch(@EdmFunctionImportParameter(name = "cityName", type = EdmType.STRING) String name) {
//    dataStoreFactory.
    return name;
  }
}
