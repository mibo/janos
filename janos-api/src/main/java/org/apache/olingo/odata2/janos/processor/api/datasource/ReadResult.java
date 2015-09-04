package org.apache.olingo.odata2.janos.processor.api.datasource;

import java.util.Collection;

/**
 * Created by mibo on 04.09.15.
 */
public interface ReadResult<T> {

  Collection<T> getResult();

  boolean appliedSkip();
  boolean appliedTop();
  boolean appliedOrder();
  boolean appliedFilter();
}
