package edu.brown.cs32.server;

import static edu.brown.cs.student.sprint5.Constants.*;
import static edu.brown.cs.student.sprint5.Constants.REDLINING_PATH;
import static edu.brown.cs.student.sprint5.server.testutils.AssertionUtils.*;
import static edu.brown.cs.student.sprint5.server.testutils.RequestUtils.*;
import static spark.Spark.after;

import edu.brown.cs.student.sprint5.server.handlers.JSONDataHandler;
import edu.brown.cs.student.sprint5.server.proxies.BoundingBoxProxy;
import edu.brown.cs.student.sprint5.server.proxies.KeywordSearchProxy;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.FeatureCollection;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.util.List;
import org.junit.jupiter.api.*;
import spark.Spark;

/**
 * This class contains live integration tests for our server. It tests the server's ability to
 * correctly handle requests for data from the redlining data set.
 */
public class TestLiveServer {

  private JSONDataHandler boundingBoxHandler;
  private JSONDataHandler keywordHandler;

  /**
   * This method is run at the beginning of our integration test suite. It sets up the server for
   * our integration testing.
   */
  @BeforeAll
  public static void initialSetup() {
    Spark.port(0);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
  }

  /**
   * This method is run before each test in our integration test suite. It clears the state of the
   * server and re-sets up the server for each test by instantiating proxies and handlers and adding
   * them to the server at their respective endpoints
   */
  @BeforeEach
  public void setup() {
    try {
      this.boundingBoxHandler =
          new JSONDataHandler(REDLINING_PATH, FeatureCollection.class, BoundingBoxProxy.class);
      this.keywordHandler =
          new JSONDataHandler(REDLINING_PATH, FeatureCollection.class, KeywordSearchProxy.class);
      Spark.get("/" + BOUNDING_BOX_ENDPOINT, this.boundingBoxHandler);
      Spark.get("/" + KEYWORD_ENDPOINT, this.keywordHandler);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to initialize server");
    }

    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /**
   * This method is run after each test in our integration test suite. It clears the state of the
   * server and un-maps all endpoints from the server.
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/" + BOUNDING_BOX_ENDPOINT);
    Spark.unmap("/" + KEYWORD_ENDPOINT);
    Spark.awaitStop();
  }

  /** This method tests the bounding box endpoint of the server. */
  @Test
  public void testBoxEndpoint() throws Exception {
    // valid requests
    ServerResponse response = tryBoxEndpoint(List.of(41.0, -71.0, 42.0, -70.0));
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertFalse(checkHasFeatures(getFeatureCollection(response)));

    response = tryBoxEndpoint(List.of(-90, -180, 90, 180));
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertTrue(checkHasFeatures(getFeatureCollection(response)));

    response = tryBoxEndpoint(List.of("41.0", "-71.0", "42.0", "-70.0"));
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertFalse(checkHasFeatures(getFeatureCollection(response)));

    response = tryBoxEndpoint(List.of("-90", "-180", "90", "180"));
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertTrue(checkHasFeatures(getFeatureCollection(response)));

    // invalid requests
    response = tryBoxEndpoint(List.of("41.0", "slay", "lana", "taylor"));
    Assertions.assertEquals(ERROR_BAD_JSON, response.responseCode());
    Assertions.assertEquals(response.errorSpecs(), INVALID_LAT_LNG);

    response = tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=41&minLng=-71&maxLat=42");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(MISSING_PARAMS, response.errorSpecs());

    response =
        tryServerRequest(
            BOUNDING_BOX_ENDPOINT + "?minLat=41&minLng=-71&maxLat=42&maxLng=180&foo=bar");
    Assertions.assertEquals(ERROR_BAD_JSON, response.responseCode());
    Assertions.assertEquals(UNKNOWN_PARAMS, response.errorSpecs());

    response = tryServerRequest(BOUNDING_BOX_ENDPOINT + "?");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(NO_PARAMS, response.errorSpecs());

    response =
        tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=41&minLng=-71&maxLat=42&maxLng=-72");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(MIN_GREATER_THAN_MAX, response.errorSpecs());

    response =
        tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=41&minLng=-181&maxLat=42&maxLng=180");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(COORD_OUT_OF_BOUNDS, response.errorSpecs());

    response =
        tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=41&minLng=-71&maxLat=42&maxLng=181");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(COORD_OUT_OF_BOUNDS, response.errorSpecs());

    response =
        tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=-92&minLng=-71&maxLat=42&maxLng=180");
    Assertions.assertEquals(response.responseCode(), "error_bad_request");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(COORD_OUT_OF_BOUNDS, response.errorSpecs());

    response =
        tryServerRequest(BOUNDING_BOX_ENDPOINT + "?minLat=42&minLng=-71&maxLat=92&maxLng=180");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(COORD_OUT_OF_BOUNDS, response.errorSpecs());

    // datasource error
    this.teardown();
    Spark.get(
        "/" + BOUNDING_BOX_ENDPOINT,
        new JSONDataHandler("nonexistent.json", FeatureCollection.class, BoundingBoxProxy.class));
    response = tryBoxEndpoint(List.of(41.0, -71.0, 42.0, -70.0));
    Assertions.assertEquals(ERROR_DATASOURCE, response.responseCode());
    Assertions.assertEquals(
        String.format(DATA_LOAD_FAILURE, "nonexistent.json"), response.errorSpecs());

    this.teardown();
    Spark.get(
        "/" + BOUNDING_BOX_ENDPOINT,
        new JSONDataHandler(
            "anotherForGoodMeasure.json", FeatureCollection.class, BoundingBoxProxy.class));
    response = tryBoxEndpoint(List.of(41.0, -71.0, 42.0, -70.0));
    Assertions.assertEquals(ERROR_DATASOURCE, response.responseCode());
    Assertions.assertEquals(
        String.format(DATA_LOAD_FAILURE, "anotherForGoodMeasure.json"), response.errorSpecs());
  }

  /** This method tests the keyword endpoint of the server. */
  @Test
  public void testKeywordEndpoint() throws Exception {
    // basic valid request
    ServerResponse response = tryKeywordEndpoint("school");
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertTrue(checkHasFeatures(getFeatureCollection(response)));
    Thread.sleep(30000);
    Assertions.assertTrue(this.keywordHandler.checkInHistory("school"));

    // more valid requests, checking case insensitivity
    ServerResponse response1 = tryKeywordEndpoint("RED%20MOUNTAIN");
    Assertions.assertEquals(response1.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response1));
    Assertions.assertTrue(checkHasFeatures(getFeatureCollection(response1)));
    Thread.sleep(30000);
    Assertions.assertTrue(this.keywordHandler.checkInHistory("red mountain"));

    ServerResponse response2 = tryKeywordEndpoint("red%20mountain");
    Assertions.assertEquals(response2.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response2));
    Assertions.assertTrue(checkHasFeatures(getFeatureCollection(response2)));
    Assertions.assertEquals(response1, response2);

