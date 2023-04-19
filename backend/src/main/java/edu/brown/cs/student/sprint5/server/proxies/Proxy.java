package edu.brown.cs.student.sprint5.server.proxies;

import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.util.Set;
import spark.Request;

/** Interface for a proxy. */
public interface Proxy {

  /**
   * Gets the response for a given request by sending a request to the cache.
   *
   * @param request The request to get the response for.
   * @return The response for the given request.
   * @throws Exception If there is an error in retrieving from the cache.
   */
  ServerResponse getResponsePercolate(Request request) throws Exception;

  /**
   * Returns true if the cache contains the response for the request dictated by the query
   * parameters passed in params. Used for testing purposes.
   *
   * @param paramsString the query parameters of the request to check for
   * @returns true if the cache contains the response for the request dictated, false otherwise
   */
  boolean cacheContains(String paramsString);

  /**
   * Gets the query parameters for the endpoint this proxy is used for.
   *
   * @return The query parameters of the endpoint this proxy is used for.
   */
  Set<String> getKnownQueryParams();
}
