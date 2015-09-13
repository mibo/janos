package org.apache.olingo.odata2.janos.processor.api.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Created by mibo on 04.09.15.
 * @param <T>  the type parameter
 */
public final class ReadResult<T> {
  private final List<T> result;
  private boolean appliedSkip = false;
  private boolean appliedTop = false;
  private boolean appliedOrder = false;
  private boolean appliedFilter = false;

  private ReadResult(List<T> result) {
    this.result = new ArrayList<>(result);
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public List<T> getResult() {
    return Collections.unmodifiableList(result);
  }

  /**
   * Applied skip.
   *
   * @return the boolean
   */
  public boolean appliedSkip() {
    return appliedSkip;
  }

  /**
   * Applied top.
   *
   * @return the boolean
   */
  public boolean appliedTop() {
    return appliedTop;
  }

  /**
   * Applied order.
   *
   * @return the boolean
   */
  public boolean appliedOrder() {
    return appliedOrder;
  }

  /**
   * Applied filter.
   *
   * @return the boolean
   */
  public boolean appliedFilter() {
    return appliedFilter;
  }

  /**
   * For result.
   *
   * @param <T>  the type parameter
   * @param result the result
   * @return the builder
   */
  public static <T> Builder<T> forResult(List<T> result) {
    return new Builder<T>(result);
  }

  public static <T> Builder<T> fromResult(ReadResult<T> readResult, List<T> result) {
    return new Builder<T>(result).apply(readResult);
  }

  /**
   * For an empty result.
   *
   * @param <T>  the type parameter
   * @return the empty read result
   */
  public static <T> ReadResult<T> empty() {
    return new Builder<T>(Collections.emptyList()).build();
  }

  /**
   * The type Builder.
   * @param <T>  the type parameter
   */
  public static class Builder<T> {
    private final ReadResult<T> readResult;

    /**
     * Instantiates a new Builder.
     *
     * @param result the result
     */
    public Builder(List<T> result) {
      readResult = new ReadResult<>(result);
    }

    Builder<T> apply(ReadResult<T> result) {
      readResult.appliedFilter = result.appliedFilter;
      readResult.appliedOrder = result.appliedOrder;
      readResult.appliedSkip = result.appliedSkip;
      readResult.appliedTop = result.appliedTop;
      return this;
    }


    /**
     * Top builder.
     *
     * @return the builder
     */
    public Builder<T> top() {
      readResult.appliedTop = true;
      return this;
    }

    /**
     * Skip builder.
     *
     * @return the builder
     */
    public Builder<T> skip() {
      readResult.appliedSkip = true;
      return this;
    }

    /**
     * Filter builder.
     *
     * @return the builder
     */
    public Builder<T> filter() {
      readResult.appliedFilter = true;
      return this;
    }

    /**
     * Order builder.
     *
     * @return the builder
     */
    public Builder<T> order() {
      readResult.appliedOrder = true;
      return this;
    }

    /**
     * Build read result.
     *
     * @return the read result
     */
    public ReadResult<T> build() {
      return readResult;
    }
  }
}