    // valid request with no results
    response =
        tryKeywordEndpoint(
            "lana del rey is so talented i love lana del rey god bless lana del rey"
                .replace(" ", "%20"));
    Assertions.assertEquals(response.responseCode(), SUCCESS_MESSAGE);
    Assertions.assertTrue(checkDataHasFeatureCollection(response));
    Assertions.assertFalse(checkHasFeatures(getFeatureCollection(response)));
    Thread.sleep(30000);
    Assertions.assertTrue(
        this.keywordHandler.checkInHistory(
            "lana del rey is so talented i love lana del rey god bless lana del rey"));

    // invalid requests
    response = tryKeywordEndpoint("school&foo=bar");
    Assertions.assertEquals(ERROR_BAD_JSON, response.responseCode());
    Assertions.assertEquals(UNKNOWN_PARAMS, response.errorSpecs());

    response = tryServerRequest(KEYWORD_ENDPOINT + "?");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(NO_PARAMS, response.errorSpecs());

    response = tryServerRequest(KEYWORD_ENDPOINT + "?keyword=");
    Assertions.assertEquals(ERROR_BAD_REQUEST, response.responseCode());
    Assertions.assertEquals(NO_KEYWORD, response.errorSpecs());

    // datasource errors
    this.teardown();
    Spark.get(
        "/" + KEYWORD_ENDPOINT,
        new JSONDataHandler("nonexistent.json", FeatureCollection.class, KeywordSearchProxy.class));
    response = tryKeywordEndpoint("school");
    Assertions.assertEquals(ERROR_DATASOURCE, response.responseCode());
    Assertions.assertEquals(
        String.format(DATA_LOAD_FAILURE, "nonexistent.json"), response.errorSpecs());

    this.teardown();
    Spark.get(
        "/" + KEYWORD_ENDPOINT,
        new JSONDataHandler(
            "anotherForGoodMeasure.json", FeatureCollection.class, KeywordSearchProxy.class));
    response = tryKeywordEndpoint("school");
    Assertions.assertEquals(ERROR_DATASOURCE, response.responseCode());
    Assertions.assertEquals(
        String.format(DATA_LOAD_FAILURE, "anotherForGoodMeasure.json"), response.errorSpecs());
  }
}
