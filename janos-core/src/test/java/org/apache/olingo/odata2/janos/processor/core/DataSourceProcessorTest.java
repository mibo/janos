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

import com.google.gson.internal.StringMap;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.core.uri.UriParserImpl;
import org.apache.olingo.odata2.janos.processor.api.data.ReadOptions;
import org.apache.olingo.odata2.janos.processor.api.data.ReadResult;
import org.apache.olingo.odata2.janos.processor.api.data.source.DataSource;
import org.apache.olingo.odata2.janos.processor.api.data.store.DataStoreManager;
import org.apache.olingo.odata2.janos.processor.api.data.access.ValueAccess;
import org.apache.olingo.odata2.janos.processor.core.data.access.BeanPropertyAccess;
import org.apache.olingo.odata2.janos.processor.core.data.source.AnnotationDataSource;
import org.apache.olingo.odata2.janos.processor.core.data.access.AnnotationValueAccess;
import org.apache.olingo.odata2.janos.processor.core.model.Building;
import org.apache.olingo.odata2.janos.processor.core.model.Room;
import org.apache.olingo.odata2.testutil.helper.JsonHelper;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.mock.EdmMock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

/**
 *
 */
public class DataSourceProcessorTest {

  private DataSourceProcessor dataSourceProcessor;
  private DataSource mockedDataSource = Mockito.mock(DataSource.class);
  private ValueAccess mockedValueAccess = new BeanPropertyAccess();//Mockito.mock(ValueAccess.class);
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
    String url1 = "Rooms?$orderby=Seats%20desc&$skiptoken=12&$skipApplied=000000&$topApplied=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url1);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$topApplied=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skiptoken=213&$skipApplied=99";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skiptoken=213&$skipApplied=0000";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }

  @Test
  public void testSkipOnly() {
    String url = "Rooms?$orderby=Seats%20desc&$skipApplied=000000&$topApplied=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$topApplied=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skipApplied=213";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skipApplied=0999";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }

  @Test
  public void testSkiptokenOnly() {
    String url = "Rooms?$orderby=Seats%20desc&$skiptoken=213&$topApplied=200";
    String result = dataSourceProcessor.percentEncodeNextLink(url);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc&$topApplied=200", result);

    String url2 = "Rooms?$orderby=Seats%20desc&$skiptoken=213";
    String result2 = dataSourceProcessor.percentEncodeNextLink(url2);
    Assert.assertEquals("Rooms?$orderby=Seats%20desc", result2);

    String url3 = "Rooms?$skiptoken=213";
    String result3 = dataSourceProcessor.percentEncodeNextLink(url3);
    Assert.assertEquals("Rooms", result3);
  }

  @Test
  public void readTop() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Mockito.when(uriInfo.getTop()).thenReturn(5);

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult) readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");
    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(5, parsedResults.size());
  }

  @Test
  public void optimizedReadTop() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Mockito.when(uriInfo.getTop()).thenReturn(5);

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).topApplied().build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult) readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");
    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(10, parsedResults.size());
  }


  @Test
  public void readOrder() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Edm edm = EdmMock.createMockEdm();

    OrderByExpression exp = UriParserImpl.parseOrderBy(edm, edm.getEntityType("RefScenario", "Room"), "Name");
    Mockito.when(uriInfo.getOrderBy()).thenReturn(exp);

    List<Room> results = createRooms(1, 10);
    Collections.reverse(results);
    ReadResult<Room> readResult = ReadResult.forResult(results).build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(10, parsedResults.size());
    Assert.assertEquals("Room with id: 1", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(9).get("Name"));
  }

  @Test
  public void optimizedReadOrder() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Edm edm = EdmMock.createMockEdm();

    OrderByExpression exp = UriParserImpl.parseOrderBy(edm, edm.getEntityType("RefScenario", "Room"), "Name");
    Mockito.when(uriInfo.getOrderBy()).thenReturn(exp);

    List<Room> results = createRooms(1, 10);
    Collections.shuffle(results);
    ReadResult<Room> readResult = ReadResult.forResult(results).orderApplied().build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(10, parsedResults.size());
    Assert.assertEquals("Room with id: 1", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(9).get("Name"));
  }

  @Test
  public void readFilter() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Edm edm = EdmMock.createMockEdm();

    FilterExpression exp = UriParserImpl.parseFilter(edm, edm.getEntityType("RefScenario", "Room"), "Version gt 105");
    Mockito.when(uriInfo.getFilter()).thenReturn(exp);

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(5, parsedResults.size());
    Assert.assertEquals("Room with id: 10", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(4).get("Name"));
  }

  @Test
  public void readOptimizedFilter() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");
    Edm edm = EdmMock.createMockEdm();

    FilterExpression exp = UriParserImpl.parseFilter(edm, edm.getEntityType("RefScenario", "Room"), "Version gt 105");
    Mockito.when(uriInfo.getFilter()).thenReturn(exp);

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).filterApplied().build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(10, parsedResults.size());
    Assert.assertEquals("Room with id: 1", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(9).get("Name"));
  }

  @Test
  public void readSkip() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");

    Mockito.when(uriInfo.getSkip()).thenReturn(5);
    Mockito.when(uriInfo.getSkipToken()).thenReturn("2");

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(3, parsedResults.size());
    Assert.assertEquals("Room with id: 7", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(2).get("Name"));
  }

  @Test
  public void readOptimizedSkip() throws Exception {
    GetEntitySetUriInfo uriInfo = createMockedUriInfo("Rooms");

    Mockito.when(uriInfo.getSkip()).thenReturn(5);
    Mockito.when(uriInfo.getSkipToken()).thenReturn("2");

    List<Room> results = createRooms(1, 10);
    ReadResult<Room> readResult = ReadResult.forResult(results).skipApplied().build();
    Mockito.when(mockedDataSource.readData(Mockito.any(EdmEntitySet.class), Mockito.any(ReadOptions.class)))
        .thenReturn((ReadResult)readResult);

    ODataResponse result = dataSourceProcessor.readEntitySet(uriInfo, "application/json");

    StringHelper.Stream resultStream = StringHelper.toStream(result.getEntityAsStream());
    List<StringMap<?>> parsedResults = JsonHelper.getResults(resultStream.asString());
    Assert.assertEquals(10, parsedResults.size());
    Assert.assertEquals("Room with id: 1", parsedResults.get(0).get("Name"));
    Assert.assertEquals("Room with id: 9", parsedResults.get(9).get("Name"));
  }

  private UriInfo createMockedUriInfo(String entitySetName) throws ODataException {
    Edm edm = EdmMock.createMockEdm();
    UriInfo uriInfo = Mockito.mock(UriInfo.class);
    EdmEntitySet rooms = EdmMock.getEntitySet(edm, entitySetName);
    Mockito.when(uriInfo.getTargetEntitySet()).thenReturn(rooms);
    ODataContext context = Mockito.mock(ODataContext.class);
    PathInfo pathInfo = Mockito.mock(PathInfo.class);
    Mockito.when(context.getPathInfo()).thenReturn(pathInfo);
    dataSourceProcessor.setContext(context);
    return uriInfo;
  }

  private List<Room> createRooms(int startId, int amount) {
    List<Room> results = new ArrayList<>();
    int max = startId + amount;
    for (int i = startId; i < max; i++) {
      results.add(createRoom(i));
    }

    return results;
  }

  private Room createRoom(int id) {
    Room r = new Room(id, "Room with id: " + id);
    r.setSeats(10 + id);
    r.setVersion(100 + id);
    return r;
  }
}
