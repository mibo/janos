package de.mirb.olingo.janos.sampleds.ds;

import de.mirb.olingo.janos.sampleds.model.Person;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStore;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreException;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 26.09.15.
 */
public class FileStore implements DataStoreManager {
  private Map<String,String> defaultProperties = Collections.emptyMap();
  private Map<String, DataStore> name2Ds = new HashMap<>();

  public void setDefaultProperty(String name, String value) {

  }

  public <T> DataStore<T> createDataStore(Class<T> clz) throws DataStoreException {
    return createDataStore(clz, defaultProperties);
  }

  public <T> DataStore<T> createDataStore(Class<T> clz, Map<String, String> properties) throws DataStoreException {
    if(clz == Person.class) {
      return (DataStore<T>) new PersonStore();
    }
    throw new DataStoreException("Class '" + clz.getSimpleName() + "' is not supported");
  }

  public <T> DataStore<T> grantDataStore(String name, Class<T> clz) throws DataStoreException {
    return grantDataStore(name, clz, defaultProperties);
  }

  public <T> DataStore<T> grantDataStore(String name, Class<T> clz, Map<String, String> properties) throws DataStoreException {
    DataStore<T> ds = getDataStore(name, clz);
    if(ds == null) {
      ds = createDataStore(clz, properties);
      name2Ds.put(name, ds);
    }
    return ds;
  }

  public <T> DataStore<T> getDataStore(String name, Class<T> clz) throws DataStoreException {
    if(clz == Person.class) {
      return name2Ds.get(name);
    }
    return null;
  }

  public DataStore<Object> getDataStore(String name) {
    try {
      return (DataStore) getDataStore(name, Person.class);
    } catch (DataStoreException e) {
      throw new RuntimeException(e);
    }
  }
}
