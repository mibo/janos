package org.apache.olingo.odata2.janos.processor.api.datasource;

import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;

/**
 * Optimized read options.
 *
 * Created by mibo on 04.09.15.
 */
public interface ReadOptions {
  FilterExpression getFilter();
  OrderByExpression getOrderBy();
  Integer getSkip();
  String getSkipToken();
  Integer getTop();
}
