package org.apache.olingo.odata2.janos.processor.api.data;

import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;

/**
 * Optimized read options.
 *
 * Created by michael on 04.09.15.
 */
public final class ReadOptions {
  private FilterExpression filter;
  private OrderByExpression orderBy;
  private String skipToken;
  private Integer skip;
  private Integer top;

  public static ReadOptions none() {
    return new ReadOptions();
  }

  public static Builder start() {
    return new Builder();
  }

  public static Builder start(FilterExpression filter) {
    return new Builder().filter(filter);
  }

  public static Builder start(OrderByExpression orderBy) {
    return new Builder().order(orderBy);
  }

  public static Builder start(String skipToken, Integer skip) {
    return new Builder().skip(skipToken, skip);
  }

  public static Builder start(Integer top) {
    return new Builder().top(top);
  }

  public FilterExpression getFilter() {
    return filter;
  }

  public OrderByExpression getOrderBy() {
    return orderBy;
  }

  public Integer getSkip() {
    return skip;
  }

  public String getSkipToken() {
    return skipToken;
  }

  public Integer getTop() {
    return top;
  }

  public static class Builder {
    private ReadOptions options = new ReadOptions();

    public ReadOptions build() {
      return options;
    }

    public Builder filter(FilterExpression filter) {
      options.filter = filter;
      return this;
    }

    public Builder order(OrderByExpression orderBy) {
      options.orderBy = orderBy;
      return this;
    }

    public Builder skip(String skipToken, Integer skip) {
      options.skipToken = skipToken;
      options.skip = skip;
      return this;
    }

    public Builder top(Integer top) {
      options.top = top;
      return this;
    }
  }
}
