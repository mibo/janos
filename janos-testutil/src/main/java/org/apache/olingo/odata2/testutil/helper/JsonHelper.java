package org.apache.olingo.odata2.testutil.helper;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by michael on 12.09.15.
 */
public class JsonHelper {

  public static LinkedTreeMap<?, ?> getLinkedTreeMap(final String body) {
    Gson gson = new Gson();
    final LinkedTreeMap<?, ?> map = gson.fromJson(body, new TypeToken<LinkedTreeMap<?, ?>>() {}.getType());
    if (map.get("d") instanceof LinkedTreeMap<?, ?>) {
      return (LinkedTreeMap<?, ?>) map.get("d");
    } else {
      return map;
    }
  }

  public static List<LinkedTreeMap<?, ?>> getResults(final String body) {
    Gson gson = new Gson();
    final LinkedTreeMap<?, ?> map = gson.fromJson(body, new TypeToken<LinkedTreeMap<?, ?>>() {}.getType());
    if (map.get("d") instanceof LinkedTreeMap<?, ?>) {
      LinkedTreeMap dMap = (LinkedTreeMap<?, ?>) map.get("d");
      if(dMap.get("results") instanceof List) {
        return (List) dMap.get("results");
      }
    }
    throw new RuntimeException("Expected results json array was not found for JSON body:\n" + body);
  }
}
