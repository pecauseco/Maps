package edu.brown.cs32.server;

import static edu.brown.cs.student.sprint5.Constants.RESPONSE_TYPE;
import static edu.brown.cs.student.sprint5.server.datasources.JSONReader.fromJsonFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.squareup.moshi.JsonDataException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * This class contains simple tests for our JSON reading functionality. Since this functionality
 * relies heavily on Moshi, we test only a few basic cases as well as error handling for
 * non-existent files.
 */
public class TestArbitraryJSON {

  /**
   * This test confirms that our JSON parsing functionality works as expected.
   *
   * @throws IOException if file intended to be parsed cannot be found
   * @throws JsonDataException if Moshi encounters issues reading the JSON file
   */
  @Test
  public void testReadJSON() throws IOException, JsonDataException {
    Map<String, Object> parsedJSON = fromJsonFile("data/mocks/mockJSON.json", RESPONSE_TYPE);
    assertEquals("JSON String Beans", parsedJSON.get("Favorite Food"));
    assertEquals("There's no such thing as too much JSON", parsedJSON.get("Favorite Quote"));
  }

  /**
   * This test confirms that we appropriately handle files that do not exist.
   *
   * @throws JsonDataException if Moshi encounters issues reading the JSON file
   */
  @Test
  public void testInvalidFile() throws JsonDataException {
    assertThrows(
        IOException.class,
        () -> {
          fromJsonFile("fakeJson.json", RESPONSE_TYPE);
        });
  }
}
