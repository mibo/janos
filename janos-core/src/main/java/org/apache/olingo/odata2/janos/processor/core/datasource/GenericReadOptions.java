package org.apache.olingo.odata2.janos.processor.core.datasource;

import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.janos.processor.api.datasource.ReadOptions;

/**
 * Created by michael on 04.09.15.
 */
public class GenericReadOptions implements ReadOptions {

  public static ReadOptions none() {
    return new GenericReadOptions();
  }

  public static Builder start() {
    return new Builder();
  }

  public static class Builder {
    private GenericReadOptions options = new GenericReadOptions();

    public GenericReadOptions build() {
      return options;
    }

    public Builder filter(FilterExpression filter) {
      return this;
    }

    public Builder order(OrderByExpression orderBy) {
      return this;
    }

    public Builder skip(String skipToken, Integer skip) {
      return this;
    }

    public Builder top(Integer top) {
      return this;
    }
  }
}
