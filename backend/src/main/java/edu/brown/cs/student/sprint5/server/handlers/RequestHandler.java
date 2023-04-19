package edu.brown.cs.student.sprint5.server.handlers;

import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import spark.Request;

/**
 * Interface for classes that handle requests, which provides generic functionality for checking a
 * request's validity.
 */
public interface RequestHandler {

  /**
   * Checks if the request contains only known parameters.
   *
   * @param request the request to check
   * @return true if the request contains only known parameters, false otherwise
   */
  boolean checkParamsKnown(Request request);

  /**
   * Handles a request if it contains only known parameters.
   *
   * @param request the request to handle
   * @return the server's response to the request
   */
  ServerResponse handleKnownParams(Request request) throws Exception;
}
