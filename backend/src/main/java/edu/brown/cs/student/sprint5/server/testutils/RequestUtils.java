package edu.brown.cs.student.sprint5.server.testutils;

import static edu.brown.cs.student.sprint5.Constants.*;

import edu.brown.cs.student.sprint5.server.datasources.WebAPICall;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.io.IOException;
import java.util.List;
import spark.Spark;

public class RequestUtils {

  private static final WebAPICall CALLER = new WebAPICall();

  /**
   * This method is used to make a GET request to the server.
   *
   * @param endpointWithParams the portion of the URI that specifies the endpoint and query
   *     parameters
   * @return ServerResponse object containing the response from the server for the given request
   */
  public static ServerResponse tryServerRequest(String endpointWithParams)
      throws IOException, InterruptedException {
    String url = "http://localhost:" + Spark.port() + "/" + endpointWithParams;
    return CALLER.makeRequest(url, ServerResponse.class);
  }

  /**
   * This method is used to make calls to the keyword endpoint of the server.
   *
   * @param keyword the keyword to search for
   * @return ServerResponse object containing the response from the server for the given request
   */
  public static ServerResponse tryKeywordEndpoint(String keyword)
      throws IOException, InterruptedException {
    return tryServerRequest(KEYWORD_ENDPOINT + "?" + KEYWORD_PARAM + "=" + keyword);
  }

  /**
   * This method is used to make calls to the bounding box endpoint of the server.
   *
   * @param coords a list of coordinates in the form [minLat, minLng, maxLat, maxLng]
   * @return ServerResponse object containing the response from the server for the given request
   */
  public static ServerResponse tryBoxEndpoint(List<?> coords)
      throws IOException, InterruptedException {
    String endpointWithParams =
        BOUNDING_BOX_ENDPOINT
            + "?"
            + MIN_LAT_PARAM
            + "="
            + coords.get(0)
            + "&"
            + MIN_LNG_PARAM
            + "="
            + coords.get(1)
            + "&"
            + MAX_LAT_PARAM
            + "="
            + coords.get(2)
            + "&"
            + MAX_LNG_PARAM
            + "="
            + coords.get(3);
    return tryServerRequest(endpointWithParams);
  }

}
