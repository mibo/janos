package org.apache.olingo.odata2.janos.processor.api.extension;

import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.omg.CORBA.portable.InputStream;

/**
 * Created by michael on 23.02.16.
 */
public interface ExtensionContext {
  String PARA_URI_INFO = "~uriinfo";
  String PARA_ACCEPT_HEADER = "~acceptheader";
  String PARA_REQUEST_BODY = "~requestbody";
  String PARA_REQUEST_TYPE = "~requesttype";

  ExtensionContext addParameter(String name, Object value);

  Object getParameter(String name);

  UriInfo getUriInfo();

  String getAcceptHeader();

  InputStream getRequestBody();

  Extension.Method getRequestType();

  ODataResponse proceed() throws Exception;
}
