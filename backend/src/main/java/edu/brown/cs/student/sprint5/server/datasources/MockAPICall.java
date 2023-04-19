package edu.brown.cs.student.sprint5.server.datasources;

import static edu.brown.cs.student.sprint5.server.datasources.JSONReader.fromJsonFile;

import java.io.IOException;

/** Class that mocks an API call by reading a JSON file and returning it as a response. */
public class MockAPICall implements APICall {

  /**
   * Makes a request to an API and returns a response.
   *
   * @param jsonFilePath the URL of the API
   * @param responseClass the class of the response object
   * @return the response object
   * @param <R> the type of the response object
   * @throws InterruptedException if the thread is interrupted
   * @throws IOException if there is an error reading the request or response
   */
  @Override
  public <R> R makeRequest(String jsonFilePath, Class<R> responseClass)
      throws InterruptedException, IOException {
    return fromJsonFile(jsonFilePath, responseClass);
  }
}
