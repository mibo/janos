package org.apache.olingo.odata2.janos.processor.core.datasource;

import org.apache.olingo.odata2.janos.processor.api.datasource.ReadResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by mibo on 04.09.15.
 */
public final class GenericReadResult<T> implements ReadResult<T> {
  private final Collection<T> result;
  private boolean appliedSkip = false;
  private boolean appliedTop = false;
  private boolean appliedOrder = false;
  private boolean appliedFilter = false;

  private GenericReadResult(Collection<T> result) {
    this.result = new ArrayList<>(result);
  }

  @Override
  public Collection<T> getResult() {
    return Collections.unmodifiableCollection(result);
  }

  @Override
  public boolean appliedSkip() {
    return appliedSkip;
  }

  @Override
  public boolean appliedTop() {
    return appliedTop;
  }

  @Override
  public boolean appliedOrder() {
    return appliedOrder;
  }

  @Override
  public boolean appliedFilter() {
    return appliedFilter;
  }

  public static <T> Builder<T> forResult(Collection<T> result) {
    return new Builder<T>(result);
  }

  public static class Builder<T> {
    private final GenericReadResult<T> readResult;
    public Builder(Collection<T> result) {
      readResult = new GenericReadResult<>(result);
    }
    public Builder<T> top() {
      readResult.appliedTop = true;
      return this;
    }
    public Builder<T> skip() {
      readResult.appliedSkip = true;
      return this;
    }
    public Builder<T> filter() {
      readResult.appliedFilter = true;
      return this;
    }
    public Builder<T> order() {
      readResult.appliedOrder = true;
      return this;
    }
    public GenericReadResult<T> build() {
      return readResult;
    }
  }
}
