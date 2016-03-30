package org.apache.olingo.odata2.janos.processor.core;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmServiceMetadata;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.*;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by mibo on 23.02.16.
 */
public class JanosODataProcessor implements ODataProcessor {
  private ODataContext context;

  public ODataContext getContext() {
    return this.context;
  }

  public void setContext(ODataContext context) {
    this.context = context;
  }

  public ODataResponse executeBatch(BatchHandler handler, String contentType, InputStream content) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public BatchResponsePart executeChangeSet(BatchHandler handler, List<ODataRequest> requests) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse executeFunctionImport(GetFunctionImportUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse executeFunctionImportValue(GetFunctionImportUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntitySimplePropertyValue(GetSimplePropertyUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntitySimplePropertyValue(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse deleteEntitySimplePropertyValue(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntitySimpleProperty(GetSimplePropertyUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntitySimpleProperty(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntityMedia(GetMediaResourceUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntityMedia(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse deleteEntityMedia(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntityLinks(GetEntitySetLinksUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse countEntityLinks(GetEntitySetLinksCountUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse createEntityLink(PostUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntityLink(GetEntityLinkUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse existsEntityLink(GetEntityLinkCountUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntityLink(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse deleteEntityLink(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntityComplexProperty(GetComplexPropertyUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntityComplexProperty(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, boolean merge, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntitySet(GetEntitySetUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse countEntitySet(GetEntitySetCountUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse createEntity(PostUriInfo uriInfo, InputStream content, String requestContentType, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readEntity(GetEntityUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse existsEntity(GetEntityCountUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse updateEntity(PutMergePatchUriInfo uriInfo, InputStream content, String requestContentType, boolean merge, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse deleteEntity(DeleteUriInfo uriInfo, String contentType) throws ODataException {
    throw new ODataNotImplementedException();
  }

  public ODataResponse readServiceDocument(GetServiceDocumentUriInfo uriInfo, String contentType) throws ODataException {
    Edm entityDataModel = this.getContext().getService().getEntityDataModel();
    String serviceRoot = this.getContext().getPathInfo().getServiceRoot().toASCIIString();
    if (this.getContext().getHttpMethod().equals("HEAD")) {
      return ODataResponse.header("DataServiceVersion", "1.0").build();
    } else {
      ODataResponse response = EntityProvider.writeServiceDocument(contentType, entityDataModel, serviceRoot);
      return ODataResponse.fromResponse(response).header("DataServiceVersion", "1.0").build();
    }
  }

  public ODataResponse readMetadata(GetMetadataUriInfo uriInfo, String contentType) throws ODataException {
    EdmServiceMetadata edmServiceMetadata = this.getContext().getService().getEntityDataModel().getServiceMetadata();
    return ODataResponse.status(HttpStatusCodes.OK).header("DataServiceVersion", edmServiceMetadata.getDataServiceVersion()).entity(edmServiceMetadata.getMetadata()).build();
  }

  public List<String> getCustomContentTypes(Class<? extends org.apache.olingo.odata2.api.processor.ODataProcessor> processorFeature) throws ODataException {
    return Collections.emptyList();
  }
}

