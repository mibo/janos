package org.apache.olingo.odata2.testutil.helper;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by michael on 12.09.15.
 */
public class JsonHelper {

  public static StringMap<?> getStringMap(final String body) {
    Gson gson = new Gson();
    final StringMap<?> map = gson.fromJson(body, new TypeToken<StringMap<?>>() {}.getType());
    if (map.get("d") instanceof StringMap<?>) {
      return (StringMap<?>) map.get("d");
    } else {
      return map;
    }
  }

  public static List<StringMap<?>> getResults(final String body) {
    Gson gson = new Gson();
    final StringMap<?> map = gson.fromJson(body, new TypeToken<StringMap<?>>() {}.getType());
    if (map.get("d") instanceof StringMap<?>) {
      StringMap dMap = (StringMap<?>) map.get("d");
      if(dMap.get("results") instanceof List) {
        return (List) dMap.get("results");
      }
    }
    throw new RuntimeException("Expected results json array was not found for JSON body:\n" + body);
  }
}
