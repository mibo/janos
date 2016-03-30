package org.apache.olingo.odata2.janos.processor.ref.jpa.model;

import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImportParameter;
import org.apache.olingo.odata2.api.annotation.edm.EdmType;
import org.apache.olingo.odata2.janos.processor.api.data.source.FunctionExecutor;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStore;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreException;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.core.util.AnnotationRuntimeException;

import java.util.Collection;
import java.util.Locale;

import static org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.ReturnType;

/**
 * Created by mibo on 23.08.15.
 *
 *
 */
public class RefFunctions implements FunctionExecutor {

  private DataStoreManager dataStoreManager;

  @Override
  public void init(DataStoreManager dataStore) {
    dataStoreManager = dataStore;
  }

  @EdmFunctionImport(returnType = @ReturnType(type = ReturnType.Type.ENTITY))
  public City citySearch(@EdmFunctionImportParameter(name = "cityName", type = EdmType.STRING) String name) {
    try {
      DataStore<Employee> ds = dataStoreManager.getDataStore("Employees", Employee.class);
      Collection<Employee> employees = ds.read();

      for (Employee employee : employees) {
        Location location = employee.getLocation();
        if(location != null) {
          City city = location.getCity();
          if(city != null && city.getCityName() != null) {
            String cityName = city.getCityName().toLowerCase(Locale.ROOT);
            if(cityName.contains(name.toLowerCase(Locale.ROOT))) {
              return city;
            }
          }
        }
      }
    } catch (DataStoreException e) {
      throw new AnnotationRuntimeException("", e);
    }
    return null;
  }
}
