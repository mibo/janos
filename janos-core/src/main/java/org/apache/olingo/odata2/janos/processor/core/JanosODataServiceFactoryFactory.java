package org.apache.olingo.odata2.janos.processor.core;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.janos.processor.api.JanosServiceFactory;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.source.FunctionSource;
import org.apache.olingo.odata2.janos.processor.core.extension.ExtensionProcessor;
import org.apache.olingo.odata2.janos.processor.core.extension.ExtensionRegistry;

/**
 * A {@link JanosServiceFactory} is used to create thread safe {@link ODataService} ({@link JanosServiceFactory )} instances.
 */
public class JanosODataServiceFactoryFactory extends JanosServiceFactory {
  private final EdmProvider edmProvider;
  private final DataSource dataSource;
  private final ValueAccess valueAccess;
  private final FunctionSource functionSource;
  private final ExtensionRegistry extensionRegistry;

  public JanosODataServiceFactoryFactory(EdmProvider edmProvider, DataSource dataSource, ValueAccess valueAccess,
                                         FunctionSource functionSource, ExtensionRegistry extensionRegistry) {
    this.edmProvider = edmProvider;
    this.dataSource = dataSource;
    this.valueAccess = valueAccess;
    this.functionSource = functionSource;
    this.extensionRegistry = extensionRegistry;
  }

  @Override
  public ODataService createService(ODataContext context) {
    DataSourceProcessor dsProcessor = new DataSourceProcessor(dataSource, valueAccess, functionSource);
    ODataProcessor wrappedProcessor = ExtensionProcessor.wrap(dsProcessor).extensions(extensionRegistry).finish(context);
    return new JanosODataService(this.edmProvider, wrappedProcessor);
  }
}
