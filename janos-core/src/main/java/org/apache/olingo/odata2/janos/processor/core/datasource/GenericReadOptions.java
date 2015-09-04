package org.apache.olingo.odata2.janos.processor.core.datasource;

import org.apache.olingo.odata2.janos.processor.api.datasource.ReadOptions;

/**
 * Created by michael on 04.09.15.
 */
public class GenericReadOptions implements ReadOptions {

  public static ReadOptions none() {
    return new GenericReadOptions();
  }
}
