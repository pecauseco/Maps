package edu.brown.cs.student.sprint5.server.handlers;

import static edu.brown.cs.student.sprint5.Constants.*;
import static edu.brown.cs.student.sprint5.server.datasources.JSONReader.fromJsonFile;

import edu.brown.cs.student.sprint5.server.proxies.Proxy;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.FeatureCollection;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class that generically handles requests for data from a JSON file. */
public class JSONDataHandler implements RequestHandler, Route {

  private Proxy proxy;
  private Set<String> knownParams;
  private FeatureCollection data;
  private boolean dataLoaded;
  private String dataPath;

  /**
   * Constructor for JSONDataHandler.
   *
   * @param path the path to the JSON file.
   * @param dataClass the Record class that the JSON file should be parsed into.
   * @param proxyClass the class of the proxy that specifies behavior needed to generate a
   *     particular response.
   * @throws Exception if the proxy class does not have a constructor that takes a dataClass object.
   */
  public JSONDataHandler(
      String path, Class<? extends Record> dataClass, Class<? extends Proxy> proxyClass)
      throws Exception {
    this.dataPath = path;
    try {
      this.data = fromJsonFile(path, dataClass);
      this.dataLoaded = true;
      this.proxy = proxyClass.getConstructor(dataClass).newInstance(this.data);
      this.knownParams = this.proxy.getKnownQueryParams();
    } catch (IOException e) {
      this.dataLoaded = false;
    }
  }

  /**
   * Called when a request is made to the endpoint.
   *
   * @param request the request object.
   * @param response the response object. Not used, but required by the Route interface.
   * @return the response to the request.
   * @throws Exception required by the Route interface.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, String[]> paramsMap = request.queryMap().toMap();
    Set<String> queryParams = request.queryParams();
    if (!this.dataLoaded) {
      return new ServerResponse(ERROR_DATASOURCE, String.format(DATA_LOAD_FAILURE, this.dataPath))
          .withParams(paramsMap)
          .serialize();
    } else if (checkParamsKnown(request)) {
      return handleKnownParams(request).withParams(paramsMap).serialize();
    } else {
      if (queryParams == null) {
        return new ServerResponse(ERROR_BAD_REQUEST, NO_PARAMS).withParams(paramsMap).serialize();
      } else if (queryParams.isEmpty()) {
        return new ServerResponse(ERROR_BAD_REQUEST, NO_PARAMS).withParams(paramsMap).serialize();
      } else if (!queryParams.containsAll(this.knownParams)) {
        return new ServerResponse(ERROR_BAD_REQUEST, MISSING_PARAMS)
            .withParams(paramsMap)
            .serialize();
      } else {
        HashMap<String, String> unknowns = new HashMap<>();
        for (String param : queryParams) {
          if (!this.knownParams.contains(param)) {
            unknowns.put(String.format(UNKNOWN_PARAM_MESSAGE, param), request.queryParams(param));
          }
        }
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put(UNKNOWN_PARAMS, unknowns);
        return new ServerResponse(ERROR_BAD_JSON, UNKNOWN_PARAMS, responseMap)
            .withParams(paramsMap)
            .serialize();
      }
    }
  }

  /**
   * Checks if the request contains all known parameters.
   *
   * @param request the request to check
   * @return true if the request contains all known parameters, false otherwise.
   */
  @Override
  public boolean checkParamsKnown(Request request) {
    return request.queryParams().containsAll(this.knownParams)
        && request.queryParams().size() == this.knownParams.size();
  }

  /**
   * Handles a request that contains all known parameters.
   *
   * @param request the request to handle
   * @return the response to the request
   * @throws Exception if the proxy throws an exception.
   */
  @Override
  public ServerResponse handleKnownParams(Request request) throws Exception {
    return this.proxy.getResponsePercolate(request);
  }

  /**
   * Checks if the request is cached in the proxy.
   *
   * @param paramsString the string representation of the request parameters.
   * @return true if the request is cached, false otherwise.
   */
  public boolean checkInHistory(String paramsString) {
    return this.proxy.cacheContains(paramsString);
  }
}
