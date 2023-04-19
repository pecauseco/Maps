package edu.brown.cs.student.sprint5.server.datasources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Utility class for reading JSON files. */
public class JSONReader {

  /**
   * Convert a JSON-formatted text file to an object of a specfied type
   *
   * @param path path of file to be converted
   * @param type type of object to be returned
   * @return converted JSON
   * @throws IOException error thrown by Moshi
   */
  public static <T> T fromJsonFile(String path, Type type) throws IOException, JsonDataException {
    JsonAdapter<T> adapter = new Moshi.Builder().build().adapter(type);
    try {
      String asString = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
      return adapter.fromJson(asString);
    } catch (IOException e) {
      throw new IOException("Error reading file at " + path);
    }
  }

  /**
   * Convert a JSON-formatted String to an object of a specfied type
   *
   * @param json JSON string to be converted
   * @param type type of object to be returned
   * @return converted JSON
   * @throws IOException error thrown by Moshi
   */
  public static <T> T fromJsonString(String json, Type type) throws IOException, JsonDataException {
    JsonAdapter<T> adapter = new Moshi.Builder().build().adapter(type);
    return adapter.fromJson(json);
  }

  /**
   * Convert a generic object into a JSON-formatted String
   *
   * @param toParse the object to be parsed
   * @param type the type of the object to be parsed
   * @return the JSON-formatted String
   * @throws IOException
   */
  public static String toJson(Object toParse, Type type) throws JsonDataException {
    JsonAdapter<Object> adapter = new Moshi.Builder().build().adapter(type);
    return adapter.toJson(toParse);
  }
}
