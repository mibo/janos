/*
 * Copyright 2013 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.odata2.janos.processor.core;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.data.access.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.model.Building;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 */
public class DataSourceProcessorTest {

  private DataSourceProcessor dataSourceProcessor;
  private DataSource mockedDataSource = Mockito.mock(DataSource.class);
  private ValueAccess mockedValueAccess = Mockito.mock(ValueAccess.class);
  private DataStoreManager mockedDataStoreManager = Mockito.mock(DataStoreManager.class);

  @Test
  public void init() throws ODataException {
    DataSource dataSource = new AnnotationDataSource(Building.class.getPackage().getName(), mockedDataStoreManager);
    ValueAccess valueAccess = new AnnotationValueAccess();
    DataSourceProcessor lp = new DataSourceProcessor(dataSource, valueAccess);

    Assert.assertNotNull(lp);
  }

  public DataSourceProcessorTest() {
    dataSourceProcessor = new DataSourceProcessor(mockedDataSource, mockedValueAccess);
  }

  @Test
  public void testSkipAndSkiptoken() {
    String url1 = "Rooms?$orderby=Seats%20desc&$skiptoken=12&$skip=000000&$top=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url1);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$top=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skiptoken=213&$skip=99";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skiptoken=213&$skip=0000";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }

  @Test
  public void testSkipOnly() {
    String url = "Rooms?$orderby=Seats%20desc&$skip=000000&$top=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$top=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skip=213";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skip=0999";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }

  @Test
  public void testSkiptokenOnly() {
    String url = "Rooms?$orderby=Seats%20desc&$skiptoken=213&$top=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$top=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skiptoken=213";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skiptoken=213";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }
}
