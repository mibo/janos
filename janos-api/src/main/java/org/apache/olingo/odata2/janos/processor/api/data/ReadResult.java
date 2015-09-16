package org.apache.olingo.odata2.janos.processor.api.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by mibo on 04.09.15.
 * @param <T>  the type parameter
 */
public final class ReadResult<T> {
  private final Collection<T> result;
  private boolean appliedSkip = false;
  private boolean appliedTop = false;
  private boolean appliedOrder = false;
  private boolean appliedFilter = false;

  private ReadResult(Collection<T> result) {
    this.result = new ArrayList<>(result);
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public Collection<T> getResult() {
    return Collections.unmodifiableCollection(result);
  }

  /**
   * Get first result from results collection
   * or <code>null</code> if result collection is empty.
   *
   * @return first result from results collection
   *          or <code>null</code> if result collection is empty.
   */
  public T getFirst() {
    if(result.isEmpty()) {
      return null;
    }
    return result.iterator().next();
  }

  /**
   * Applied skipApplied.
   *
   * @return the boolean
   */
  public boolean isSkipApplied() {
    return appliedSkip;
  }

  /**
   * Applied topApplied.
   *
   * @return the boolean
   */
  public boolean isTopApplied() {
    return appliedTop;
  }

  /**
   * Applied orderApplied.
   *
   * @return the boolean
   */
  public boolean isOrderApplied() {
    return appliedOrder;
  }

  /**
   * Applied filterApplied.
   *
   * @return the boolean
   */
  public boolean isFilterApplied() {
    return appliedFilter;
  }

  /**
   * For result.
   *
   * @param <T>  the type parameter
   * @return the builder
   */
  public static <T> Builder<T> start() {
    return new Builder<T>();
  }

  /**
   * For result.
   *
   * @param <T>  the type parameter
   * @param result the result
   * @return the builder
   */
  public static <T> Builder<T> forResult(Collection<T> result) {
    return new Builder<T>(result);
  }

  /**
   * Start building a result with applied settings from existing result.
   *
   * @param readResult existing result
   * @param result result values
   * @param <T> type parameter for ReadResult
   * @return the new ReadResult
   */
  public static <T> Builder<T> fromResult(ReadResult<T> readResult, Collection<T> result) {
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
     */
    public Builder() {
      this(new ArrayList<T>());
    }

    /**
     * Instantiates a new Builder.
     *
     * @param result the result
     */
    public Builder(Collection<T> result) {
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
     * Set top to applied.
     *
     * @return the builder
     */
    public Builder<T> topApplied() {
      readResult.appliedTop = true;
      return this;
    }

    /**
     * Set skip to applied.
     *
     * @return the builder
     */
    public Builder<T> skipApplied() {
      readResult.appliedSkip = true;
      return this;
    }

    /**
     * Set filter to applied.
     *
     * @return the builder
     */
    public Builder<T> filterApplied() {
      readResult.appliedFilter = true;
      return this;
    }

    /**
     * Set order to applied.
     *
     * @return the builder
     */
    public Builder<T> orderApplied() {
      readResult.appliedOrder = true;
      return this;
    }

    public Builder<T> values(Collection<T> values) {
      if(!readResult.result.isEmpty()) {
        readResult.result.clear();
      }
      readResult.result.addAll(values);
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
