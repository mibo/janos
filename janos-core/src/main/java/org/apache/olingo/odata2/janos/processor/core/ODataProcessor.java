package org.apache.olingo.odata2.janos.processor.core;

import org.apache.olingo.odata2.api.processor.feature.CustomContentType;
import org.apache.olingo.odata2.api.processor.part.*;

/**
 * Created by mibo on 23.02.16.
 */
public interface ODataProcessor extends MetadataProcessor, ServiceDocumentProcessor, EntityProcessor, EntitySetProcessor,
    EntityComplexPropertyProcessor, EntityLinkProcessor, EntityLinksProcessor, EntityMediaProcessor, EntitySimplePropertyProcessor,
    EntitySimplePropertyValueProcessor, FunctionImportProcessor, FunctionImportValueProcessor, BatchProcessor, CustomContentType {
}
