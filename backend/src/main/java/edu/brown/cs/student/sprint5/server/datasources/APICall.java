package edu.brown.cs.student.sprint5.server.datasources;

import java.io.IOException;

/** Interface for a class that can make a request to an API and return a response. */
public interface APICall {

  /**
   * Makes a request to an API and returns a response.
   *
   * @param url the URL of the API
   * @param responseClass the class of the response object
   * @param <R> the type of the response object
   * @return the response object
   * @throws InterruptedException if the thread is interrupted
   * @throws IOException if there is an error reading the request or response
   */
  <R> R makeRequest(String url, Class<R> responseClass) throws InterruptedException, IOException;
}
