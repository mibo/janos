package org.apache.olingo.odata2.janos.processor.core;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.part.*;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;
import org.apache.olingo.odata2.janos.processor.api.JanosService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 23.02.16.
 */
public class JanosODataService extends JanosService implements ODataService {
  private final ODataProcessor processor;
  private final Edm edm;

  public JanosODataService(EdmProvider edmProvider, org.apache.olingo.odata2.janos.processor.core.ODataProcessor processor) {
    this.processor = processor;
    this.edm = RuntimeDelegate.createEdm(edmProvider);
  }

  @Override
  public String getVersion() throws ODataException {
    return "2.0";
  }

  @Override
  public Edm getEntityDataModel() throws ODataException {
    return edm;
  }

  @Override
  public MetadataProcessor getMetadataProcessor() throws ODataException {
    return processor;
  }

  @Override
  public ServiceDocumentProcessor getServiceDocumentProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntityProcessor getEntityProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntitySetProcessor getEntitySetProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntityComplexPropertyProcessor getEntityComplexPropertyProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntityLinkProcessor getEntityLinkProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntityLinksProcessor getEntityLinksProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntityMediaProcessor getEntityMediaProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntitySimplePropertyProcessor getEntitySimplePropertyProcessor() throws ODataException {
    return processor;
  }

  @Override
  public EntitySimplePropertyValueProcessor getEntitySimplePropertyValueProcessor() throws ODataException {
    return processor;
  }

  @Override
  public FunctionImportProcessor getFunctionImportProcessor() throws ODataException {
    return processor;
  }

  @Override
  public FunctionImportValueProcessor getFunctionImportValueProcessor() throws ODataException {
    return processor;
  }

  @Override
  public BatchProcessor getBatchProcessor() throws ODataException {
    return processor;
  }

  @Override
  public ODataProcessor getProcessor() throws ODataException {
    return processor;
  }

  @Override
  public List<String> getSupportedContentTypes(Class<? extends org.apache.olingo.odata2.api.processor.ODataProcessor> processorFeature) throws ODataException {
    ArrayList<String> result = new ArrayList<>();
    if(this.processor != null) {
      result.addAll(this.processor.getCustomContentTypes(processorFeature));
    }

    if(processorFeature == BatchProcessor.class) {
      result.add("*/*");
    } else if(processorFeature == EntityProcessor.class) {
      result.add("application/atom+xml;type=entry;charset=utf-8");
      result.add("application/atom+xml;charset=utf-8");
      result.add("application/json;charset=utf-8");
      result.add("application/json;charset=utf-8;odata=verbose");
      result.add("application/json");
      result.add("application/json;odata=verbose");
      result.add("application/xml;charset=utf-8");
    } else if(processorFeature != FunctionImportProcessor.class && processorFeature != EntityLinkProcessor.class && processorFeature != EntityLinksProcessor.class && processorFeature != EntitySimplePropertyProcessor.class && processorFeature != EntityComplexPropertyProcessor.class) {
      if(processorFeature != EntityMediaProcessor.class && processorFeature != EntitySimplePropertyValueProcessor.class && processorFeature != FunctionImportValueProcessor.class) {
        if(processorFeature == EntitySetProcessor.class) {
          result.add("application/atom+xml;type=feed;charset=utf-8");
          result.add("application/atom+xml;charset=utf-8");
          result.add("application/json;charset=utf-8");
          result.add("application/json;charset=utf-8;odata=verbose");
          result.add("application/json");
          result.add("application/json;odata=verbose");
          result.add("application/xml;charset=utf-8");
        } else if(processorFeature == MetadataProcessor.class) {
          result.add("application/xml;charset=utf-8");
        } else {
          if(processorFeature != ServiceDocumentProcessor.class) {
            throw new ODataNotImplementedException();
          }

          result.add("application/atomsvc+xml;charset=utf-8");
          result.add("application/json;charset=utf-8");
          result.add("application/json;charset=utf-8;odata=verbose");
          result.add("application/json");
          result.add("application/json;odata=verbose");
          result.add("application/xml;charset=utf-8");
        }
      } else {
        result.add("*/*");
      }
    } else {
      result.add("application/xml;charset=utf-8");
      result.add("application/json;charset=utf-8");
      result.add("application/json;charset=utf-8;odata=verbose");
      result.add("application/json");
      result.add("application/json;odata=verbose");
    }

    return result;
  }
}
