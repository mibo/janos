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

  /**
   * None read options.
   *
   * @return the read options
   */
  public static ReadOptions none() {
    return new ReadOptions();
  }

  /**
   * Start builder.
   *
   * @return the builder
   */
  public static Builder start() {
    return new Builder();
  }

  /**
   * Start builder.
   *
   * @param filter the filter
   * @return the builder
   */
  public static Builder start(FilterExpression filter) {
    return new Builder().filter(filter);
  }

  /**
   * Start builder.
   *
   * @param orderBy the order by
   * @return the builder
   */
  public static Builder start(OrderByExpression orderBy) {
    return new Builder().order(orderBy);
  }

  /**
   * Start builder.
   *
   * @param skipToken the skip token
   * @param skip the skip
   * @return the builder
   */
  public static Builder start(String skipToken, Integer skip) {
    return new Builder().skip(skipToken, skip);
  }

  /**
   * Start builder.
   *
   * @param top the top
   * @return the builder
   */
  public static Builder start(Integer top) {
    return new Builder().top(top);
  }

  /**
   * Gets filter.
   *
   * @return the filter
   */
  public FilterExpression getFilter() {
    return filter;
  }

  /**
   * Gets order by.
   *
   * @return the order by
   */
  public OrderByExpression getOrderBy() {
    return orderBy;
  }

  /**
   * Gets skip.
   *
   * @return the skip
   */
  public Integer getSkip() {
    return skip;
  }

  /**
   * Gets skip token.
   *
   * @return the skip token
   */
  public String getSkipToken() {
    return skipToken;
  }

  /**
   * Gets top.
   *
   * @return the top
   */
  public Integer getTop() {
    return top;
  }

  /**
   * The type Builder.
   */
  public static class Builder {
    private ReadOptions options = new ReadOptions();

    /**
     * Build read options.
     *
     * @return the read options
     */
    public ReadOptions build() {
      return options;
    }

    /**
     * Filter builder.
     *
     * @param filter the filter
     * @return the builder
     */
    public Builder filter(FilterExpression filter) {
      options.filter = filter;
      return this;
    }

    /**
     * Order builder.
     *
     * @param orderBy the order by
     * @return the builder
     */
    public Builder order(OrderByExpression orderBy) {
      options.orderBy = orderBy;
      return this;
    }

    /**
     * Skip builder.
     *
     * @param skipToken the skip token
     * @param skip the skip
     * @return the builder
     */
    public Builder skip(String skipToken, Integer skip) {
      options.skipToken = skipToken;
      options.skip = skip;
      return this;
    }

    /**
     * Top builder.
     *
     * @param top the top
     * @return the builder
     */
    public Builder top(Integer top) {
      options.top = top;
      return this;
    }
  }
}
