package edu.brown.cs32.server;

import static edu.brown.cs.student.sprint5.Constants.*;
import static edu.brown.cs.student.sprint5.server.testutils.AssertionUtils.checkDataHasFeatureCollection;
import static edu.brown.cs.student.sprint5.server.testutils.AssertionUtils.getFeatureCollection;
import static edu.brown.cs.student.sprint5.server.testutils.FuzzUtils.generateRandomBoundingBox;
import static edu.brown.cs.student.sprint5.server.testutils.RequestUtils.tryServerRequest;
import static spark.Spark.after;

import edu.brown.cs.student.sprint5.server.handlers.JSONDataHandler;
import edu.brown.cs.student.sprint5.server.proxies.BoundingBoxProxy;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.*;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.*;
import spark.Spark;

/** Integration tests for the program, including random fuzzing. */
public class TestFuzz {

  private static final int NUM_FUZZ_ITERATIONS = 1000;

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
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * This method is run before each test in our integration test suite. It clears the state of the
   * server and re-sets up the server for each test by instantiating proxies and handlers and adding
   * them to the server at their respective endpoints
   */
  @BeforeEach
  public void setup() {
    try {
      Spark.get(
          "/" + BOUNDING_BOX_ENDPOINT,
          new JSONDataHandler(REDLINING_PATH, FeatureCollection.class, BoundingBoxProxy.class));
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
    Spark.awaitStop();
  }

  /**
   * This method performs a random fuzzing test on the server. It sends a random request to the
   * bounding box endpoint and checks that the response is valid.
   */
  @Test
  public void testBoundingBoxFuzz() throws IOException, InterruptedException {
    for (int i = 0; i < NUM_FUZZ_ITERATIONS; i++) {
      Map<String, String> boxParams = generateRandomBoundingBox();
      // generate endpointWithParams using boxParams
      StringBuilder endpointWithParams = new StringBuilder(BOUNDING_BOX_ENDPOINT + "?");
      for (String key : boxParams.keySet()) {
        endpointWithParams.append(key).append("=").append(boxParams.get(key)).append("&");
      }
      // remove final &
      endpointWithParams.deleteCharAt(endpointWithParams.length() - 1);
      String endpoint = endpointWithParams.toString();
      ServerResponse response = tryServerRequest(endpoint);
      if (response.responseCode().equals(SUCCESS_MESSAGE)) {
        // check that all features are within the bounding box
        Assertions.assertTrue(checkDataHasFeatureCollection(response));
        FeatureCollection collection = getFeatureCollection(response);
        assert collection != null;
        assert collection.features() != null;
        // convert boxParams to doubles
        double minLat = Double.parseDouble(boxParams.get("minLat"));
        double maxLat = Double.parseDouble(boxParams.get("maxLat"));
        double minLng = Double.parseDouble(boxParams.get("minLng"));
        double maxLng = Double.parseDouble(boxParams.get("maxLng"));
        for (Feature feature : collection.features()) {
          Assertions.assertTrue(feature.geometry().checkBounds(minLat, maxLat, minLng, maxLng));
        }
      } else {
        if (response.errorSpecs().equals(NO_PARAMS)) {
          if (boxParams.size() != 0) {
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          }
        } else if (response.errorSpecs().equals(UNKNOWN_PARAMS)) {
          // check that all params are minLat, maxLat, minLng, maxLng
          boolean allValidParams = true;
          for (String key : boxParams.keySet()) {
            // if there is no key in boxParams that is not minLat, maxLat, minLng, or maxLng, then
            // fail
            if (key.equals("minLat")
                || key.equals("maxLat")
                || key.equals("minLng")
                || key.equals("maxLng")) {
              continue;
            }
            allValidParams = false;
          }
          if (allValidParams) {
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          }
        } else if (response.errorSpecs().equals(MISSING_PARAMS)) {
          if (boxParams.containsKey("minLat")
              && boxParams.containsKey("maxLat")
              && boxParams.containsKey("minLng")
              && boxParams.containsKey("maxLng")) {
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          }
        } else if (response.errorSpecs().equals(COORD_OUT_OF_BOUNDS)) {
          // check if params are in the correct range (to contest coord_out_of_bounds)
          double minLat = Double.parseDouble(boxParams.get("minLat"));
          double maxLat = Double.parseDouble(boxParams.get("maxLat"));
          double minLng = Double.parseDouble(boxParams.get("minLng"));
          double maxLng = Double.parseDouble(boxParams.get("maxLng"));
          if (minLat >= -90
              && minLat <= 90
              && maxLat >= -90
              && maxLat <= 90
              && minLng >= -180
              && minLng <= 180
              && maxLng >= -180
              && maxLng <= 180) {
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          }
        } else if (response.errorSpecs().equals(INVALID_LAT_LNG)) {
          try {
            Double.parseDouble(boxParams.get("minLat"));
            Double.parseDouble(boxParams.get("maxLat"));
            Double.parseDouble(boxParams.get("minLng"));
            Double.parseDouble(boxParams.get("maxLng"));
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          } catch (NumberFormatException e) {
            // do nothing, this is the expected behavior
          }
        } else if (response.errorSpecs().equals(MIN_GREATER_THAN_MAX)) {
          // check that minLat <= maxLat and minLng <= maxLng
          double minLat = Double.parseDouble(boxParams.get("minLat"));
          double maxLat = Double.parseDouble(boxParams.get("maxLat"));
          double minLng = Double.parseDouble(boxParams.get("minLng"));
          double maxLng = Double.parseDouble(boxParams.get("maxLng"));
          if (minLat <= maxLat && minLng <= maxLng) {
            System.out.println(boxParams);
            System.out.println(response);
            Assertions.fail();
          }
        } else {
          System.out.println("unknown error");
          System.out.println(boxParams);
          System.out.println(response);
          Assertions.fail();
        }
      }
    }
  }
}
